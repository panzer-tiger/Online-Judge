package com.oj.system.domain.user.dto;

import com.oj.common.core.domain.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQueryDTO extends PageQuery {

    private Long userId;

    private String nickName;
}
