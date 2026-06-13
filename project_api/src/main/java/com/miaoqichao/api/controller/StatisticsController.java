package com.miaoqichao.api.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.miaoqichao.common.api.CommonResult;
import com.miaoqichao.core.dto.CollegeStatVO;
import com.miaoqichao.core.dto.RealtimeStatVO;
import com.miaoqichao.core.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "数据统计与导出接口 (学校用户)")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "实时统计各款式、男女、尺码的选择人数")
    @GetMapping("/realtime")
    @SaCheckRole("school")
    public CommonResult<List<RealtimeStatVO>> getRealtimeStatistics() {
        return CommonResult.success(statisticsService.getRealtimeStatistics());
    }

    @Operation(summary = "按学院维度聚合统计需求")
    @GetMapping("/college")
    @SaCheckRole("school")
    public CommonResult<List<CollegeStatVO>> getCollegeStatistics() {
        return CommonResult.success(statisticsService.getCollegeStatistics());
    }

    @Operation(summary = "导出 Excel (申请明细表 / 汇总结果表)")
    @GetMapping("/export")
    @SaCheckRole("school")
    public CommonResult<Map<String, String>> exportExcel(@RequestParam("type") String type) {
        return CommonResult.success(statisticsService.exportExcel(type));
    }
}
