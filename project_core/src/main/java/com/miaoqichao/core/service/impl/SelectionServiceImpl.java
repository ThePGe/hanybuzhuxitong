package com.miaoqichao.core.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miaoqichao.common.exception.BusinessException;
import com.miaoqichao.core.dto.selection.AdminUpdateSelectionReq;
import com.miaoqichao.core.dto.selection.ClothingVO;
import com.miaoqichao.core.dto.selection.SelectionSubmitReq;
import com.miaoqichao.core.service.*;
import com.miaoqichao.dao.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelectionServiceImpl implements SelectionService {

    private final ClothingService clothingService;
    private final SkuService skuService;
    private final UserService userService;
    private final BatchService batchService;
    private final SubsidyApplyService subsidyApplyService;

    @Override
    public List<ClothingVO> getSelectionClothingList() {
        long userId = StpUtil.getLoginIdAsLong();
        User student = userService.getById(userId);
        
        LambdaQueryWrapper<Clothing> wrapper = new LambdaQueryWrapper<>();
        if (student.getGender() != null) {
            wrapper.eq(Clothing::getGenderLimit, student.getGender());
        }
        
        List<Clothing> clothings = clothingService.list(wrapper);
        return clothings.stream().map(clothing -> {
            ClothingVO vo = new ClothingVO();
            BeanUtils.copyProperties(clothing, vo);
            vo.setGender(clothing.getGenderLimit() == 1 ? "M" : "F");
            
            List<Sku> skus = skuService.list(new LambdaQueryWrapper<Sku>()
                    .eq(Sku::getClothingId, clothing.getId()));
            vo.setSkus(skus);
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void submitSelection(SelectionSubmitReq req) {
        long userId = StpUtil.getLoginIdAsLong();
        User student = userService.getById(userId);
        
        Batch batch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                .orderByDesc(Batch::getCreateTime)
                .last("LIMIT 1"));
        if (batch == null) throw new BusinessException("当前无活动批次");

        if (new Date().after(batch.getSelectEndTime())) {
            throw new BusinessException("选款时间已截止");
        }

        SubsidyApply apply = subsidyApplyService.getByStudentIdAndBatchId(userId, batch.getId());
        if (apply == null) {
            throw new BusinessException("您还未提交申请，暂无选款资格");
        }
        if (apply.getFinalStatus() != null && apply.getFinalStatus() == -1) {
            throw new BusinessException("您的申请已被拒绝，暂无选款资格");
        }
        if (apply.getFinalStatus() == null || apply.getFinalStatus() != 3) {
            throw new BusinessException("您还未通过学校终审，暂无选款资格");
        }

        Clothing clothing = clothingService.getById(req.getClothingId());
        if (clothing == null) throw new BusinessException("款式不存在");
        
        if (!clothing.getGenderLimit().equals(student.getGender())) {
            throw new BusinessException("只能选择符合自己性别的款式");
        }

        Sku sku = skuService.getById(req.getSkuId());
        if (sku == null || !sku.getClothingId().equals(req.getClothingId())) {
            throw new BusinessException("尺码选择有误");
        }

        apply.setClothingSkuId(sku.getId());
        apply.setSelectTime(new Date());
        apply.setUpdateTime(new Date());
        subsidyApplyService.updateById(apply);
    }

    @Override
    public void adminUpdateSelection(AdminUpdateSelectionReq req) {
        User student = userService.getUserByUsername(req.getStudentId());
        if (student == null) throw new BusinessException("学生不存在");

        Batch batch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                .orderByDesc(Batch::getCreateTime)
                .last("LIMIT 1"));
        if (batch == null) throw new BusinessException("当前无活动批次");
        
        SubsidyApply apply = subsidyApplyService.getByStudentIdAndBatchId(student.getId(), batch.getId());
        if (apply == null) {
            throw new BusinessException("该生还未提交申请，无法修改其选款");
        }
        if (apply.getFinalStatus() != null && apply.getFinalStatus() == -1) {
            throw new BusinessException("该生申请已被拒绝，无法修改其选款");
        }
        if (apply.getFinalStatus() == null || apply.getFinalStatus() != 3) {
            throw new BusinessException("该生未通过学校终审，无法修改其选款");
        }

        Clothing clothing = clothingService.getById(req.getClothingId());
        if (clothing == null) throw new BusinessException("款式不存在");

        Sku sku = skuService.getById(req.getSkuId());
        if (sku == null || !sku.getClothingId().equals(req.getClothingId())) {
            throw new BusinessException("尺码选择有误");
        }

        apply.setClothingSkuId(sku.getId());
        apply.setSelectTime(new Date());
        apply.setUpdateTime(new Date());
        subsidyApplyService.updateById(apply);
    }
}
