package com.oj.system.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResult {
    //code 为0失败 为1成功
    private int code;
    //返回信息
    private String mag;
}
