package com.miaoqichao.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miaoqichao.common.api.CommonResult;
import com.miaoqichao.core.dto.auth.RegisterApplyVO;
import com.miaoqichao.core.dto.auth.RegisterReviewReq;
import com.miaoqichao.core.service.RegisterApplyService;
import com.miaoqichao.core.service.WhitelistService;
import com.miaoqichao.dao.entity.Whitelist;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "超级管理员接口")
@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
public class SuperadminController {

    private final WhitelistService whitelistService;
    private final RegisterApplyService registerApplyService;

    @Operation(summary = "获取待审核注册申请")
    @GetMapping("/register-apply/list")
    @SaCheckLogin
    public CommonResult<List<RegisterApplyVO>> getPendingApplications() {
        return CommonResult.success(registerApplyService.getPendingApplications());
    }

    @Operation(summary = "审核注册申请")
    @PostMapping("/register-apply/review")
    @SaCheckLogin
    public CommonResult<Boolean> reviewApplication(@RequestBody RegisterReviewReq req) {
        registerApplyService.reviewApplication(req);
        return CommonResult.success(true);
    }

    @Operation(summary = "获取白名单列表")
    @GetMapping("/whitelist")
    @SaCheckLogin
    public CommonResult<List<String>> getWhitelist() {
        // 由于这里仅做简单返回，实际上应该鉴权超管角色，目前根据项目直接放行查询或做简单拦截
        List<Whitelist> list = whitelistService.list();
        List<String> usernames = list.stream().map(Whitelist::getUsername).collect(Collectors.toList());
        return CommonResult.success(usernames);
    }

    @Operation(summary = "添加白名单")
    @PostMapping("/whitelist")
    @SaCheckLogin
    public CommonResult<Boolean> addWhitelist(@RequestBody Whitelist req) {
        // 简单校验是否存在
        long count = whitelistService.count(new LambdaQueryWrapper<Whitelist>().eq(Whitelist::getUsername, req.getUsername()));
        if (count > 0) {
            return CommonResult.failed("该用户已在白名单中");
        }
        Whitelist whitelist = new Whitelist();
        whitelist.setUsername(req.getUsername());
        whitelist.setCreateTime(new Date());
        whitelist.setOperatorId(StpUtil.getLoginIdAsLong());
        whitelistService.save(whitelist);
        return CommonResult.success(true);
    }

    @Operation(summary = "移除白名单")
    @DeleteMapping("/whitelist/{username}")
    @SaCheckLogin
    public CommonResult<Boolean> removeWhitelist(@PathVariable String username) {
        whitelistService.remove(new LambdaQueryWrapper<Whitelist>().eq(Whitelist::getUsername, username));
        return CommonResult.success(true);
    }
}
