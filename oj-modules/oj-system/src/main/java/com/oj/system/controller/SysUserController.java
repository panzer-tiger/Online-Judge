package com.oj.system.controller;

import com.oj.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    public LoginResult login(String userAccount, String userPassword){
        return sysUserService.login(userAccount,userPassword);
    }
}
