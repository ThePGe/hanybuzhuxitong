package com.miaoqichao.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miaoqichao.common.config.SaTokenConfig;
import com.miaoqichao.core.dto.audit.AuditApproveReq;
import com.miaoqichao.core.dto.audit.AuditQueryReq;
import com.miaoqichao.core.dto.audit.AuditVO;
import com.miaoqichao.core.service.SubsidyApplyService;
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

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.miaoqichao.api.config.WebConfig;

@WebMvcTest(
    controllers = AuditController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SaTokenConfig.class, WebConfig.class})
)
@AutoConfigureMockMvc(addFilters = false)
public class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubsidyApplyService subsidyApplyService;
    
    @MockBean
    private com.miaoqichao.core.service.UserService userService;
    
    @MockBean
    private com.miaoqichao.core.service.BatchService batchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAuditList() throws Exception {
        AuditQueryReq req = new AuditQueryReq();
        Page<AuditVO> page = new Page<>();
        page.setRecords(Collections.emptyList());
        
        Mockito.when(subsidyApplyService.getAuditList(Mockito.any())).thenReturn(page);
        
        mockMvc.perform(post("/api/audit/list")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    @Test
    public void testApprove() throws Exception {
        AuditApproveReq req = new AuditApproveReq();
        req.setApplicationIds(Collections.singletonList(1L));
        req.setIsPass(true);
        
        Mockito.doNothing().when(subsidyApplyService).approve(Mockito.any());
        
        mockMvc.perform(post("/api/audit/approve")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
    
    @Test
    public void testSubmitToNext() throws Exception {
        Mockito.doNothing().when(subsidyApplyService).submitToNext();
        
        mockMvc.perform(post("/api/audit/submit")
                .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
    
    @Test
    public void testNotifyStudents() throws Exception {
        Mockito.doNothing().when(subsidyApplyService).notifyStudents();
        
        mockMvc.perform(post("/api/audit/notify")
                .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}
