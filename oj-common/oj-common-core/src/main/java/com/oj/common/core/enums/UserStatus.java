package com.oj.common.core.enums;

public enum UserStatus {
    Normal(1),
    Blocked(0),
    ;
    private Integer status;

    UserStatus(int i) {
        this.status=i;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
