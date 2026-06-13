package com.miaoqichao.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miaoqichao.core.service.BatchService;
import com.miaoqichao.dao.entity.Batch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

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
    controllers = BatchController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SaTokenConfig.class, WebConfig.class})
)
@AutoConfigureMockMvc(addFilters = false)
public class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BatchService batchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetBatchInfo() throws Exception {
        Batch batch = new Batch();
        batch.setId(1L);
        batch.setYear("2023");
        batch.setLevel("特困");

        when(batchService.getOne((com.baomidou.mybatisplus.core.conditions.Wrapper<Batch>) any())).thenReturn(batch);

        mockMvc.perform(get("/api/school/batch/info"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.year").value("2023"));
    }

    @Test
    public void testCreateBatch() throws Exception {
        Batch batch = new Batch();
        batch.setYear("2023");
        batch.setLevel("特困");

        when(batchService.save(any(Batch.class))).thenReturn(true);

        mockMvc.perform(post("/api/school/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batch)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}
