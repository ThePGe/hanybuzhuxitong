package com.miaoqichao.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.miaoqichao.common.api.CommonResult;
import com.miaoqichao.core.dto.auth.LoginReq;
import com.miaoqichao.core.dto.auth.LoginVO;
import com.miaoqichao.core.dto.auth.RegisterReq;
import com.miaoqichao.core.dto.auth.UserInfoVO;
import com.miaoqichao.core.service.RegisterApplyService;
import com.miaoqichao.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "一、 登录与认证模块")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RegisterApplyService registerApplyService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public CommonResult<Boolean> register(@RequestBody RegisterReq req) {
        registerApplyService.register(req);
        return CommonResult.success(true);
    }

    @Operation(summary = "统一账号登录")
    @PostMapping("/login")
    public CommonResult<LoginVO> login(@RequestBody LoginReq req) {
        LoginVO vo = userService.login(req);
        return CommonResult.success(vo);
    }

    @Operation(summary = "智慧学工授权登录 (白名单)")
    @PostMapping("/sso/login")
    public CommonResult<LoginVO> ssoLogin(@RequestBody LoginReq req) {
        LoginVO vo = userService.ssoLogin(req);
        return CommonResult.success(vo);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/userInfo")
    @SaCheckLogin
    public CommonResult<UserInfoVO> getUserInfo() {
        UserInfoVO vo = userService.getCurrentUserInfo();
        return CommonResult.success(vo);
    }
}
