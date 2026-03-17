package com.jewelry.pims.controller.inventory;

import com.jewelry.pims.common.ApiResponse;
import com.jewelry.pims.domain.BusinessEntities;
import com.jewelry.pims.dto.inventory.InventoryDtos;
import com.jewelry.pims.security.Permission;
import com.jewelry.pims.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
/**
 * 盘点与溯源接口。
 */
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * 创建盘点单。
     */
    @PostMapping("/checks")
    @Permission("inventory:check:create")
    public ApiResponse<BusinessEntities.StockCheckOrder> createCheck(@Valid @RequestBody InventoryDtos.StockCheckCreateRequest request) {
        return ApiResponse.ok("创建成功", inventoryService.createStockCheck(request));
    }

    /**
     * 查询盘点单列表。
     */
    @GetMapping("/checks")
    @Permission("inventory:check:view")
    public ApiResponse<List<BusinessEntities.StockCheckOrder>> listChecks() {
        return ApiResponse.ok(inventoryService.listStockChecks());
    }

    /**
     * 审核盘点单并执行库存调整。
     */
    @PostMapping("/checks/{id}/approve")
    @Permission("inventory:check:approve")
    public ApiResponse<Void> approveCheck(@PathVariable Long id) {
        inventoryService.approveStockCheck(id);
        return ApiResponse.ok("盘点审核成功", null);
    }

    /**
     * 按批次号或证书号查询溯源记录。
     */
    @GetMapping("/trace")
    @Permission("inventory:trace:view")
    public ApiResponse<List<InventoryDtos.TraceView>> trace(@RequestParam String keyword) {
        return ApiResponse.ok(inventoryService.trace(keyword));
    }
}
