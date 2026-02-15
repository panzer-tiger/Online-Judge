package com.oj.system.controller;

import com.oj.system.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    public LoginResult login(String userAccount, String userPassword){
        return loginService.login(userAccount,userPassword);
    }
}
