package com.oj.common.message.config;


//import com.aliyun.teaopenapi.Client;
import com.aliyun.credentials.provider.AlibabaCloudCredentialsProvider;
import com.aliyun.teaopenapi.models.Config;
//import com.aliyun.credentials.Client;
import com.aliyun.dypnsapi20170525.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云短信服务客户端配置类。
 * 用于在Spring容器中初始化并管理短信服务的Client实例。
 */
@Configuration
public class AliSmsConfig {

    @Value("${sms.aliyun.accessKeyId:}")
    private String accessKeyId;

    @Value("${sms.aliyun.accessKeySecret:}")
    private String accessKeySecret;

    @Value("${sms.aliyun.endpoint:}")
    private String endpoint;

    /**
     * 创建并配置阿里云号码认证服务（dypnsapi）客户端，适配SendSmsVerifyCodeRequest。
     *
     * @return 初始化完成的dypnsapi Client实例
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean("aliSmsClient")
    public Client aliSmsClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 如果配置文件中未指定endpoint，则使用dypnsapi的默认endpoint
        if (endpoint != null && !endpoint.isEmpty()) {
            config.setEndpoint(endpoint);
        } else {
            config.setEndpoint("dypnsapi.aliyuncs.com");
        }
        // 创建dypnsapi20170525对应的Client实例
        return new Client(config);
    }
}