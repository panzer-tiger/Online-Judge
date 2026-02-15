package com.oj.system.service;

import com.oj.system.controller.LoginResult;

public interface LoginService {
    LoginResult login(String userAccount, String userPassword);
}
