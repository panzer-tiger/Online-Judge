package com.oj.system.domain.sysuser;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oj.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@TableName("tb_sys_user")
public class SysUser extends BaseEntity {
    //以雪花算法"ASSIGN_ID"的方式来生成userId
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;

    private String userAccount;

    private String password;

    private String nickName;

}
