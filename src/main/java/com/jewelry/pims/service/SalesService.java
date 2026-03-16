package com.jewelry.pims.service;

import com.jewelry.pims.dto.sales.SalesDtos;

import java.util.List;

public interface SalesService {

    SalesDtos.SaleOrderView createOrder(SalesDtos.SaleOrderCreateRequest request);

    List<SalesDtos.SaleOrderView> listOrders();

    SalesDtos.SaleOrderView getOrder(Long id);

    void approve(Long id);
}
