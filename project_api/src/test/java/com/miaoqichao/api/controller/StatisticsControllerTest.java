package com.miaoqichao.api.controller;

import com.miaoqichao.api.config.WebConfig;
import com.miaoqichao.common.config.SaTokenConfig;
import com.miaoqichao.core.dto.CollegeStatVO;
import com.miaoqichao.core.dto.RealtimeStatVO;
import com.miaoqichao.core.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = StatisticsController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SaTokenConfig.class, WebConfig.class})
)
@AutoConfigureMockMvc(addFilters = false)
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @MockBean
    private com.miaoqichao.core.service.BatchService batchService;

    @Test
    public void testGetRealtimeStatistics() throws Exception {
        RealtimeStatVO vo = new RealtimeStatVO();
        vo.setClothingName("波司登极寒系列羽绒服(男)");
        vo.setSize("L");
        vo.setCount(10);

        when(statisticsService.getRealtimeStatistics()).thenReturn(Collections.singletonList(vo));

        mockMvc.perform(get("/api/statistics/realtime"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].clothingName").value("波司登极寒系列羽绒服(男)"))
                .andExpect(jsonPath("$.data[0].count").value(10));
    }

    @Test
    public void testGetCollegeStatistics() throws Exception {
        CollegeStatVO vo = new CollegeStatVO();
        vo.setStudentId("2020001");
        vo.setName("李雷");
        vo.setGender("M");
        vo.setCollege("计算机学院");
        vo.setClothingName("李宁冲锋衣");
        vo.setSize("L");
        vo.setCount(1);

        when(statisticsService.getCollegeStatistics()).thenReturn(Collections.singletonList(vo));

        mockMvc.perform(get("/api/statistics/college"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].college").value("计算机学院"))
                .andExpect(jsonPath("$.data[0].count").value(1));
    }

    @Test
    public void testExportExcel() throws Exception {
        Map<String, String> result = new HashMap<>();
        result.put("downloadUrl", "http://localhost:9000/winter-clothing/abcd1234.xlsx");

        when(statisticsService.exportExcel(anyString())).thenReturn(result);

        mockMvc.perform(get("/api/statistics/export").param("type", "application"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.downloadUrl").value("http://localhost:9000/winter-clothing/abcd1234.xlsx"));
    }
}
