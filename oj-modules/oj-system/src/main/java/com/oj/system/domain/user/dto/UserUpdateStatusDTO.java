package com.oj.system.domain.user.dto;

//@Getter
//@Setter
//@Data
public class UserUpdateStatusDTO {
    private Long userId;
    private Integer status;

    // 必须显式写无参构造（关键！）
    public UserUpdateStatusDTO() {
    }

    // 手动写getter/setter，避免Lombok生成异常
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
