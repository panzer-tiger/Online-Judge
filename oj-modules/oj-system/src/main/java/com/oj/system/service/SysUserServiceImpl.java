package com.oj.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oj.common.core.domain.R;
import com.oj.common.core.enums.ResultCode;
import com.oj.system.domain.LoginDTO;
import com.oj.system.domain.SysUser;
import com.oj.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class SysUserServiceImpl implements SysUserService{
    @Autowired
    SysUserMapper userMapper;
    @Override
    public R<Void> login( LoginDTO loginDTO) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        //将数据库查询结果序列化为SysUser对象
        SysUser sysUser = userMapper.selectOne(
                queryWrapper.select(SysUser::getPassword).eq(SysUser::getUserAccount,loginDTO.getUserAccount()));
        //返回结果的创建
        R<Void> loginResult=new R();
        if(sysUser.getUserAccount()==null){
            loginResult.setCode(ResultCode.FAILED_USER_NOT_EXISTS.getCode());
            loginResult.setMsg(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        //登录成功
        if (sysUser.getPassword().equals(loginDTO.getUserPassword())){
            loginResult.setCode(ResultCode.SUCCESS.getCode());

        }
        //
        loginResult.setCode(ResultCode.FAILED_LOGIN.getCode());
        loginResult.setMsg(ResultCode.FAILED_LOGIN.getMsg());
        return loginResult;
    }
}
