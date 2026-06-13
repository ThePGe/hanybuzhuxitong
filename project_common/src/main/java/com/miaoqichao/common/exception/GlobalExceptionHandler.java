package com.miaoqichao.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.miaoqichao.common.api.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public CommonResult<?> handleBusinessException(BusinessException e) {
        log.error("Business Exception: {}", e.getMessage());
        return CommonResult.failed(e.getMessage());
    }

    // Sa-Token Exceptions
    @ExceptionHandler(NotLoginException.class)
    public CommonResult<?> handleNotLoginException(NotLoginException e) {
        log.error("Not Login Exception: {}", e.getMessage());
        return CommonResult.failed(401, "未登录或登录已过期");
    }

    @ExceptionHandler(NotRoleException.class)
    public CommonResult<?> handleNotRoleException(NotRoleException e) {
        log.error("Not Role Exception: {}", e.getMessage());
        return CommonResult.failed(403, "无此角色权限");
    }

    @ExceptionHandler(NotPermissionException.class)
    public CommonResult<?> handleNotPermissionException(NotPermissionException e) {
        log.error("Not Permission Exception: {}", e.getMessage());
        return CommonResult.failed(403, "无此操作权限");
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<?> handleException(Exception e) {
        log.error("Unknown Exception: ", e);
        return CommonResult.failed("系统错误，请联系管理员");
    }
}