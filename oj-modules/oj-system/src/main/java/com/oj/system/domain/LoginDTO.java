package com.oj.system.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//将用户传入的用户名密码进行封装,提高安全性
public class LoginDTO {
    private String userAccount;
    private String userPassword;
}
