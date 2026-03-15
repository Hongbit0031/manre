package io.linfeng.modules.oss.factory;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.comm.SignVersion;
import io.linfeng.common.exception.LinfengException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 阿里云存储 V4签名
 *
 */
public class AliyunCloudStorageService extends CloudStorageService {
    private OSS ossClient;

    public AliyunCloudStorageService(CloudStorageConfig config){
        this.config = config;
        //初始化
        init();
    }

    private void init() {
        // 自动获取 Bucket 区域
        String region = "cn-shenzhen";
        // 正则表达式匹配oss-后面的部分，直到第一个点（.）为止
        String regex = "oss-([^.]+)\\.";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(config.getAliyunEndPoint());
        if (matcher.find()) {
            region = matcher.group(1);
        }
        CredentialsProvider credentialsProvider = new CredentialsProvider(){
            String accessKeyId = config.getAliyunAccessKeyId();
            String accessKeySecrect = config.getAliyunAccessKeySecret();
            @Override
            public void setCredentials(Credentials credentials) {}
            @Override
            public Credentials getCredentials() {
                return new DefaultCredentials(accessKeyId, accessKeySecrect);
            }
        };
        // 创建 OSSClient 实例
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        ossClient = OSSClientBuilder.create()
                .endpoint(config.getAliyunEndPoint())
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();
    }

    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            ossClient.putObject(config.getAliyunBucketName(), path, inputStream);
        } catch (Exception e){
            throw new LinfengException("上传文件失败，请检查配置信息", e);
        }
        ossClient.shutdown();
        return config.getAliyunDomain() + "/" + path;
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getAliyunPrefix(), suffix));
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getAliyunPrefix(), suffix));
    }
}
