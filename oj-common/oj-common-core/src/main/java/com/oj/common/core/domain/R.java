package com.oj.common.core.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class R<T> {
    //响应代码
    private int code;
    //响应的信息
    private String msg;
    //泛型类型接收所有的接口返回的数据
    private T data;
}
