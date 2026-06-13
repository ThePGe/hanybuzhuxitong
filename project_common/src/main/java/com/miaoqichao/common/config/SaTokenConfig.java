package com.miaoqichao.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定一条 match 规则
            SaRouter
                .match("/**")
                .notMatch(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/sso/login",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/doc.html",
                    "/webjars/**",
                    "/favicon.ico",
                    "/api/file/upload" // 临时放行，或者后续根据需要调整
                )
                .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
