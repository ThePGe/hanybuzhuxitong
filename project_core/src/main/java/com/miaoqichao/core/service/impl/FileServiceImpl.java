package com.miaoqichao.core.service.impl;

import com.miaoqichao.common.config.MinioConfig;
import com.miaoqichao.common.exception.BusinessException;
import com.miaoqichao.core.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : "";
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            // Construct the access URL (assuming public read bucket)
            return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + fileName;
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO", e);
            throw new BusinessException("文件上传失败");
        }
    }
}
