package com.jewelry.pims.service;

import com.jewelry.pims.dto.purchase.PurchaseDtos;

import java.util.List;

public interface PurchaseService {

    PurchaseDtos.PurchaseOrderView createOrder(PurchaseDtos.PurchaseOrderCreateRequest request);

    List<PurchaseDtos.PurchaseOrderView> listOrders();

    PurchaseDtos.PurchaseOrderView getOrder(Long id);

    void approve(Long id);

    void stockIn(Long id);
}
