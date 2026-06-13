package com.miaoqichao.api.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miaoqichao.common.api.CommonResult;
import com.miaoqichao.core.service.BatchService;
import com.miaoqichao.dao.entity.Batch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Tag(name = "批次管理接口 (学校用户)")
@RestController
@RequestMapping("/api/school/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @Operation(summary = "获取当前批次信息")
    @GetMapping("/info")
    public CommonResult<Batch> getBatchInfo() {
        // 假设获取最新创建的批次作为当前活动批次
        Batch batch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                .orderByDesc(Batch::getCreateTime)
                .last("LIMIT 1"));
        return CommonResult.success(batch);
    }
    
    @Operation(summary = "获取活动批次状态")
    @GetMapping("/active")
    public CommonResult<Batch> getActiveBatchStatus() {
        return getBatchInfo();
    }

    @Operation(summary = "创建新批次")
    @PostMapping
    @SaCheckRole("school")
    public CommonResult<Boolean> createBatch(@RequestBody Batch batch) {
        if (batch.getBatchName() == null) {
            batch.setBatchName(batch.getYear() + "学年寒衣补助");
        }
        if (batch.getFreshmanGrade() == null) {
            batch.setFreshmanGrade(Integer.parseInt(batch.getYear().substring(0, 4)));
        }
        if (batch.getApplyStartTime() == null) {
            batch.setApplyStartTime(new Date());
        }
        if (batch.getSelectStartTime() == null) {
            batch.setSelectStartTime(new Date());
        }
        batch.setStatus(1);
        batch.setCreateTime(new Date());
        batch.setUpdateTime(new Date());
        boolean success = batchService.save(batch);
        return CommonResult.success(success);
    }

    @Operation(summary = "修改延长时间")
    @PutMapping("/time")
    @SaCheckRole("school")
    public CommonResult<Boolean> updateBatchTime(@RequestBody Batch updateBatch) {
        Batch currentBatch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                .orderByDesc(Batch::getCreateTime)
                .last("LIMIT 1"));
        if (currentBatch == null) {
            return CommonResult.failed("当前无活动批次");
        }
        
        if (updateBatch.getApplyEndTime() != null) {
            currentBatch.setApplyEndTime(updateBatch.getApplyEndTime());
        }
        if (updateBatch.getSelectEndTime() != null) {
            currentBatch.setSelectEndTime(updateBatch.getSelectEndTime());
        }
        currentBatch.setUpdateTime(new Date());
        boolean success = batchService.updateById(currentBatch);
        return CommonResult.success(success);
    }
}
