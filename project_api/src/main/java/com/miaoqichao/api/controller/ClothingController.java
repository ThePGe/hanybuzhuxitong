package com.miaoqichao.api.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miaoqichao.common.api.CommonResult;
import com.miaoqichao.core.dto.selection.AdminUpdateSelectionReq;
import com.miaoqichao.core.service.BatchService;
import com.miaoqichao.core.service.ClothingService;
import com.miaoqichao.core.service.SelectionService;
import com.miaoqichao.dao.entity.Batch;
import com.miaoqichao.dao.entity.Clothing;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "服装款式管理接口")
@RestController
@RequestMapping("/api/school/clothing")
@RequiredArgsConstructor
public class ClothingController {

    private final ClothingService clothingService;
    private final SelectionService selectionService;
    private final BatchService batchService;

    @Operation(summary = "获取服装款式列表(可按性别过滤)")
    @GetMapping("/list")
    @SaCheckLogin
    public CommonResult<List<Clothing>> getClothingList(@RequestParam(value = "gender", required = false) String gender) {
        LambdaQueryWrapper<Clothing> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(gender)) {
            queryWrapper.eq(Clothing::getGenderLimit, "M".equals(gender) ? 1 : 2);
        }
        List<Clothing> list = clothingService.list(queryWrapper);
        return CommonResult.success(list);
    }

    @Operation(summary = "新增服装款式")
    @PostMapping
    @SaCheckRole("school")
    public CommonResult<Boolean> createClothing(@RequestBody Clothing clothing) {
        if (clothing.getBatchId() == null) {
            Batch currentBatch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                    .orderByDesc(Batch::getCreateTime)
                    .last("LIMIT 1"));
            if (currentBatch != null) {
                clothing.setBatchId(currentBatch.getId());
            } else {
                return CommonResult.failed("当前无活动批次，无法创建款式");
            }
        }
        clothing.setCreateTime(new Date());
        clothing.setUpdateTime(new Date());
        boolean success = clothingService.save(clothing);
        return CommonResult.success(success);
    }
    
    @Operation(summary = "更新服装款式")
    @PutMapping
    @SaCheckRole("school")
    public CommonResult<Boolean> updateClothing(@RequestBody Clothing clothing) {
        clothing.setUpdateTime(new Date());
        boolean success = clothingService.updateById(clothing);
        return CommonResult.success(success);
    }
    
    @Operation(summary = "删除服装款式")
    @DeleteMapping("/{id}")
    @SaCheckRole("school")
    public CommonResult<Boolean> deleteClothing(@PathVariable Long id) {
        boolean success = clothingService.removeById(id);
        return CommonResult.success(success);
    }
    
    @Operation(summary = "管理员干预学生选款")
    @PostMapping("/selection/update")
    @SaCheckLogin
    @SaCheckRole("school")
    public CommonResult<Boolean> adminUpdateSelection(@RequestBody AdminUpdateSelectionReq req) {
        selectionService.adminUpdateSelection(req);
        return CommonResult.success(true);
    }
}

