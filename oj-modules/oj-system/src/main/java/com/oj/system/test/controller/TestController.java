package com.oj.system.test.controller;

import com.oj.system.test.service.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestServiceImpl testService;

    @GetMapping("/list")
    public List<?> list(){
        return testService.list();
    }
}
