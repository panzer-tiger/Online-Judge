package com.oj.system.service;

import com.oj.system.controller.LoginResult;

public interface SysUserService {
    LoginResult login(String userAccount, String userPassword);
}
