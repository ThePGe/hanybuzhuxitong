package com.miaoqichao.common.utils;

import com.miaoqichao.common.config.MinioConfig;
import com.miaoqichao.common.exception.BusinessException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUtils {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @PostConstruct
    public void init() {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucketName()).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucketName()).build());
                // Set bucket policy to public read
                String policy = "{\n" +
                        "  \"Version\": \"2012-10-17\",\n" +
                        "  \"Statement\": [\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": \"*\",\n" +
                        "      \"Action\": [\"s3:GetObject\"],\n" +
                        "      \"Resource\": [\"arn:aws:s3:::" + minioConfig.getBucketName() + "/*\"]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .config(policy)
                        .build());
            }
        } catch (Exception e) {
            log.error("MinIO bucket init failed", e);
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            
            return uploadFile(file.getInputStream(), fileName, file.getContentType());
        } catch (Exception e) {
            log.error("Upload file failed", e);
            throw new BusinessException("文件上传失败");
        }
    }

    public String uploadFile(InputStream inputStream, String fileName, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(contentType)
                    .build());
            
            return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + fileName;
        } catch (Exception e) {
            log.error("Upload file failed", e);
            throw new BusinessException("文件上传失败");
        }
    }
}