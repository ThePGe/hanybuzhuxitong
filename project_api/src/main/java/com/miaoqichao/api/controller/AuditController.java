package com.miaoqichao.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.miaoqichao.common.api.CommonResult;
import com.miaoqichao.core.dto.audit.AuditApproveReq;
import com.miaoqichao.core.dto.audit.AuditQueryReq;
import com.miaoqichao.core.dto.audit.AuditVO;
import com.miaoqichao.core.service.SubsidyApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "审核模块")
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final SubsidyApplyService subsidyApplyService;

    @Operation(summary = "获取审核列表")
    @PostMapping("/list")
    @SaCheckLogin
    @SaCheckRole(mode = SaMode.OR, value = {"counselor", "college", "school"})
    public CommonResult<Page<AuditVO>> getAuditList(@RequestBody AuditQueryReq req) {
        Page<AuditVO> page = subsidyApplyService.getAuditList(req);
        return CommonResult.success(page);
    }

    @Operation(summary = "批量审核申请")
    @PostMapping("/approve")
    @SaCheckLogin
    @SaCheckRole(mode = SaMode.OR, value = {"counselor", "college", "school"})
    public CommonResult<Boolean> approveApplication(@RequestBody AuditApproveReq req) {
        subsidyApplyService.approve(req);
        return CommonResult.success(true);
    }

    @Operation(summary = "提交至下一级审核")
    @PostMapping("/submit")
    @SaCheckLogin
    @SaCheckRole(mode = SaMode.OR, value = {"counselor", "college"})
    public CommonResult<Boolean> submitToNext() {
        subsidyApplyService.submitToNext();
        return CommonResult.success(true);
    }

    @Operation(summary = "通知学生最终结果")
    @PostMapping("/notify")
    @SaCheckLogin
    @SaCheckRole("school")
    public CommonResult<Boolean> notifyStudents() {
        subsidyApplyService.notifyStudents();
        return CommonResult.success(true);
    }
}
