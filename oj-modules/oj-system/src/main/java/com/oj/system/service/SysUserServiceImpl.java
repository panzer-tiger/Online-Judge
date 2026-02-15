package com.oj.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oj.system.controller.LoginResult;
import com.oj.system.domain.SysUser;
import com.oj.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class SysUserServiceImpl implements SysUserService{
    @Autowired
    SysUserMapper userMapper;
    @Override
    public LoginResult login(String userAccount, String userPassword) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        //将数据库查询结果序列化为SysUser对象
        SysUser sysUser = userMapper.selectOne(
                queryWrapper.select(SysUser::getPassword).eq(SysUser::getUserAccount,userAccount));
        //返回结果的创建
        LoginResult loginResult=new LoginResult();
        if(sysUser.getUserAccount()==null){
            loginResult.setCode(0);
            loginResult.setMag("用户不存在");
        }
        //登录成功
        if (sysUser.getPassword().equals(userPassword)){
            loginResult.setCode(1);

        }
        //
        loginResult.setCode(0);
        loginResult.setMag("用户名或密码错误");
        return loginResult;
    }
}
