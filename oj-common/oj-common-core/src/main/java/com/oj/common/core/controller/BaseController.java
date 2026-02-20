package com.oj.common.core.controller;

import com.oj.common.core.domain.R;
   //基础的controller层都会有的功能
public class BaseController {
    //返回值是否大于0来判断结果是否为成功
    public R<Void> toR(int value){
        return value>0 ? R.ok() : R.fail();
    }
    //返回值为boolean来判断结果是否成功
    public R<Void> toR(boolean ret){
        return ret ? R.ok() : R.fail();
    }
}
