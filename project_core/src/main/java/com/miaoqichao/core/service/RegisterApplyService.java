package com.miaoqichao.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miaoqichao.dao.entity.RegisterApply;
import com.miaoqichao.core.dto.auth.RegisterReq;
import com.miaoqichao.core.dto.auth.RegisterReviewReq;
import com.miaoqichao.core.dto.auth.RegisterApplyVO;

import java.util.List;

public interface RegisterApplyService extends IService<RegisterApply> {
    void register(RegisterReq req);
    List<RegisterApplyVO> getPendingApplications();
    void reviewApplication(RegisterReviewReq req);
}