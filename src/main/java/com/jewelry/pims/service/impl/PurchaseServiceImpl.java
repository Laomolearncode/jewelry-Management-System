package com.jewelry.pims.service.impl;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.common.NoGenerator;
import com.jewelry.pims.domain.BusinessEntities;
import com.jewelry.pims.domain.MasterEntities;
import com.jewelry.pims.dto.purchase.PurchaseDtos;
import com.jewelry.pims.mapper.InventoryMapper;
import com.jewelry.pims.mapper.MasterDataMapper;
import com.jewelry.pims.mapper.PurchaseMapper;
import com.jewelry.pims.security.AuthContext;
import com.jewelry.pims.service.PurchaseService;
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
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseMapper purchaseMapper;
    private final MasterDataMapper masterDataMapper;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public PurchaseDtos.PurchaseOrderView createOrder(PurchaseDtos.PurchaseOrderCreateRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseDtos.PurchaseItemRequest item : request.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BusinessException("采购数量必须大于0");
            }
            totalAmount = totalAmount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        BusinessEntities.PurchaseOrder order = new BusinessEntities.PurchaseOrder();
        order.setOrderNo(NoGenerator.generate("PO"));
        order.setSupplierId(request.getSupplierId());
        order.setStatus("DRAFT");
        order.setTotalAmount(totalAmount);
        order.setCreatedBy(AuthContext.userId());
        purchaseMapper.insertOrder(order);

        for (PurchaseDtos.PurchaseItemRequest requestItem : request.getItems()) {
            BusinessEntities.PurchaseOrderItem item = new BusinessEntities.PurchaseOrderItem();
            item.setOrderId(order.getId());
            item.setProductId(requestItem.getProductId());
            item.setWarehouseId(requestItem.getWarehouseId());
            item.setBatchNo(requestItem.getBatchNo());
            item.setCertificateNo(requestItem.getCertificateNo());
            item.setQuantity(requestItem.getQuantity());
            item.setWeight(requestItem.getWeight());
            item.setUnitPrice(requestItem.getUnitPrice());
            item.setSubtotalAmount(requestItem.getUnitPrice().multiply(BigDecimal.valueOf(requestItem.getQuantity())));
            purchaseMapper.insertItem(item);
        }
        return getOrder(order.getId());
    }

    @Override
    public List<PurchaseDtos.PurchaseOrderView> listOrders() {
        List<PurchaseDtos.PurchaseOrderView> result = new ArrayList<>();
        for (BusinessEntities.PurchaseOrder order : purchaseMapper.listOrders()) {
            result.add(buildView(order));
        }
        return result;
    }

    @Override
    public PurchaseDtos.PurchaseOrderView getOrder(Long id) {
        BusinessEntities.PurchaseOrder order = purchaseMapper.findOrderById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        return buildView(order);
    }

    @Override
    @Transactional
    public void approve(Long id) {
        BusinessEntities.PurchaseOrder order = purchaseMapper.findOrderById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        if (!"DRAFT".equals(order.getStatus())) {
            throw new BusinessException("仅草稿单可审核");
        }
        order.setStatus("APPROVED");
        order.setApprovedBy(AuthContext.userId());
        order.setApprovedAt(LocalDateTime.now());
        purchaseMapper.updateOrderStatus(order);
        writeTrace("PURCHASE_APPROVED", "PURCHASE_ORDER", order.getOrderNo(), null, null, null, "采购单审核通过");
    }

    @Override
    @Transactional
    public void stockIn(Long id) {
        BusinessEntities.PurchaseOrder order = purchaseMapper.findOrderById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        if (!"APPROVED".equals(order.getStatus())) {
            throw new BusinessException("仅审核通过的采购单可入库");
        }
        List<BusinessEntities.PurchaseOrderItem> items = purchaseMapper.listItemsByOrderId(id);
        for (BusinessEntities.PurchaseOrderItem item : items) {
            MasterEntities.Stock stock = masterDataMapper.findStockByKey(
                    item.getWarehouseId(), item.getProductId(), item.getBatchNo(), item.getCertificateNo()
            );
            int afterQuantity;
            if (stock == null) {
                stock = new MasterEntities.Stock();
                stock.setWarehouseId(item.getWarehouseId());
                stock.setProductId(item.getProductId());
                stock.setBatchNo(item.getBatchNo());
                stock.setCertificateNo(item.getCertificateNo());
                stock.setQuantity(item.getQuantity());
                stock.setTotalWeight(item.getWeight());
                stock.setAvgCostPrice(item.getUnitPrice());
                stock.setWarningThreshold(0);
                masterDataMapper.insertStock(stock);
                afterQuantity = item.getQuantity();
            } else {
                int oldQuantity = stock.getQuantity();
                BigDecimal oldWeight = stock.getTotalWeight() == null ? BigDecimal.ZERO : stock.getTotalWeight();
                BigDecimal newWeight = oldWeight.add(item.getWeight());
                int newQuantity = oldQuantity + item.getQuantity();
                BigDecimal oldAmount = stock.getAvgCostPrice().multiply(BigDecimal.valueOf(oldQuantity));
                BigDecimal newAmount = oldAmount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                stock.setQuantity(newQuantity);
                stock.setTotalWeight(newWeight);
                stock.setAvgCostPrice(newAmount.divide(BigDecimal.valueOf(newQuantity), 4, RoundingMode.HALF_UP));
                masterDataMapper.updateStock(stock);
                afterQuantity = newQuantity;
            }

            BusinessEntities.InventoryTxn txn = new BusinessEntities.InventoryTxn();
            txn.setTxnNo(NoGenerator.generate("TXN"));
            txn.setTxnType("PURCHASE_IN");
            txn.setWarehouseId(item.getWarehouseId());
            txn.setProductId(item.getProductId());
            txn.setRelatedOrderId(order.getId());
            txn.setRelatedOrderNo(order.getOrderNo());
            txn.setRelatedOrderType("PURCHASE_ORDER");
            txn.setBatchNo(item.getBatchNo());
            txn.setCertificateNo(item.getCertificateNo());
            txn.setQuantityDelta(item.getQuantity());
            txn.setQuantityAfter(afterQuantity);
            txn.setWeightDelta(item.getWeight());
            txn.setUnitCost(item.getUnitPrice());
            txn.setRemark("采购入库");
            txn.setCreatedBy(AuthContext.userId());
            inventoryMapper.insertTxn(txn);

            writeTrace("PURCHASE_IN", "PURCHASE_ORDER", order.getOrderNo(), item.getProductId(), item.getBatchNo(), item.getCertificateNo(),
                    "采购入库，数量=" + item.getQuantity());
        }
        order.setStatus("STOCKED");
        order.setApprovedBy(AuthContext.userId());
        order.setApprovedAt(LocalDateTime.now());
        purchaseMapper.updateOrderStatus(order);
    }

    private PurchaseDtos.PurchaseOrderView buildView(BusinessEntities.PurchaseOrder order) {
        PurchaseDtos.PurchaseOrderView view = new PurchaseDtos.PurchaseOrderView();
        view.setId(order.getId());
        view.setOrderNo(order.getOrderNo());
        view.setSupplierId(order.getSupplierId());
        view.setSupplierName(masterDataMapper.getSupplierName(order.getSupplierId()));
        view.setStatus(order.getStatus());
        view.setTotalAmount(order.getTotalAmount());
        view.setCreatedAt(order.getCreatedAt());
        List<PurchaseDtos.PurchaseItemView> itemViews = new ArrayList<>();
        for (BusinessEntities.PurchaseOrderItem item : purchaseMapper.listItemsByOrderId(order.getId())) {
            PurchaseDtos.PurchaseItemView itemView = new PurchaseDtos.PurchaseItemView();
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
