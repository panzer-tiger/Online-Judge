package com.oj.common.core.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUser {
    // 1 为普通用户 2 是管理员用户
    private Integer Identity;
}
