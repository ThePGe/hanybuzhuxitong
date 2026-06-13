package com.miaoqichao.core.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miaoqichao.common.exception.BusinessException;
import com.miaoqichao.core.dto.apply.SubsidyApplyReq;
import com.miaoqichao.core.dto.audit.AuditApproveReq;
import com.miaoqichao.core.dto.audit.AuditQueryReq;
import com.miaoqichao.core.dto.audit.AuditVO;
import com.miaoqichao.core.service.BatchService;
import com.miaoqichao.core.service.SubsidyApplyService;
import com.miaoqichao.core.service.UserService;
import com.miaoqichao.dao.entity.Batch;
import com.miaoqichao.dao.entity.SubsidyApply;
import com.miaoqichao.dao.entity.User;
import com.miaoqichao.dao.mapper.SubsidyApplyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubsidyApplyServiceImpl extends ServiceImpl<SubsidyApplyMapper, SubsidyApply> implements SubsidyApplyService {

    private final BatchService batchService;
    
    @Lazy
    @Autowired
    private UserService userService;

    @Override
    public void apply(SubsidyApplyReq req) {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user == null || user.getRole() == null || user.getRole() != 3) {
            throw new BusinessException("仅学生可提交申请");
        }

        Batch batch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                .orderByDesc(Batch::getCreateTime)
                .last("LIMIT 1"));
        if (batch == null) {
            throw new BusinessException("当前无活动批次");
        }

        if (new Date().after(batch.getApplyEndTime())) {
            throw new BusinessException("申请时间已截止");
        }

        long count = count(new LambdaQueryWrapper<SubsidyApply>()
                .eq(SubsidyApply::getBatchId, batch.getId())
                .eq(SubsidyApply::getStudentId, userId));
        if (count > 0) {
            throw new BusinessException("您已提交过申请");
        }

        boolean isFreshman = user.getGrade() != null && user.getGrade().equals(batch.getFreshmanGrade());
        if (!isFreshman && !StringUtils.hasText(req.getApplyReason())) {
            throw new BusinessException("老生必须填写申请理由");
        }

        SubsidyApply apply = new SubsidyApply();
        apply.setBatchId(batch.getId());
        apply.setStudentId(userId);
        apply.setApplyReason(req.getApplyReason());
        apply.setCounselorStatus(0);
        apply.setCounselorSubmit(false);
        apply.setCollegeStatus(0);
        apply.setCollegeSubmit(false);
        apply.setSchoolStatus(0);
        apply.setFinalStatus(0);
        apply.setCreateTime(new Date());
        apply.setUpdateTime(new Date());
        
        save(apply);
    }

    @Override
    public Page<AuditVO> getAuditList(AuditQueryReq req) {
        long userId = StpUtil.getLoginIdAsLong();
        User currentUser = userService.getById(userId);
        Integer role = currentUser.getRole();

        Batch batch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                .orderByDesc(Batch::getCreateTime)
                .last("LIMIT 1"));
        if (batch == null) {
            return new Page<>();
        }

        LambdaQueryWrapper<SubsidyApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubsidyApply::getBatchId, batch.getId());

        if (role == 2) { // 辅导员
            List<Long> studentIds = getStudentIdsByClass(currentUser.getClassName());
            if (studentIds.isEmpty()) return new Page<>();
            queryWrapper.in(SubsidyApply::getStudentId, studentIds);
            
            if ("pending".equals(req.getStatus())) {
                queryWrapper.eq(SubsidyApply::getFinalStatus, 0);
            } else if ("approved".equals(req.getStatus())) {
                queryWrapper.ge(SubsidyApply::getFinalStatus, 1);
            } else if ("rejected".equals(req.getStatus())) {
                queryWrapper.eq(SubsidyApply::getFinalStatus, -1);
            }
        } else if (role == 1) { // 学院
            List<Long> studentIds = getStudentIdsByCollege(currentUser.getCollegeId());
            if (studentIds.isEmpty()) return new Page<>();
            queryWrapper.in(SubsidyApply::getStudentId, studentIds);
            
            if ("pending".equals(req.getStatus())) {
                queryWrapper.eq(SubsidyApply::getFinalStatus, 1);
            } else if ("approved".equals(req.getStatus())) {
                queryWrapper.ge(SubsidyApply::getFinalStatus, 2);
            } else if ("rejected".equals(req.getStatus())) {
                queryWrapper.eq(SubsidyApply::getFinalStatus, -1);
                queryWrapper.ge(SubsidyApply::getCounselorStatus, 1); // 必须是辅导员通过后被拒的
            } else {
                queryWrapper.and(w -> w.ge(SubsidyApply::getFinalStatus, 1).or().and(i -> i.eq(SubsidyApply::getFinalStatus, -1).ge(SubsidyApply::getCounselorStatus, 1)));
            }
        } else if (role == 0) { // 学校
            if ("pending".equals(req.getStatus())) {
                queryWrapper.eq(SubsidyApply::getFinalStatus, 2);
            } else if ("approved".equals(req.getStatus())) {
                queryWrapper.eq(SubsidyApply::getFinalStatus, 3);
            } else if ("rejected".equals(req.getStatus())) {
                queryWrapper.eq(SubsidyApply::getFinalStatus, -1);
                queryWrapper.ge(SubsidyApply::getCollegeStatus, 1); // 必须是学院通过后被拒的
            } else {
                queryWrapper.and(w -> w.ge(SubsidyApply::getFinalStatus, 2).or().and(i -> i.eq(SubsidyApply::getFinalStatus, -1).ge(SubsidyApply::getCollegeStatus, 1)));
            }
        }

        Page<SubsidyApply> page = new Page<>(req.getPage(), req.getSize());
        page = page(page, queryWrapper);

        Page<AuditVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        
        if (page.getRecords().isEmpty()) {
            return voPage;
        }

        List<Long> studentIds = page.getRecords().stream().map(SubsidyApply::getStudentId).collect(Collectors.toList());
        Map<Long, User> userMap = userService.listByIds(studentIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<AuditVO> voList = page.getRecords().stream().map(apply -> {
            AuditVO vo = new AuditVO();
            vo.setId(apply.getId());
            User student = userMap.get(apply.getStudentId());
            if (student != null) {
                vo.setStudentId(student.getUsername());
                vo.setName(student.getName());
                vo.setGender(student.getGender() != null && student.getGender() == 1 ? "M" : "F");
                vo.setCollege(student.getCollegeName());
                vo.setMajor(student.getClassName());
            }
            vo.setReason(apply.getApplyReason());
            
            // Determine status based on role
            vo.setStatus(getStatusStr(apply.getFinalStatus(), role));
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    private String getStatusStr(Integer finalStatus, Integer role) {
        if (finalStatus == -1) return "rejected";
        if (role == 2) {
            return finalStatus == 0 ? "pending" : "approved";
        } else if (role == 1) {
            return finalStatus == 1 ? "pending" : "approved";
        } else if (role == 0) {
            return finalStatus == 2 ? "pending" : "approved";
        }
        return "pending";
    }

    private List<Long> getStudentIdsByClass(String className) {
        return userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getRole, 3)
                .eq(User::getClassName, className))
                .stream().map(User::getId).collect(Collectors.toList());
    }

    private List<Long> getStudentIdsByCollege(Long collegeId) {
        return userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getRole, 3)
                .eq(User::getCollegeId, collegeId))
                .stream().map(User::getId).collect(Collectors.toList());
    }

    @Override
    public void approve(AuditApproveReq req) {
        long userId = StpUtil.getLoginIdAsLong();
        User currentUser = userService.getById(userId);
        Integer role = currentUser.getRole();

        List<SubsidyApply> applies = listByIds(req.getApplicationIds());
        for (SubsidyApply apply : applies) {
            if (role == 2) {
                if (apply.getFinalStatus() != 0) throw new BusinessException("当前状态不可审核");
                apply.setCounselorStatus(req.getIsPass() ? 1 : 2);
                if (!req.getIsPass()) apply.setCounselorReason(req.getReason());
                
                apply.setCounselorSubmit(true);
                apply.setFinalStatus(req.getIsPass() ? 1 : -1);
            } else if (role == 1) {
                if (apply.getFinalStatus() != 1) throw new BusinessException("当前状态不可审核");
                apply.setCollegeStatus(req.getIsPass() ? 1 : 2);
                if (!req.getIsPass()) apply.setCollegeReason(req.getReason());
                
                apply.setCollegeSubmit(true);
                apply.setFinalStatus(req.getIsPass() ? 2 : -1);
            } else if (role == 0) {
                if (apply.getFinalStatus() != 2) throw new BusinessException("当前状态不可审核");
                apply.setSchoolStatus(req.getIsPass() ? 1 : 2);
                if (!req.getIsPass()) apply.setSchoolReason(req.getReason());
                
                apply.setFinalStatus(req.getIsPass() ? 3 : -1);
            }
        }
        updateBatchById(applies);
    }

    @Override
    public void submitToNext() {
        long userId = StpUtil.getLoginIdAsLong();
        User currentUser = userService.getById(userId);
        Integer role = currentUser.getRole();

        Batch batch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                .orderByDesc(Batch::getCreateTime)
                .last("LIMIT 1"));
        if (batch == null) throw new BusinessException("当前无活动批次");

        boolean isApplyEnded = new Date().after(batch.getApplyEndTime());

        if (role == 2) {
            List<Long> studentIds = getStudentIdsByClass(currentUser.getClassName());
            if (studentIds.isEmpty()) return;
            
            List<SubsidyApply> applies = list(new LambdaQueryWrapper<SubsidyApply>()
                    .eq(SubsidyApply::getBatchId, batch.getId())
                    .in(SubsidyApply::getStudentId, studentIds)
                    .eq(SubsidyApply::getFinalStatus, 0));
                    
            for (SubsidyApply apply : applies) {
                apply.setCounselorStatus(1); // 默认通过
                apply.setCounselorSubmit(true);
                apply.setFinalStatus(1); 
            }
            if (!applies.isEmpty()) updateBatchById(applies);
        } else if (role == 1) {
            List<Long> studentIds = getStudentIdsByCollege(currentUser.getCollegeId());
            if (studentIds.isEmpty()) return;

            List<SubsidyApply> applies = list(new LambdaQueryWrapper<SubsidyApply>()
                    .eq(SubsidyApply::getBatchId, batch.getId())
                    .in(SubsidyApply::getStudentId, studentIds)
                    .eq(SubsidyApply::getFinalStatus, 1));
                    
            for (SubsidyApply apply : applies) {
                apply.setCollegeStatus(1); // 默认通过
                apply.setCollegeSubmit(true);
                apply.setFinalStatus(2); 
            }
            if (!applies.isEmpty()) updateBatchById(applies);
        }
    }

    @Override
    public void notifyStudents() {
        long userId = StpUtil.getLoginIdAsLong();
        User currentUser = userService.getById(userId);
        if (currentUser.getRole() != 0) {
            throw new BusinessException("仅学校管理员可执行此操作");
        }

        Batch batch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                .orderByDesc(Batch::getCreateTime)
                .last("LIMIT 1"));
        if (batch == null) throw new BusinessException("当前无活动批次");
    }

    @Override
    public SubsidyApply getByStudentIdAndBatchId(Long studentId, Long batchId) {
        return getOne(new LambdaQueryWrapper<SubsidyApply>()
                .eq(SubsidyApply::getStudentId, studentId)
                .eq(SubsidyApply::getBatchId, batchId)
                .last("LIMIT 1"));
    }
}
