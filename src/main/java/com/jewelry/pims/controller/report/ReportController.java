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
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @Permission("report:dashboard:view")
    public ApiResponse<InventoryDtos.DashboardView> dashboard() {
        return ApiResponse.ok(reportService.dashboard());
    }

    @GetMapping("/abc")
    @Permission("report:abc:view")
    public ApiResponse<List<InventoryDtos.AbcItemView>> abc() {
        return ApiResponse.ok(reportService.abcAnalysis());
    }

    @GetMapping("/turnover")
    @Permission("report:turnover:view")
    public ApiResponse<List<InventoryDtos.TurnoverView>> turnover() {
        return ApiResponse.ok(reportService.turnoverAnalysis());
    }
}
