package com.miaoqichao.core.security;

import cn.dev33.satoken.stp.StpInterface;
import com.miaoqichao.core.service.UserService;
import com.miaoqichao.dao.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserService userService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本系统以角色为主，暂不配置细粒度权限
        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        User user = userService.getById(Long.parseLong(loginId.toString()));
        if (user != null) {
            Integer role = user.getRole();
            if (role != null) {
                switch (role) {
                    case 0:
                        // 超管 9447 和普通学校管理员都算 0，但在业务中 9447 是超管
                        if ("9447".equals(user.getUsername())) {
                            list.add("superadmin");
                        }
                        list.add("school");
                        break;
                    case 1:
                        list.add("college");
                        break;
                    case 2:
                        list.add("counselor");
                        break;
                    case 3:
                        list.add("student");
                        break;
                }
            }
        }
        return list;
    }
}