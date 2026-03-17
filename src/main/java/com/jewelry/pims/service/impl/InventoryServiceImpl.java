package com.jewelry.pims.service.impl;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.common.DocumentStatus;
import com.jewelry.pims.common.InventoryTxnType;
import com.jewelry.pims.common.NoGenerator;
import com.jewelry.pims.common.SourceType;
import com.jewelry.pims.common.TraceType;
import com.jewelry.pims.domain.BusinessEntities;
import com.jewelry.pims.domain.MasterEntities;
import com.jewelry.pims.dto.inventory.InventoryDtos;
import com.jewelry.pims.mapper.InventoryMapper;
import com.jewelry.pims.mapper.MasterDataMapper;
import com.jewelry.pims.security.AuthContext;
import com.jewelry.pims.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
/**
 * 盘点与溯源业务实现。
 */
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final MasterDataMapper masterDataMapper;

    /**
     * 创建盘点单及其明细。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessEntities.StockCheckOrder createStockCheck(InventoryDtos.StockCheckCreateRequest request) {
        BusinessEntities.StockCheckOrder order = new BusinessEntities.StockCheckOrder();
        order.setCheckNo(NoGenerator.generate("CHK"));
        order.setWarehouseId(request.getWarehouseId());
        order.setStatus(DocumentStatus.DRAFT.name());
        order.setRemark(request.getRemark());
        order.setCreatedBy(AuthContext.userId());
        inventoryMapper.insertCheckOrder(order);

        for (InventoryDtos.StockCheckItemRequest requestItem : request.getItems()) {
            MasterEntities.Stock stock = masterDataMapper.findStockById(requestItem.getStockId());
            if (stock == null) {
                throw new BusinessException("盘点库存记录不存在");
            }
            BusinessEntities.StockCheckItem item = new BusinessEntities.StockCheckItem();
            item.setCheckOrderId(order.getId());
            item.setStockId(stock.getId());
            item.setSystemQuantity(stock.getQuantity());
            item.setActualQuantity(requestItem.getActualQuantity());
            item.setDiffQuantity(requestItem.getActualQuantity() - stock.getQuantity());
            inventoryMapper.insertCheckItem(item);
        }
        writeTrace(TraceType.CHECK_CREATED, SourceType.STOCK_CHECK, order.getCheckNo(), null, null, null, "盘点单创建");
        return order;
    }

    /**
     * 查询盘点单列表。
     */
    @Override
    public List<BusinessEntities.StockCheckOrder> listStockChecks() {
        return inventoryMapper.listCheckOrders();
    }

    /**
     * 审核盘点单并写入调整流水。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveStockCheck(Long id) {
        BusinessEntities.StockCheckOrder order = inventoryMapper.findCheckOrderById(id);
        if (order == null) {
            throw new BusinessException("盘点单不存在");
        }
        if (!DocumentStatus.DRAFT.name().equals(order.getStatus())) {
            throw new BusinessException("盘点单状态错误");
        }
        List<BusinessEntities.StockCheckItem> items = inventoryMapper.listCheckItems(id);
        for (BusinessEntities.StockCheckItem item : items) {
            MasterEntities.Stock stock = masterDataMapper.findStockById(item.getStockId());
            if (stock == null) {
                throw new BusinessException("盘点库存记录不存在");
            }
            int afterQuantity = item.getActualQuantity();
            int diffQuantity = afterQuantity - stock.getQuantity();
            BigDecimal avgWeight = BigDecimal.ZERO;
            if (stock.getQuantity() != null && stock.getQuantity() > 0 && stock.getTotalWeight() != null) {
                avgWeight = stock.getTotalWeight().divide(BigDecimal.valueOf(stock.getQuantity()), 4, RoundingMode.HALF_UP);
            }
            stock.setQuantity(afterQuantity);
            stock.setTotalWeight(avgWeight.multiply(BigDecimal.valueOf(afterQuantity)));
            masterDataMapper.updateStock(stock);

            BusinessEntities.InventoryTxn txn = new BusinessEntities.InventoryTxn();
            txn.setTxnNo(NoGenerator.generate("TXN"));
            txn.setTxnType(InventoryTxnType.CHECK_ADJUST.name());
            txn.setWarehouseId(stock.getWarehouseId());
            txn.setProductId(stock.getProductId());
            txn.setRelatedOrderId(order.getId());
            txn.setRelatedOrderNo(order.getCheckNo());
            txn.setRelatedOrderType("STOCK_CHECK");
            txn.setBatchNo(stock.getBatchNo());
            txn.setCertificateNo(stock.getCertificateNo());
            txn.setQuantityDelta(diffQuantity);
            txn.setQuantityAfter(afterQuantity);
            txn.setWeightDelta(avgWeight.multiply(BigDecimal.valueOf(diffQuantity)));
            txn.setUnitCost(stock.getAvgCostPrice());
            txn.setRemark("盘点调整");
            txn.setCreatedBy(AuthContext.userId());
            inventoryMapper.insertTxn(txn);

            writeTrace(TraceType.CHECK_ADJUST, SourceType.STOCK_CHECK, order.getCheckNo(), stock.getProductId(),
                    stock.getBatchNo(), stock.getCertificateNo(), "盘点调整，差异数量=" + diffQuantity);
        }
        order.setStatus(DocumentStatus.APPROVED.name());
        order.setApprovedBy(AuthContext.userId());
        order.setApprovedAt(LocalDateTime.now());
        inventoryMapper.updateCheckOrder(order);
    }

    /**
     * 查询指定关键字的溯源记录。
     */
    @Override
    public List<InventoryDtos.TraceView> trace(String keyword) {
        return inventoryMapper.queryTrace(keyword);
    }

    private void writeTrace(TraceType traceType, SourceType sourceType, String sourceNo,
                            Long productId, String batchNo, String certificateNo, String content) {
        BusinessEntities.TraceLog traceLog = new BusinessEntities.TraceLog();
        traceLog.setTraceType(traceType.name());
        traceLog.setSourceType(sourceType.name());
        traceLog.setSourceNo(sourceNo);
        traceLog.setProductId(productId);
        traceLog.setBatchNo(batchNo);
        traceLog.setCertificateNo(certificateNo);
        traceLog.setContent(content);
        traceLog.setCreatedBy(AuthContext.userId());
        inventoryMapper.insertTrace(traceLog);
    }
}
