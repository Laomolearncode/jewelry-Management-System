package com.jewelry.pims.service.impl;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.common.NoGenerator;
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
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final MasterDataMapper masterDataMapper;

    @Override
    @Transactional
    public BusinessEntities.StockCheckOrder createStockCheck(InventoryDtos.StockCheckCreateRequest request) {
        BusinessEntities.StockCheckOrder order = new BusinessEntities.StockCheckOrder();
        order.setCheckNo(NoGenerator.generate("CHK"));
        order.setWarehouseId(request.getWarehouseId());
        order.setStatus("DRAFT");
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
        return order;
    }

    @Override
    public List<BusinessEntities.StockCheckOrder> listStockChecks() {
        return inventoryMapper.listCheckOrders();
    }

    @Override
    @Transactional
    public void approveStockCheck(Long id) {
        BusinessEntities.StockCheckOrder order = inventoryMapper.findCheckOrderById(id);
        if (order == null) {
            throw new BusinessException("盘点单不存在");
        }
        if (!"DRAFT".equals(order.getStatus())) {
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
            txn.setTxnType("CHECK_ADJUST");
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

            BusinessEntities.TraceLog traceLog = new BusinessEntities.TraceLog();
            traceLog.setTraceType("CHECK_ADJUST");
            traceLog.setSourceType("STOCK_CHECK");
            traceLog.setSourceNo(order.getCheckNo());
            traceLog.setProductId(stock.getProductId());
            traceLog.setBatchNo(stock.getBatchNo());
            traceLog.setCertificateNo(stock.getCertificateNo());
            traceLog.setContent("盘点调整，差异数量=" + diffQuantity);
            traceLog.setCreatedBy(AuthContext.userId());
            inventoryMapper.insertTrace(traceLog);
        }
        order.setStatus("APPROVED");
        order.setApprovedBy(AuthContext.userId());
        order.setApprovedAt(LocalDateTime.now());
        inventoryMapper.updateCheckOrder(order);
    }

    @Override
    public List<InventoryDtos.TraceView> trace(String keyword) {
        return inventoryMapper.queryTrace(keyword);
    }
}
