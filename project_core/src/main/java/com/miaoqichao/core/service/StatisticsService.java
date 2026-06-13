package com.miaoqichao.core.service;

import com.miaoqichao.core.dto.CollegeStatVO;
import com.miaoqichao.core.dto.RealtimeStatVO;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    List<RealtimeStatVO> getRealtimeStatistics();

    List<CollegeStatVO> getCollegeStatistics();

    Map<String, String> exportExcel(String type);
}
