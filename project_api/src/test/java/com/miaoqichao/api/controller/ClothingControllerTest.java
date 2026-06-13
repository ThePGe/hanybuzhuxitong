package com.miaoqichao.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miaoqichao.core.dto.selection.AdminUpdateSelectionReq;
import com.miaoqichao.core.service.ClothingService;
import com.miaoqichao.core.service.SelectionService;
import com.miaoqichao.dao.entity.Clothing;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.miaoqichao.api.config.WebConfig;
import com.miaoqichao.common.config.SaTokenConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(
    controllers = ClothingController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SaTokenConfig.class, WebConfig.class})
)
@AutoConfigureMockMvc(addFilters = false)
public class ClothingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClothingService clothingService;
    
    @MockBean
    private com.miaoqichao.core.service.BatchService batchService;

    @MockBean
    private SelectionService selectionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetClothingList() throws Exception {
        Clothing clothing = new Clothing();
        clothing.setId(1L);
        clothing.setName("Test Clothing");
        clothing.setGender("M");

        Mockito.when(clothingService.list((com.baomidou.mybatisplus.core.conditions.Wrapper<Clothing>) Mockito.any())).thenReturn(Collections.singletonList(clothing));

        mockMvc.perform(get("/api/school/clothing/list")
                .header("Authorization", "Bearer mock-token")
                .param("gender", "M"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Test Clothing"))
                .andExpect(jsonPath("$.data[0].gender").value("M"));
    }

    @Test
    public void testCreateClothing() throws Exception {
        Clothing clothing = new Clothing();
        clothing.setName("New Clothing");
        clothing.setGender("F");

        Mockito.when(clothingService.save(Mockito.any(Clothing.class))).thenReturn(true);

        mockMvc.perform(post("/api/school/clothing")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clothing)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
    
    @Test
    public void testAdminUpdateSelection() throws Exception {
        AdminUpdateSelectionReq req = new AdminUpdateSelectionReq();
        req.setStudentId("2020001");
        req.setClothingId(1L);
        req.setSkuId(101L);
        
        Mockito.doNothing().when(selectionService).adminUpdateSelection(Mockito.any());
        
        mockMvc.perform(post("/api/school/clothing/selection/update")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}
