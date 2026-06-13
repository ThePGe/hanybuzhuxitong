package com.miaoqichao.api.controller;

import com.miaoqichao.common.api.CommonResult;
import com.miaoqichao.core.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "文件上传接口")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传图片到MinIO")
    @PostMapping("/upload")
    public CommonResult<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadFile(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return CommonResult.success(result);
    }
}
