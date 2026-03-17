package com.jewelry.pims.controller.report;

import com.jewelry.pims.common.ApiResponse;
import com.jewelry.pims.dto.inventory.InventoryDtos;
import com.jewelry.pims.security.Permission;
import com.jewelry.pims.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
/**
 * 统计分析接口。
 */
public class ReportController {

    private final ReportService reportService;

    /**
     * 查询仪表盘汇总数据。
     */
    @GetMapping("/dashboard")
    @Permission("report:dashboard:view")
    public ApiResponse<InventoryDtos.DashboardView> dashboard(InventoryDtos.ReportQuery query) {
        return ApiResponse.ok(reportService.dashboard(query));
    }

    /**
     * 查询 ABC 分类分析结果。
     */
    @GetMapping("/abc")
    @Permission("report:abc:view")
    public ApiResponse<List<InventoryDtos.AbcItemView>> abc() {
        return ApiResponse.ok(reportService.abcAnalysis());
    }

    /**
     * 查询库存周转率分析结果。
     */
    @GetMapping("/turnover")
    @Permission("report:turnover:view")
    public ApiResponse<List<InventoryDtos.TurnoverView>> turnover(InventoryDtos.ReportQuery query) {
        return ApiResponse.ok(reportService.turnoverAnalysis(query));
    }
}
