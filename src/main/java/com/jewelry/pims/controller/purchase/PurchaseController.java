package com.jewelry.pims.controller.purchase;

import com.jewelry.pims.common.ApiResponse;
import com.jewelry.pims.dto.purchase.PurchaseDtos;
import com.jewelry.pims.security.Permission;
import com.jewelry.pims.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @Permission("purchase:order:create")
    public ApiResponse<PurchaseDtos.PurchaseOrderView> create(@Valid @RequestBody PurchaseDtos.PurchaseOrderCreateRequest request) {
        return ApiResponse.ok("创建成功", purchaseService.createOrder(request));
    }

    @GetMapping
    @Permission("purchase:order:view")
    public ApiResponse<List<PurchaseDtos.PurchaseOrderView>> list() {
        return ApiResponse.ok(purchaseService.listOrders());
    }

    @GetMapping("/{id}")
    @Permission("purchase:order:view")
    public ApiResponse<PurchaseDtos.PurchaseOrderView> get(@PathVariable Long id) {
        return ApiResponse.ok(purchaseService.getOrder(id));
    }

    @PostMapping("/{id}/approve")
    @Permission("purchase:order:approve")
    public ApiResponse<Void> approve(@PathVariable Long id) {
        purchaseService.approve(id);
        return ApiResponse.ok("审核成功", null);
    }

    @PostMapping("/{id}/stock-in")
    @Permission("inventory:stockin:create")
    public ApiResponse<Void> stockIn(@PathVariable Long id) {
        purchaseService.stockIn(id);
        return ApiResponse.ok("入库成功", null);
    }
}
