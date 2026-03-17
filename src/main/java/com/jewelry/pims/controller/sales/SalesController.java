package com.jewelry.pims.controller.sales;

import com.jewelry.pims.common.ApiResponse;
import com.jewelry.pims.common.PageResult;
import com.jewelry.pims.dto.sales.SalesDtos;
import com.jewelry.pims.security.Permission;
import com.jewelry.pims.service.SalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
/**
 * 销售业务接口。
 */
public class SalesController {

    private final SalesService salesService;

    /**
     * 创建销售单。
     */
    @PostMapping
    @Permission("sales:order:create")
    public ApiResponse<SalesDtos.SaleOrderView> create(@Valid @RequestBody SalesDtos.SaleOrderCreateRequest request) {
        return ApiResponse.ok("创建成功", salesService.createOrder(request));
    }

    /**
     * 按条件分页查询销售单列表。
     */
    @GetMapping
    @Permission("sales:order:view")
    public ApiResponse<PageResult<SalesDtos.SaleOrderView>> list(SalesDtos.SaleOrderQuery query) {
        return ApiResponse.ok(salesService.listOrders(query));
    }

    /**
     * 查询销售单详情。
     */
    @GetMapping("/{id}")
    @Permission("sales:order:view")
    public ApiResponse<SalesDtos.SaleOrderView> get(@PathVariable Long id) {
        return ApiResponse.ok(salesService.getOrder(id));
    }

    /**
     * 审核销售单并执行出库。
     */
    @PostMapping("/{id}/approve")
    @Permission("sales:order:approve")
    public ApiResponse<Void> approve(@PathVariable Long id) {
        salesService.approve(id);
        return ApiResponse.ok("审核并出库成功", null);
    }
}
