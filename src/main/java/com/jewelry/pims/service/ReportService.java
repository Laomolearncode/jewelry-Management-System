package com.jewelry.pims.service;

import com.jewelry.pims.dto.inventory.InventoryDtos;

import java.util.List;

public interface ReportService {

    InventoryDtos.DashboardView dashboard(InventoryDtos.ReportQuery query);

    List<InventoryDtos.AbcItemView> abcAnalysis();

    List<InventoryDtos.TurnoverView> turnoverAnalysis(InventoryDtos.ReportQuery query);
}
