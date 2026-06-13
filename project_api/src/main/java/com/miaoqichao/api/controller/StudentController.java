package com.miaoqichao.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.miaoqichao.common.api.CommonResult;
import com.miaoqichao.core.dto.apply.SubsidyApplyReq;
import com.miaoqichao.core.dto.auth.UnbindReq;
import com.miaoqichao.core.dto.selection.ClothingVO;
import com.miaoqichao.core.dto.selection.SelectionSubmitReq;
import com.miaoqichao.core.service.SelectionService;
import com.miaoqichao.core.service.SubsidyApplyService;
import com.miaoqichao.core.service.UserService;
import com.miaoqichao.dao.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学生模块")
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final UserService userService;
    private final SubsidyApplyService subsidyApplyService;
    private final SelectionService selectionService;

    @Operation(summary = "微信解绑")
    @PostMapping("/unbind")
    @SaCheckLogin
    @SaCheckRole("student")
    public CommonResult<Boolean> unbindWechat(@RequestBody UnbindReq req) {
        userService.unbindWechat(req.getStudentId());
        return CommonResult.success(true);
    }
    
    @Operation(summary = "学生提交寒衣补助申请")
    @PostMapping("/apply")
    @SaCheckLogin
    @SaCheckRole("student")
    public CommonResult<Boolean> apply(@RequestBody SubsidyApplyReq req) {
        subsidyApplyService.apply(req);
        return CommonResult.success(true);
    }
    
    @Operation(summary = "学生获取选款列表")
    @GetMapping("/selection/clothing/list")
    @SaCheckLogin
    @SaCheckRole("student")
    public CommonResult<List<ClothingVO>> getSelectionClothingList() {
        List<ClothingVO> list = selectionService.getSelectionClothingList();
        return CommonResult.success(list);
    }

    @Operation(summary = "学生提交选款")
    @PostMapping("/selection")
    @SaCheckLogin
    @SaCheckRole("student")
    public CommonResult<Boolean> submitSelection(@RequestBody SelectionSubmitReq req) {
        selectionService.submitSelection(req);
        return CommonResult.success(true);
    }
}


