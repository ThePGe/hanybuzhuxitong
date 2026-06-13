package com.miaoqichao.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miaoqichao.common.config.SaTokenConfig;
import com.miaoqichao.core.dto.auth.LoginReq;
import com.miaoqichao.core.dto.auth.LoginVO;
import com.miaoqichao.core.dto.auth.UserInfoVO;
import com.miaoqichao.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.miaoqichao.api.config.WebConfig;

@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SaTokenConfig.class, WebConfig.class})
)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private com.miaoqichao.core.service.BatchService batchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLogin() throws Exception {
        LoginReq req = new LoginReq();
        req.setUsername("2020001");
        req.setPassword("101234");

        LoginVO mockVo = new LoginVO();
        mockVo.setToken("mock-token");
        mockVo.setRole("student");

        Mockito.when(userService.login(Mockito.any(LoginReq.class))).thenReturn(mockVo);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("mock-token"));
    }

    @Test
    public void testSsoLogin() throws Exception {
        LoginReq req = new LoginReq();
        req.setUsername("school01");
        req.setPassword("123456");

        LoginVO mockVo = new LoginVO();
        mockVo.setToken("mock-token-sso");
        mockVo.setRole("school");

        Mockito.when(userService.ssoLogin(Mockito.any(LoginReq.class))).thenReturn(mockVo);

        mockMvc.perform(post("/api/auth/sso/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("mock-token-sso"));
    }

    @Test
    public void testGetUserInfo() throws Exception {
        UserInfoVO mockVo = new UserInfoVO();
        mockVo.setUsername("2020001");
        mockVo.setName("李新生(男)");

        Mockito.when(userService.getCurrentUserInfo()).thenReturn(mockVo);

        mockMvc.perform(get("/api/auth/userInfo")
                .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("李新生(男)"));
    }
}
