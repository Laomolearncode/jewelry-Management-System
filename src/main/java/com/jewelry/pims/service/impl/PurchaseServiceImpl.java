package com.jewelry.pims.service.impl;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.common.DocumentStatus;
import com.jewelry.pims.common.InventoryTxnType;
import com.jewelry.pims.common.NoGenerator;
import com.jewelry.pims.common.PageResult;
import com.jewelry.pims.common.PageUtils;
import com.jewelry.pims.common.SourceType;
import com.jewelry.pims.common.TraceType;
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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
/**
 * 采购业务实现。
 */
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseMapper purchaseMapper;
    private final MasterDataMapper masterDataMapper;
    private final InventoryMapper inventoryMapper;

    /**
     * 创建采购单和采购明细。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
        order.setStatus(DocumentStatus.DRAFT.name());
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
        writeTrace(TraceType.PURCHASE_CREATED, SourceType.PURCHASE_ORDER, order.getOrderNo(), null, null, null, "采购单创建");
        return getOrder(order.getId());
    }

    /**
     * 按条件分页查询采购单。
     */
    @Override
    public PageResult<PurchaseDtos.PurchaseOrderView> listOrders(PurchaseDtos.PurchaseOrderQuery query) {
        PurchaseDtos.PurchaseOrderQuery safeQuery = query == null ? new PurchaseDtos.PurchaseOrderQuery() : query;
        List<PurchaseDtos.PurchaseOrderView> filtered = purchaseMapper.listOrders().stream()
                .map(this::buildView)
                .filter(item -> !StringUtils.hasText(safeQuery.getOrderNo()) || item.getOrderNo().contains(safeQuery.getOrderNo()))
                .filter(item -> !StringUtils.hasText(safeQuery.getStatus()) || safeQuery.getStatus().equals(item.getStatus()))
                .filter(item -> !StringUtils.hasText(safeQuery.getCertificateNo()) || containsCertificate(item, safeQuery.getCertificateNo()))
                .filter(item -> safeQuery.getStartDate() == null || !item.getCreatedAt().toLocalDate().isBefore(safeQuery.getStartDate()))
                .filter(item -> safeQuery.getEndDate() == null || !item.getCreatedAt().toLocalDate().isAfter(safeQuery.getEndDate()))
                .collect(Collectors.toList());
        return PageUtils.paginate(filtered, safeQuery);
    }

    /**
     * 查询采购单详情。
     */
    @Override
    public PurchaseDtos.PurchaseOrderView getOrder(Long id) {
        BusinessEntities.PurchaseOrder order = purchaseMapper.findOrderById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        return buildView(order);
    }

    /**
     * 审核采购单。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        BusinessEntities.PurchaseOrder order = purchaseMapper.findOrderById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        if (!DocumentStatus.DRAFT.name().equals(order.getStatus())) {
            throw new BusinessException("仅草稿单可审核");
        }
        order.setStatus(DocumentStatus.APPROVED.name());
        order.setApprovedBy(AuthContext.userId());
        order.setApprovedAt(LocalDateTime.now());
        purchaseMapper.updateOrderStatus(order);
        writeTrace(TraceType.PURCHASE_APPROVED, SourceType.PURCHASE_ORDER, order.getOrderNo(), null, null, null, "采购单审核通过");
    }

    /**
     * 根据采购单执行入库并写入库存流水。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockIn(Long id) {
        BusinessEntities.PurchaseOrder order = purchaseMapper.findOrderById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        if (!DocumentStatus.APPROVED.name().equals(order.getStatus())) {
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
            txn.setTxnType(InventoryTxnType.PURCHASE_IN.name());
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

            writeTrace(TraceType.PURCHASE_IN, SourceType.PURCHASE_ORDER, order.getOrderNo(), item.getProductId(), item.getBatchNo(), item.getCertificateNo(),
                    "采购入库，数量=" + item.getQuantity());
        }
        order.setStatus(DocumentStatus.STOCKED.name());
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

    private boolean containsCertificate(PurchaseDtos.PurchaseOrderView view, String certificateNo) {
        return view.getItems() != null && view.getItems().stream()
                .anyMatch(item -> certificateNo.equals(item.getCertificateNo()));
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
