package com.jewelry.pims.service.impl;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.common.NoGenerator;
import com.jewelry.pims.domain.BusinessEntities;
import com.jewelry.pims.domain.MasterEntities;
import com.jewelry.pims.dto.sales.SalesDtos;
import com.jewelry.pims.mapper.InventoryMapper;
import com.jewelry.pims.mapper.MasterDataMapper;
import com.jewelry.pims.mapper.SalesMapper;
import com.jewelry.pims.security.AuthContext;
import com.jewelry.pims.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final SalesMapper salesMapper;
    private final MasterDataMapper masterDataMapper;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public SalesDtos.SaleOrderView createOrder(SalesDtos.SaleOrderCreateRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (SalesDtos.SaleItemRequest item : request.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BusinessException("销售数量必须大于0");
            }
            totalAmount = totalAmount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        BusinessEntities.SaleOrder order = new BusinessEntities.SaleOrder();
        order.setOrderNo(NoGenerator.generate("SO"));
        order.setCustomerId(request.getCustomerId());
        order.setStatus("DRAFT");
        order.setTotalAmount(totalAmount);
        order.setTotalCost(BigDecimal.ZERO);
        order.setCreatedBy(AuthContext.userId());
        salesMapper.insertOrder(order);

        for (SalesDtos.SaleItemRequest requestItem : request.getItems()) {
            BusinessEntities.SaleOrderItem item = new BusinessEntities.SaleOrderItem();
            item.setOrderId(order.getId());
            item.setProductId(requestItem.getProductId());
            item.setWarehouseId(requestItem.getWarehouseId());
            item.setBatchNo(requestItem.getBatchNo());
            item.setCertificateNo(requestItem.getCertificateNo());
            item.setQuantity(requestItem.getQuantity());
            item.setWeight(requestItem.getWeight());
            item.setUnitPrice(requestItem.getUnitPrice());
            item.setUnitCost(BigDecimal.ZERO);
            item.setSubtotalAmount(requestItem.getUnitPrice().multiply(BigDecimal.valueOf(requestItem.getQuantity())));
            salesMapper.insertItem(item);
        }
        return getOrder(order.getId());
    }

    @Override
    public List<SalesDtos.SaleOrderView> listOrders() {
        List<SalesDtos.SaleOrderView> result = new ArrayList<>();
        for (BusinessEntities.SaleOrder order : salesMapper.listOrders()) {
            result.add(buildView(order));
        }
        return result;
    }

    @Override
    public SalesDtos.SaleOrderView getOrder(Long id) {
        BusinessEntities.SaleOrder order = salesMapper.findOrderById(id);
        if (order == null) {
            throw new BusinessException("销售单不存在");
        }
        return buildView(order);
    }

    @Override
    @Transactional
    public void approve(Long id) {
        BusinessEntities.SaleOrder order = salesMapper.findOrderById(id);
        if (order == null) {
            throw new BusinessException("销售单不存在");
        }
        if (!"DRAFT".equals(order.getStatus())) {
            throw new BusinessException("仅草稿单可审核");
        }
        BigDecimal totalCost = BigDecimal.ZERO;
        for (BusinessEntities.SaleOrderItem item : salesMapper.listItemsByOrderId(id)) {
            MasterEntities.Stock stock = masterDataMapper.findStockByKey(
                    item.getWarehouseId(), item.getProductId(), item.getBatchNo(), item.getCertificateNo()
            );
            if (stock == null || stock.getQuantity() < item.getQuantity()) {
                throw new BusinessException("库存不足，商品ID=" + item.getProductId());
            }
            int afterQuantity = stock.getQuantity() - item.getQuantity();
            BigDecimal averageWeight = BigDecimal.ZERO;
            if (stock.getQuantity() != null && stock.getQuantity() > 0 && stock.getTotalWeight() != null) {
                averageWeight = stock.getTotalWeight().divide(BigDecimal.valueOf(stock.getQuantity()), 4, RoundingMode.HALF_UP);
            }
            stock.setQuantity(afterQuantity);
            BigDecimal currentWeight = stock.getTotalWeight() == null ? BigDecimal.ZERO : stock.getTotalWeight();
            stock.setTotalWeight(currentWeight.subtract(averageWeight.multiply(BigDecimal.valueOf(item.getQuantity()))));
            masterDataMapper.updateStock(stock);

            BigDecimal itemCost = stock.getAvgCostPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalCost = totalCost.add(itemCost);
            item.setUnitCost(stock.getAvgCostPrice());
            salesMapper.updateItemUnitCost(item);

            BusinessEntities.InventoryTxn txn = new BusinessEntities.InventoryTxn();
            txn.setTxnNo(NoGenerator.generate("TXN"));
            txn.setTxnType("SALE_OUT");
            txn.setWarehouseId(item.getWarehouseId());
            txn.setProductId(item.getProductId());
            txn.setRelatedOrderId(order.getId());
            txn.setRelatedOrderNo(order.getOrderNo());
            txn.setRelatedOrderType("SALE_ORDER");
            txn.setBatchNo(item.getBatchNo());
            txn.setCertificateNo(item.getCertificateNo());
            txn.setQuantityDelta(-item.getQuantity());
            txn.setQuantityAfter(afterQuantity);
            txn.setWeightDelta(item.getWeight().negate());
            txn.setUnitCost(stock.getAvgCostPrice());
            txn.setRemark("销售出库");
            txn.setCreatedBy(AuthContext.userId());
            inventoryMapper.insertTxn(txn);

            writeTrace("SALE_OUT", "SALE_ORDER", order.getOrderNo(), item.getProductId(), item.getBatchNo(), item.getCertificateNo(),
                    "销售出库，数量=" + item.getQuantity());
        }
        order.setStatus("APPROVED");
        order.setTotalCost(totalCost);
        order.setApprovedBy(AuthContext.userId());
        order.setApprovedAt(LocalDateTime.now());
        salesMapper.updateOrderStatus(order);
    }

    private SalesDtos.SaleOrderView buildView(BusinessEntities.SaleOrder order) {
        SalesDtos.SaleOrderView view = new SalesDtos.SaleOrderView();
        view.setId(order.getId());
        view.setOrderNo(order.getOrderNo());
        view.setCustomerId(order.getCustomerId());
        view.setCustomerName(masterDataMapper.getCustomerName(order.getCustomerId()));
        view.setStatus(order.getStatus());
        view.setTotalAmount(order.getTotalAmount());
        view.setTotalCost(order.getTotalCost());
        view.setCreatedAt(order.getCreatedAt());
        List<SalesDtos.SaleItemView> itemViews = new ArrayList<>();
        for (BusinessEntities.SaleOrderItem item : salesMapper.listItemsByOrderId(order.getId())) {
            SalesDtos.SaleItemView itemView = new SalesDtos.SaleItemView();
            itemView.setId(item.getId());
            itemView.setProductId(item.getProductId());
            itemView.setProductName(masterDataMapper.getProductName(item.getProductId()));
            itemView.setWarehouseId(item.getWarehouseId());
            itemView.setWarehouseName(masterDataMapper.getWarehouseName(item.getWarehouseId()));
            itemView.setBatchNo(item.getBatchNo());
            itemView.setCertificateNo(item.getCertificateNo());
            itemView.setQuantity(item.getQuantity());
            itemView.setWeight(item.getWeight());
            itemView.setUnitPrice(item.getUnitPrice());
            itemView.setUnitCost(item.getUnitCost());
            itemView.setSubtotalAmount(item.getSubtotalAmount());
            itemViews.add(itemView);
        }
        view.setItems(itemViews);
        return view;
    }

    private void writeTrace(String traceType, String sourceType, String sourceNo,
                            Long productId, String batchNo, String certificateNo, String content) {
        BusinessEntities.TraceLog traceLog = new BusinessEntities.TraceLog();
        traceLog.setTraceType(traceType);
        traceLog.setSourceType(sourceType);
        traceLog.setSourceNo(sourceNo);
        traceLog.setProductId(productId);
        traceLog.setBatchNo(batchNo);
        traceLog.setCertificateNo(certificateNo);
        traceLog.setContent(content);
        traceLog.setCreatedBy(AuthContext.userId());
        inventoryMapper.insertTrace(traceLog);
    }
}
