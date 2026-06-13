package com.miaoqichao.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miaoqichao.api.dto.ConfigSkuDTO;
import com.miaoqichao.core.service.SkuService;
import com.miaoqichao.dao.entity.Sku;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
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
    controllers = SkuController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SaTokenConfig.class, WebConfig.class})
)
@AutoConfigureMockMvc(addFilters = false)
public class SkuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkuService skuService;
    
    @MockBean
    private com.miaoqichao.core.service.BatchService batchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testConfigClothingSku() throws Exception {
        ConfigSkuDTO dto = new ConfigSkuDTO();
        dto.setClothingId(1L);
        Sku sku = new Sku();
        sku.setSize("XL");
        dto.setSkus(Arrays.asList(sku));

        when(skuService.remove((com.baomidou.mybatisplus.core.conditions.Wrapper<Sku>) any())).thenReturn(true);
        when(skuService.saveBatch(any())).thenReturn(true);

        mockMvc.perform(post("/api/school/clothing/sku/config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}
