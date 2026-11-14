package com.oj.friend.test;

import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController extends BaseController {

    @GetMapping("/sendCode")
    public R<Void> sendCode(String phone, String code) {
        // 已移除阿里云短信服务调用，仅打印日志用于本地联调
        log.info("本地模拟发送验证码，手机号: {}, 验证码: {}（未真正发送短信）", phone, code);
        return toR(true);
    }
}
