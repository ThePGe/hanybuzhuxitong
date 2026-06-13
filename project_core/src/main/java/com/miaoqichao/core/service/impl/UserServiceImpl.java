package com.miaoqichao.core.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miaoqichao.common.exception.BusinessException;
import com.miaoqichao.core.dto.auth.LoginReq;
import com.miaoqichao.core.dto.auth.LoginVO;
import com.miaoqichao.core.dto.auth.UserInfoVO;
import com.miaoqichao.core.service.BatchService;
import com.miaoqichao.core.service.SubsidyApplyService;
import com.miaoqichao.core.service.UserService;
import com.miaoqichao.core.service.WhitelistService;
import com.miaoqichao.dao.entity.Batch;
import com.miaoqichao.dao.entity.SubsidyApply;
import com.miaoqichao.dao.entity.User;
import com.miaoqichao.dao.entity.Whitelist;
import com.miaoqichao.dao.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final WhitelistService whitelistService;
    
    @Lazy
    @Autowired
    private BatchService batchService;
    
    @Lazy
    @Autowired
    private SubsidyApplyService subsidyApplyService;

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public LoginVO login(LoginReq req) {
        User user = getUserByUsername(req.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 校验密码
        if (!user.getPassword().equals(req.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 学生登录校验是否在困难库
        if (user.getRole() != null && user.getRole() == 3) {
            if (user.getIsDifficulty() == null || !user.getIsDifficulty()) {
                throw new BusinessException("该生不在困难认定库，不符合补助申请资格");
            }
        }
        
        // 白名单校验，超管9447不受白名单限制
        if (!"9447".equals(user.getUsername())) {
            LambdaQueryWrapper<Whitelist> wlWrapper = new LambdaQueryWrapper<>();
            wlWrapper.eq(Whitelist::getUsername, user.getUsername());
            long count = whitelistService.count(wlWrapper);
            if (count == 0) {
                throw new BusinessException("用户 [" + user.getUsername() + "] 不在白名单内，禁止登录。请联系超管 9447 添加。");
            }
        }
        
        return doLogin(user);
    }

    @Override
    public LoginVO ssoLogin(LoginReq req) {
        User user = getUserByUsername(req.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 白名单校验，超管9447不受白名单限制
        if (!"9447".equals(user.getUsername())) {
            LambdaQueryWrapper<Whitelist> wlWrapper = new LambdaQueryWrapper<>();
            wlWrapper.eq(Whitelist::getUsername, user.getUsername());
            long count = whitelistService.count(wlWrapper);
            if (count == 0) {
                throw new BusinessException("用户 [" + user.getUsername() + "] 不在白名单内，禁止登录。请联系超管 9447 添加。");
            }
        }
        
        return doLogin(user);
    }

    private LoginVO doLogin(User user) {
        StpUtil.login(user.getId());
        
        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        
        String roleStr = getRoleStr(user);
        vo.setRole(roleStr);
        
        UserInfoVO userInfo = convertToUserInfo(user, roleStr);
        vo.setUserInfo(userInfo);
        
        return vo;
    }
    
    private String getRoleStr(User user) {
        if ("9447".equals(user.getUsername())) {
            return "superadmin";
        }
        if (user.getRole() == null) return "student";
        switch (user.getRole()) {
            case 0: return "school";
            case 1: return "college";
            case 2: return "counselor";
            case 3: return "student";
            default: return "student";
        }
    }
    
    private UserInfoVO convertToUserInfo(User user, String roleStr) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setName(user.getName());
        vo.setRole(roleStr);
        vo.setGender(user.getGender() != null ? (user.getGender() == 1 ? "M" : "F") : null);
        
        if ("student".equals(roleStr)) {
            // 获取当前活动批次
            Batch batch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                    .orderByDesc(Batch::getCreateTime)
                    .last("LIMIT 1"));
            
            if (batch != null) {
                vo.setIsNewStudent(user.getGrade() != null && user.getGrade().equals(batch.getFreshmanGrade()));
                
                // 获取审核状态
                SubsidyApply apply = subsidyApplyService.getByStudentIdAndBatchId(user.getId(), batch.getId());
                if (apply != null) {
                    vo.setFinalStatus(apply.getFinalStatus());
                    vo.setCounselorReason(apply.getCounselorReason());
                    vo.setCollegeReason(apply.getCollegeReason());
                    vo.setSchoolReason(apply.getSchoolReason());
                    if (apply.getFinalStatus() == -1) {
                        vo.setAuditStatus("rejected");
                    } else if (apply.getFinalStatus() == 3) {
                        vo.setAuditStatus("approved");
                    } else {
                        vo.setAuditStatus("pending");
                    }
                } else {
                    vo.setFinalStatus(null);
                    vo.setAuditStatus("none");
                }
            } else {
                vo.setIsNewStudent(user.getGrade() != null && user.getGrade() == 2020);
            }
        }
        
        return vo;
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户信息不存在");
        }
        String roleStr = getRoleStr(user);
        return convertToUserInfo(user, roleStr);
    }

    @Override
    public void unbindWechat(String studentId) {
        User user = getUserByUsername(studentId);
        if (user == null) {
            throw new BusinessException("学生不存在");
        }
        user.setWechatOpenid(null);
        updateById(user);
    }
}
