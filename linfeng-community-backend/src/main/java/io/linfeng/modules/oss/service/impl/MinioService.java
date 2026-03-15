package io.linfeng.modules.oss.service.impl;

import cn.hutool.core.date.DateUtil;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

/**
 * @author linfeng
 * @date 2025/3/28 11:16
 */
@Slf4j
@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucketName}")
    private String bucketName;



    /**
     * 初始化 Bucket
     */
    public void init() {

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // 创建存储桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                // 设置存储桶策略为公开读
                String policy = String.format(
                        "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::%s/*\"]}]}",
                        bucketName
                );
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policy)
                                .build()
                );

                log.info("Created bucket: {} and set public policy", bucketName);
            }
        } catch (Exception e) {
            log.error("Error initializing minio bucket", e);
        }
    }

    /**
     * 上传文件并返回永久访问链接
     * 在控制台中：
     * 点击左侧的 "Buckets"
     * 找到 "linfeng" 存储桶
     * 点击 "Edit Policy"
     * 添加以下策略：
     * {
     *     "Version": "2012-10-17",
     *     "Statement": [
     *         {
     *             "Effect": "Allow",
     *             "Principal": {
     *                 "AWS": [
     *                     "*"
     *                 ]
     *             },
     *             "Action": [
     *                 "s3:GetObject"
     *             ],
     *             "Resource": [
     *                 "arn:aws:s3:::linfeng/*"
     *             ]
     *         }
     *     ]
     * }
     */
    public String uploadFile(MultipartFile file) throws Exception {
        // 初始化 MinIO 存储桶
        init();

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String fileName = generateFileName(originalFilename);
        String folder = DateUtil.format(new Date(), "yyyyMMdd");
        fileName = folder + "/" + fileName;
        // 上传文件
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());

        // 返回永久访问链接
        return String.format("%s/%s/%s", endpoint, bucketName, fileName);
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileName) throws Exception {
        // 初始化 MinIO 存储桶
        init();

        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build());
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid + "." + extension;
    }
}
