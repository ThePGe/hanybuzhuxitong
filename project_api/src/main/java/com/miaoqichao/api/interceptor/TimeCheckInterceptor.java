package com.miaoqichao.api.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miaoqichao.common.exception.BusinessException;
import com.miaoqichao.core.service.BatchService;
import com.miaoqichao.dao.entity.Batch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class TimeCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private BatchService batchService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 只拦截学生的申请和选款操作
        if (requestURI.contains("/api/student/apply") || requestURI.contains("/api/student/select")) {
            // 验证是否是学生登录
            if (StpUtil.isLogin() && "student".equals(StpUtil.getRoleList().get(0))) {
                Batch currentBatch = batchService.getOne(new LambdaQueryWrapper<Batch>()
                        .orderByDesc(Batch::getCreateTime)
                        .last("LIMIT 1"));
                
                if (currentBatch == null) {
                    throw new BusinessException("当前无活动批次，无法操作");
                }
                
                Date now = new Date();
                
                if (requestURI.contains("/api/student/apply")) {
                    if (currentBatch.getApplyEndTime() != null && now.after(currentBatch.getApplyEndTime())) {
                        throw new BusinessException("申请时间已截止");
                    }
                } else if (requestURI.contains("/api/student/select")) {
                    if (currentBatch.getSelectEndTime() != null && now.after(currentBatch.getSelectEndTime())) {
                        throw new BusinessException("选款时间已截止");
                    }
                }
            }
        }
        return true;
    }
}
