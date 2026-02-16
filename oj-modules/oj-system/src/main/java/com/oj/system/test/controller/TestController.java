package com.oj.system.test.controller;

import com.oj.system.test.service.TestServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestServiceImpl testService;

    @GetMapping("/list")
    public List<?> list(){
        return testService.list();
    }
    @GetMapping("/log")
    public String log(){
        log.info("你好info");
        log.error("你好error");
        return "我是日志";
    }
}
