package com.jewelry.pims.service;

import com.jewelry.pims.domain.BusinessEntities;
import com.jewelry.pims.dto.inventory.InventoryDtos;

import java.util.List;

public interface InventoryService {

    BusinessEntities.StockCheckOrder createStockCheck(InventoryDtos.StockCheckCreateRequest request);

    List<BusinessEntities.StockCheckOrder> listStockChecks();

    void approveStockCheck(Long id);

    List<InventoryDtos.TraceView> trace(String keyword);
}
