package com.miaoqichao.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.miaoqichao.core.dto.apply.SubsidyApplyReq;
import com.miaoqichao.core.dto.audit.AuditApproveReq;
import com.miaoqichao.core.dto.audit.AuditQueryReq;
import com.miaoqichao.core.dto.audit.AuditVO;
import com.miaoqichao.dao.entity.SubsidyApply;

public interface SubsidyApplyService extends IService<SubsidyApply> {
    
    /**
     * 学生提交申请
     */
    void apply(SubsidyApplyReq req);
    
    /**
     * 获取审核列表
     */
    Page<AuditVO> getAuditList(AuditQueryReq req);
    
    /**
     * 批量审核
     */
    void approve(AuditApproveReq req);
    
    /**
     * 提交至下一级
     */
    void submitToNext();
    
    /**
     * 通知学生
     */
    void notifyStudents();
    
    /**
     * 根据学生ID和批次ID获取申请记录
     */
    SubsidyApply getByStudentIdAndBatchId(Long studentId, Long batchId);
}


