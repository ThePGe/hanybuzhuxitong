package com.miaoqichao.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miaoqichao.dao.entity.User;
import com.miaoqichao.core.dto.auth.LoginReq;
import com.miaoqichao.core.dto.auth.LoginVO;
import com.miaoqichao.core.dto.auth.UserInfoVO;

public interface UserService extends IService<User> {
    User getUserByUsername(String username);
    
    LoginVO login(LoginReq req);
    
    LoginVO ssoLogin(LoginReq req);
    
    UserInfoVO getCurrentUserInfo();
    
    void unbindWechat(String studentId);
}
