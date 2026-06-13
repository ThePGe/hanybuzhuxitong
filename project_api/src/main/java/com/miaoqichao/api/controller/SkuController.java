package com.miaoqichao.api.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miaoqichao.api.dto.ConfigSkuDTO;
import com.miaoqichao.common.api.CommonResult;
import com.miaoqichao.core.service.SkuService;
import com.miaoqichao.dao.entity.Sku;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "商品SKU管理接口")
@RestController
@RequestMapping("/api/school/clothing/sku")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @Operation(summary = "配置款式SKU")
    @PostMapping("/config")
    @SaCheckRole("school")
    @Transactional
    public CommonResult<Boolean> configClothingSku(@RequestBody ConfigSkuDTO configSkuDTO) {
        Long clothingId = configSkuDTO.getClothingId();
        List<Sku> skus = configSkuDTO.getSkus();
        
        // 删除旧的SKU
        skuService.remove(new LambdaQueryWrapper<Sku>().eq(Sku::getClothingId, clothingId));
        
        // 插入新的SKU
        if (skus != null && !skus.isEmpty()) {
            for (int i = 0; i < skus.size(); i++) {
                Sku sku = skus.get(i);
                sku.setId(null); // Ensure new insert
                sku.setClothingId(clothingId);
                // 自动生成商品编码：规则例如 男01款XL码，但如果没有提供，可以用通用规则 SKU-clothingId-size
                if (sku.getSkuCode() == null || sku.getSkuCode().isEmpty()) {
                    sku.setSkuCode("SKU-" + clothingId + "-" + sku.getSize());
                }
                sku.setCreateTime(new Date());
                sku.setUpdateTime(new Date());
            }
            skuService.saveBatch(skus);
        }
        
        return CommonResult.success(true);
    }
    
    @Operation(summary = "获取款式SKU列表")
    @GetMapping("/{clothingId}")
    public CommonResult<List<Sku>> getSkuList(@PathVariable Long clothingId) {
        List<Sku> list = skuService.list(new LambdaQueryWrapper<Sku>().eq(Sku::getClothingId, clothingId));
        return CommonResult.success(list);
    }
}
