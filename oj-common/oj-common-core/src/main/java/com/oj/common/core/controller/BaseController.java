package com.oj.common.core.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import com.oj.common.core.domain.R;
import com.oj.common.core.domain.TableDataInfo;

import java.util.List;

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
    public TableDataInfo toList(List<?> list){
        if(CollectionUtil.isEmpty(list)){
            //无查询结果返回一个空的列表
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(list,
                /*pagehelper的查询出所有符合条件结果的总数*/ new PageInfo(list).getTotal());
    }
}
