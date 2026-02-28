package com.oj.common.core.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//分页查询的实体基类
public class PageQuery {
    private Integer pageSize=10; // 一页展示多少数据

    private Integer pageNum=1; // 需要查询第几页的数据
}
