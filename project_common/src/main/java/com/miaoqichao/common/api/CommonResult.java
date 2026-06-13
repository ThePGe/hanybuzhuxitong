package com.miaoqichao.common.api;

import lombok.Data;

/**
 * Unified API Response
 */
@Data
public class CommonResult<T> {
    private long code;
    private String msg;
    private T data;

    protected CommonResult() {
    }

    protected CommonResult(long code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> CommonResult<T> success() {
        return new CommonResult<>(200, "操作成功", null);
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "操作成功", data);
    }

    public static <T> CommonResult<T> success(T data, String msg) {
        return new CommonResult<>(200, msg, data);
    }

    public static <T> CommonResult<T> failed(String msg) {
        return new CommonResult<>(500, msg, null);
    }

    public static <T> CommonResult<T> failed(long code, String msg) {
        return new CommonResult<>(code, msg, null);
    }

    public static <T> CommonResult<T> failed() {
        return failed("操作失败");
    }
}