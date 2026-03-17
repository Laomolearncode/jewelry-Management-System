package com.jewelry.pims.service.impl;

import com.jewelry.pims.dto.inventory.InventoryDtos;
import com.jewelry.pims.mapper.InventoryMapper;
import com.jewelry.pims.mapper.MasterDataMapper;
import com.jewelry.pims.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
/**
 * 统计分析业务实现。
 */
public class ReportServiceImpl implements ReportService {

    private final InventoryMapper inventoryMapper;
    private final MasterDataMapper masterDataMapper;

    /**
     * 统计仪表盘汇总数据。
     */
    @Override
    public InventoryDtos.DashboardView dashboard(InventoryDtos.ReportQuery query) {
        String startTime = startTime(query);
        String endTime = endTime(query);
        InventoryDtos.DashboardView view = new InventoryDtos.DashboardView();
        view.setProductCount(masterDataMapper.countProducts());
        view.setStockQuantity(inventoryMapper.sumStockQuantity());
        view.setStockAmount(inventoryMapper.sumStockAmount());
        view.setPurchaseAmount(inventoryMapper.sumPurchaseAmount(startTime, endTime));
        view.setSaleAmount(inventoryMapper.sumSaleAmount(startTime, endTime));
        view.setGrossProfit(inventoryMapper.sumGrossProfit(startTime, endTime));
        view.setLowStockCount(masterDataMapper.countLowStock());
        return view;
    }

    /**
     * 计算库存 ABC 分类结果。
     */
    @Override
    public List<InventoryDtos.AbcItemView> abcAnalysis() {
        List<Map<String, Object>> source = inventoryMapper.abcSource();
        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> row : source) {
            total = total.add((BigDecimal) row.get("stockAmount"));
        }
        BigDecimal cumulative = BigDecimal.ZERO;
        List<InventoryDtos.AbcItemView> result = new ArrayList<>();
        for (Map<String, Object> row : source) {
            BigDecimal stockAmount = (BigDecimal) row.get("stockAmount");
            cumulative = cumulative.add(stockAmount);
            BigDecimal ratio = total.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : cumulative.divide(total, 4, RoundingMode.HALF_UP);
            String clazz = ratio.compareTo(new BigDecimal("0.70")) <= 0 ? "A"
                    : ratio.compareTo(new BigDecimal("0.90")) <= 0 ? "B" : "C";
            InventoryDtos.AbcItemView itemView = new InventoryDtos.AbcItemView();
            itemView.setProductId(((Number) row.get("productId")).longValue());
            itemView.setProductCode((String) row.get("productCode"));
            itemView.setProductName((String) row.get("productName"));
            itemView.setStockAmount(stockAmount);
            itemView.setAbcClass(clazz);
            result.add(itemView);
        }
        return result;
    }

    /**
     * 按时间范围统计库存周转率。
     */
    @Override
    public List<InventoryDtos.TurnoverView> turnoverAnalysis(InventoryDtos.ReportQuery query) {
        String startTime = startTime(query);
        String endTime = endTime(query);
        BigDecimal averageInventoryCost = inventoryMapper.averageInventoryCost();
        List<InventoryDtos.TurnoverView> result = new ArrayList<>();
        for (Map<String, Object> row : inventoryMapper.monthlySalesCost(startTime, endTime)) {
            InventoryDtos.TurnoverView view = new InventoryDtos.TurnoverView();
            BigDecimal salesCost = (BigDecimal) row.get("salesCost");
            view.setPeriod((String) row.get("period"));
            view.setSalesCost(salesCost);
            view.setAverageInventoryCost(averageInventoryCost);
            view.setTurnoverRate(averageInventoryCost.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : salesCost.divide(averageInventoryCost, 4, RoundingMode.HALF_UP));
            result.add(view);
        }
        return result;
    }

    private String startTime(InventoryDtos.ReportQuery query) {
        if (query == null || query.getStartDate() == null) {
            return null;
        }
        return query.getStartDate() + " 00:00:00";
    }

    private String endTime(InventoryDtos.ReportQuery query) {
        LocalDate endDate = query == null ? null : query.getEndDate();
        if (endDate == null) {
            return null;
        }
        return endDate + " 23:59:59";
    }
}
