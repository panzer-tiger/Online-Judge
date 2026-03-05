package com.oj.common.message.service;

import com.alibaba.fastjson2.JSON;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;

import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

// 业务类逻辑...
@Component
@Slf4j
public class AliSmsService {

    @Autowired
    private Client aliClient;

    // 业务配置
    @Value("${sms.aliyun.templateCode:}")
    private String templateCode;

    @Value("${sms.aliyun.sign-name:}") // 注意：此处修正了拼写，应为 sign-name 而非 sing-name
    private String signName;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否发送成功
     */
    public boolean sendMobileCode(String phone, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        return sendTempMessage(phone, signName, templateCode, params);
    }

    /**
     * 发送模板消息
     *
     * @param phone         手机号
     * @param signName      短信签名
     * @param templateCode  短信模板CODE
     * @param params        模板参数
     * @return 是否发送成功
     */
    public boolean sendTempMessage(String phone, String signName, String templateCode, Map<String, String> params) {
//        SendSmsRequest sendSmsRequest = new SendSmsRequest()
//
        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                .setPhoneNumber(phone)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(JSON.toJSONString(params));
        RuntimeOptions runtimeOptions = new RuntimeOptions();

        try {
            // 使用带 RuntimeOptions 的调用方式，更健壮
            SendSmsVerifyCodeResponse response = aliClient.sendSmsVerifyCodeWithOptions(request,runtimeOptions);
            // 解析响应体
            String code = response.getBody().getCode();
            String message = response.getBody().getMessage();


            if ("OK".equalsIgnoreCase(code)) {
                log.info("短信发送成功, BizId: {}, 手机号: {}", phone);
                return true;
            } else {
                log.error("短信发送失败, 请求: {}, 错误码: {}, 错误信息: {}",
                        JSON.toJSONString(request), code, message);
                return false;
            }
        } catch (Exception e) {
            log.error("短信发送异常, 请求: {}, 异常信息: {}", JSON.toJSONString(request), e.getMessage(), e);
            return false;
        }
    }
}
