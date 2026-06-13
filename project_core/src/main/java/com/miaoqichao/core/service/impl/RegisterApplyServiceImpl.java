package com.miaoqichao.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miaoqichao.common.exception.BusinessException;
import com.miaoqichao.core.dto.auth.RegisterApplyVO;
import com.miaoqichao.core.dto.auth.RegisterReq;
import com.miaoqichao.core.dto.auth.RegisterReviewReq;
import com.miaoqichao.core.service.RegisterApplyService;
import com.miaoqichao.core.service.UserService;
import com.miaoqichao.core.service.WhitelistService;
import com.miaoqichao.dao.entity.RegisterApply;
import com.miaoqichao.dao.entity.User;
import com.miaoqichao.dao.entity.Whitelist;
import com.miaoqichao.dao.mapper.RegisterApplyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterApplyServiceImpl extends ServiceImpl<RegisterApplyMapper, RegisterApply> implements RegisterApplyService {

    private final UserService userService;
    private final WhitelistService whitelistService;

    @Override
    public void register(RegisterReq req) {
        // 校验用户名是否已存在
        User existUser = userService.getUserByUsername(req.getUsername());
        if (existUser != null) {
            throw new BusinessException("该学号/工号已被注册");
        }

        // 校验是否已经在申请中
        LambdaQueryWrapper<RegisterApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RegisterApply::getUsername, req.getUsername())
                .eq(RegisterApply::getStatus, 0); // 待审核
        if (count(queryWrapper) > 0) {
            throw new BusinessException("您已提交过注册申请，正在审核中");
        }

        RegisterApply apply = new RegisterApply();
        BeanUtils.copyProperties(req, apply);
        apply.setStatus(0); // 待审核
        save(apply);
    }

    @Override
    public List<RegisterApplyVO> getPendingApplications() {
        LambdaQueryWrapper<RegisterApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RegisterApply::getStatus, 0) // 待审核
                .orderByDesc(RegisterApply::getCreateTime);
        
        List<RegisterApply> list = list(queryWrapper);
        return list.stream().map(apply -> {
            RegisterApplyVO vo = new RegisterApplyVO();
            BeanUtils.copyProperties(apply, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewApplication(RegisterReviewReq req) {
        RegisterApply apply = getById(req.getId());
        if (apply == null) {
            throw new BusinessException("申请记录不存在");
        }
        if (apply.getStatus() != 0) {
            throw new BusinessException("该申请已被处理");
        }

        if (req.getApprove()) {
            apply.setStatus(1); // 已通过
            
            // 创建用户
            User user = new User();
            BeanUtils.copyProperties(apply, user);
            user.setId(null); // 清除主键
            if (user.getRole() != null && user.getRole() == 3) {
                // 如果是学生，默认困难认定（演示用）
                user.setIsDifficulty(true);
                user.setDifficultyLevel("一般困难");
            }
            userService.save(user);

            // 加入白名单
            Whitelist whitelist = new Whitelist();
            whitelist.setUsername(apply.getUsername());
            whitelistService.save(whitelist);
        } else {
            apply.setStatus(2); // 已驳回
            apply.setRejectReason(req.getRejectReason());
        }
        
        updateById(apply);
    }
}