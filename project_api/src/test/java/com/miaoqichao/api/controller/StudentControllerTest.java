package com.miaoqichao.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miaoqichao.common.config.SaTokenConfig;
import com.miaoqichao.core.dto.apply.SubsidyApplyReq;
import com.miaoqichao.core.dto.auth.UnbindReq;
import com.miaoqichao.core.dto.selection.SelectionSubmitReq;
import com.miaoqichao.core.service.SelectionService;
import com.miaoqichao.core.service.SubsidyApplyService;
import com.miaoqichao.core.service.UserService;
import com.miaoqichao.dao.entity.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.miaoqichao.api.config.WebConfig;

@WebMvcTest(
    controllers = StudentController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SaTokenConfig.class, WebConfig.class})
)
@AutoConfigureMockMvc(addFilters = false)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private com.miaoqichao.core.service.BatchService batchService;

    @MockBean
    private SubsidyApplyService subsidyApplyService;
    
    @MockBean
    private SelectionService selectionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUnbindWechat() throws Exception {
        UnbindReq req = new UnbindReq();
        req.setStudentId("2020001");

        Mockito.doNothing().when(userService).unbindWechat(Mockito.anyString());

        mockMvc.perform(post("/api/student/unbind")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
    
    @Test
    public void testApply() throws Exception {
        SubsidyApplyReq req = new SubsidyApplyReq();
        req.setApplyReason("家庭困难");
        
        Mockito.doNothing().when(subsidyApplyService).apply(Mockito.any());
        
        mockMvc.perform(post("/api/student/apply")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
    
    @Test
    public void testGetSelectionClothingList() throws Exception {
        Mockito.when(selectionService.getSelectionClothingList()).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/api/student/selection/clothing/list")
                .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    @Test
    public void testSubmitSelection() throws Exception {
        SelectionSubmitReq req = new SelectionSubmitReq();
        req.setClothingId(1L);
        req.setSkuId(101L);
        
        Mockito.doNothing().when(selectionService).submitSelection(Mockito.any());
        
        mockMvc.perform(post("/api/student/selection")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}


