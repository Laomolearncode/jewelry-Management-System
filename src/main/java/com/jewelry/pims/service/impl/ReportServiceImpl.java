package com.jewelry.pims.service.impl;

import com.jewelry.pims.dto.inventory.InventoryDtos;
import com.jewelry.pims.mapper.InventoryMapper;
import com.jewelry.pims.mapper.MasterDataMapper;
import com.jewelry.pims.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final InventoryMapper inventoryMapper;
    private final MasterDataMapper masterDataMapper;

    @Override
    public InventoryDtos.DashboardView dashboard() {
        InventoryDtos.DashboardView view = new InventoryDtos.DashboardView();
        view.setProductCount(masterDataMapper.countProducts());
        view.setStockQuantity(inventoryMapper.sumStockQuantity());
        view.setStockAmount(inventoryMapper.sumStockAmount());
        view.setPurchaseAmount(inventoryMapper.sumPurchaseAmount());
        view.setSaleAmount(inventoryMapper.sumSaleAmount());
        view.setGrossProfit(inventoryMapper.sumGrossProfit());
        view.setLowStockCount(masterDataMapper.countLowStock());
        return view;
    }

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

    @Override
    public List<InventoryDtos.TurnoverView> turnoverAnalysis() {
        BigDecimal averageInventoryCost = inventoryMapper.averageInventoryCost();
        List<InventoryDtos.TurnoverView> result = new ArrayList<>();
        for (Map<String, Object> row : inventoryMapper.monthlySalesCost()) {
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
}
