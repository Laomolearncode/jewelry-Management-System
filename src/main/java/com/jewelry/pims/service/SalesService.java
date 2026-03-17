package com.jewelry.pims.service;

import com.jewelry.pims.common.PageResult;
import com.jewelry.pims.dto.sales.SalesDtos;

public interface SalesService {

    SalesDtos.SaleOrderView createOrder(SalesDtos.SaleOrderCreateRequest request);

    PageResult<SalesDtos.SaleOrderView> listOrders(SalesDtos.SaleOrderQuery query);

    SalesDtos.SaleOrderView getOrder(Long id);

    void approve(Long id);
}
