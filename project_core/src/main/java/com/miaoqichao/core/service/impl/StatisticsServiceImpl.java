package com.miaoqichao.core.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miaoqichao.common.exception.BusinessException;
import com.miaoqichao.common.utils.MinioUtils;
import com.miaoqichao.core.dto.CollegeStatVO;
import com.miaoqichao.core.dto.RealtimeStatVO;
import com.miaoqichao.core.service.*;
import com.miaoqichao.dao.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SubsidyApplyService subsidyApplyService;
    private final UserService userService;
    private final ClothingService clothingService;
    private final SkuService skuService;
    private final BatchService batchService;
    private final MinioUtils minioUtils;

    private Batch getActiveBatch() {
        return batchService.getOne(new LambdaQueryWrapper<Batch>()
                .orderByDesc(Batch::getCreateTime)
                .last("LIMIT 1"));
    }

    @Override
    public List<RealtimeStatVO> getRealtimeStatistics() {
        Batch batch = getActiveBatch();
        if (batch == null) return Collections.emptyList();

        List<SubsidyApply> applies = subsidyApplyService.list(new LambdaQueryWrapper<SubsidyApply>()
                .eq(SubsidyApply::getBatchId, batch.getId())
                .eq(SubsidyApply::getFinalStatus, 3)
                .isNotNull(SubsidyApply::getClothingSkuId));

        if (applies.isEmpty()) return Collections.emptyList();

        List<Long> skuIds = applies.stream().map(SubsidyApply::getClothingSkuId).distinct().collect(Collectors.toList());
        Map<Long, Sku> skuMap = skuService.listByIds(skuIds).stream().collect(Collectors.toMap(Sku::getId, s -> s));
        List<Long> clothingIds = skuMap.values().stream().map(Sku::getClothingId).distinct().collect(Collectors.toList());
        Map<Long, Clothing> clothingMap = clothingService.listByIds(clothingIds).stream().collect(Collectors.toMap(Clothing::getId, c -> c));

        Map<String, RealtimeStatVO> aggData = new HashMap<>();

        for (SubsidyApply apply : applies) {
            Sku sku = skuMap.get(apply.getClothingSkuId());
            if (sku != null) {
                Clothing clothing = clothingMap.get(sku.getClothingId());
                if (clothing != null) {
                    String key = clothing.getId() + "-" + sku.getId();
                    RealtimeStatVO vo = aggData.getOrDefault(key, new RealtimeStatVO());
                    if (vo.getClothingName() == null) {
                        vo.setClothingName(clothing.getName());
                        vo.setSize(sku.getSize());
                        vo.setCount(0);
                    }
                    vo.setCount(vo.getCount() + 1);
                    aggData.put(key, vo);
                }
            }
        }

        return new ArrayList<>(aggData.values());
    }

    @Override
    public List<CollegeStatVO> getCollegeStatistics() {
        Batch batch = getActiveBatch();
        if (batch == null) return Collections.emptyList();

        List<SubsidyApply> applies = subsidyApplyService.list(new LambdaQueryWrapper<SubsidyApply>()
                .eq(SubsidyApply::getBatchId, batch.getId())
                .eq(SubsidyApply::getFinalStatus, 3)
                .isNotNull(SubsidyApply::getClothingSkuId));

        if (applies.isEmpty()) return Collections.emptyList();

        List<Long> studentIds = applies.stream().map(SubsidyApply::getStudentId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = userService.listByIds(studentIds).stream().collect(Collectors.toMap(User::getId, u -> u));

        List<Long> skuIds = applies.stream().map(SubsidyApply::getClothingSkuId).distinct().collect(Collectors.toList());
        Map<Long, Sku> skuMap = skuService.listByIds(skuIds).stream().collect(Collectors.toMap(Sku::getId, s -> s));
        List<Long> clothingIds = skuMap.values().stream().map(Sku::getClothingId).distinct().collect(Collectors.toList());
        Map<Long, Clothing> clothingMap = clothingService.listByIds(clothingIds).stream().collect(Collectors.toMap(Clothing::getId, c -> c));

        List<CollegeStatVO> result = new ArrayList<>();
        for (SubsidyApply apply : applies) {
            User student = userMap.get(apply.getStudentId());
            Sku sku = skuMap.get(apply.getClothingSkuId());
            if (student != null && sku != null) {
                Clothing clothing = clothingMap.get(sku.getClothingId());
                if (clothing != null) {
                    CollegeStatVO vo = new CollegeStatVO();
                    vo.setStudentId(student.getUsername());
                    vo.setName(student.getName());
                    vo.setGender(student.getGender() != null && student.getGender() == 1 ? "M" : "F");
                    vo.setCollege(student.getCollegeName());
                    vo.setClothingId(clothing.getId());
                    vo.setClothingName(clothing.getName());
                    vo.setSkuId(sku.getId());
                    vo.setSize(sku.getSize());
                    vo.setCount(1);
                    result.add(vo);
                }
            }
        }

        return result;
    }

    @Override
    public Map<String, String> exportExcel(String type) {
        Batch batch = getActiveBatch();
        if (batch == null) throw new BusinessException("当前无活动批次");

        List<SubsidyApply> applies = subsidyApplyService.list(new LambdaQueryWrapper<SubsidyApply>()
                .eq(SubsidyApply::getBatchId, batch.getId()));

        if (applies.isEmpty()) throw new BusinessException("暂无数据可导出");

        List<Long> studentIds = applies.stream().map(SubsidyApply::getStudentId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = userService.listByIds(studentIds).stream().collect(Collectors.toMap(User::getId, u -> u));

        List<Long> skuIds = applies.stream().map(SubsidyApply::getClothingSkuId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, Sku> skuMap = skuIds.isEmpty() ? Collections.emptyMap() : skuService.listByIds(skuIds).stream().collect(Collectors.toMap(Sku::getId, s -> s));
        
        List<Long> clothingIds = skuMap.values().stream().map(Sku::getClothingId).distinct().collect(Collectors.toList());
        Map<Long, Clothing> clothingMap = clothingIds.isEmpty() ? Collections.emptyMap() : clothingService.listByIds(clothingIds).stream().collect(Collectors.toMap(Clothing::getId, c -> c));

        ExcelWriter writer = ExcelUtil.getWriter(true);
        String fileName;

        if ("application".equals(type) || "status".equals(type)) { // 兼容前端传递 application 或 status
            fileName = "申请明细表_" + UUID.randomUUID().toString().substring(0, 8) + ".xlsx";
            writer.addHeaderAlias("studentId", "学号");
            writer.addHeaderAlias("name", "姓名");
            writer.addHeaderAlias("college", "学院");
            writer.addHeaderAlias("major", "专业");
            writer.addHeaderAlias("counselorStatus", "辅导员审核");
            writer.addHeaderAlias("collegeStatus", "学院审核");
            writer.addHeaderAlias("schoolStatus", "学校审核");

            List<Map<String, Object>> rows = new ArrayList<>();
            for (SubsidyApply apply : applies) {
                User student = userMap.get(apply.getStudentId());
                if (student != null) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("studentId", student.getUsername());
                    row.put("name", student.getName());
                    row.put("college", student.getCollegeName());
                    row.put("major", student.getClassName());
                    row.put("counselorStatus", getStatusText(apply.getCounselorStatus()));
                    row.put("collegeStatus", getStatusText(apply.getCollegeStatus()));
                    row.put("schoolStatus", getStatusText(apply.getSchoolStatus()));
                    rows.add(row);
                }
            }
            writer.write(rows, true);
        } else if ("summary".equals(type)) {
            fileName = "汇总结果表_" + UUID.randomUUID().toString().substring(0, 8) + ".xlsx";
            writer.addHeaderAlias("studentId", "学号");
            writer.addHeaderAlias("name", "姓名");
            writer.addHeaderAlias("college", "学院");
            writer.addHeaderAlias("major", "专业");
            writer.addHeaderAlias("status", "最终审核状态");
            writer.addHeaderAlias("selection", "选款详情");

            List<Map<String, Object>> rows = new ArrayList<>();
            for (SubsidyApply apply : applies) {
                User student = userMap.get(apply.getStudentId());
                if (student != null) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("studentId", student.getUsername());
                    row.put("name", student.getName());
                    row.put("college", student.getCollegeName());
                    row.put("major", student.getClassName());
                    
                    String finalStatusText = apply.getFinalStatus() == 3 ? "已通过" : (apply.getFinalStatus() == -1 ? "已拒绝" : "审核中");
                    row.put("status", finalStatusText);
                    
                    String selection = "未选款";
                    if (apply.getClothingSkuId() != null) {
                        Sku sku = skuMap.get(apply.getClothingSkuId());
                        if (sku != null) {
                            Clothing clothing = clothingMap.get(sku.getClothingId());
                            if (clothing != null) {
                                selection = clothing.getName() + " - " + sku.getSize() + "码";
                            }
                        }
                    }
                    row.put("selection", selection);
                    rows.add(row);
                }
            }
            writer.write(rows, true);
        } else {
            throw new BusinessException("未知的导出类型");
        }

        writer.autoSizeColumnAll();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.flush(out, true);
        writer.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        String url = minioUtils.uploadFile(in, fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        
        IoUtil.close(out);
        IoUtil.close(in);

        Map<String, String> res = new HashMap<>();
        res.put("downloadUrl", url);
        return res;
    }

    private String getStatusText(Integer status) {
        if (status == null || status == 0 || status == 1 || status == 2) return "待审核";
        if (status == 3) return "已通过";
        if (status == -1) return "已拒绝";
        return "未知";
    }
}
