package com.jewelry.pims.service;

import com.jewelry.pims.common.PageResult;
import com.jewelry.pims.dto.purchase.PurchaseDtos;

public interface PurchaseService {

    PurchaseDtos.PurchaseOrderView createOrder(PurchaseDtos.PurchaseOrderCreateRequest request);

    PageResult<PurchaseDtos.PurchaseOrderView> listOrders(PurchaseDtos.PurchaseOrderQuery query);

    PurchaseDtos.PurchaseOrderView getOrder(Long id);

    void approve(Long id);

    void stockIn(Long id);
}
