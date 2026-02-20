package com.oj.system.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oj.common.core.domain.R;
import com.oj.common.core.enums.ResultCode;
import com.oj.common.core.enums.UserIdentity;
import com.oj.system.domain.LoginDTO;
import com.oj.system.domain.SysUser;
import com.oj.system.domain.SysUserSaveDTO;
import com.oj.system.mapper.SysUserMapper;
import com.oj.system.util.BCryptUtils;
import oj.common.security.exception.ServiceException;
import oj.common.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RefreshScope
public class SysUserServiceImpl implements SysUserService {
    //在nacos上设置jwt的secret的值,并获取赋值给secret
    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    SysUserMapper userMapper;
    @Autowired
    TokenService tokenService;

    @Override
    public R<String> login(LoginDTO loginDTO) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        //将数据库查询结果序列化为SysUser对象
        SysUser sysUser = userMapper.selectOne(
                queryWrapper
                        .select(SysUser::getPassword,SysUser::getUserId)
                        .eq(SysUser::getUserAccount, loginDTO.getUserAccount()));
        //返回结果的创建
        if (sysUser == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        //登录成功
        if (BCryptUtils.matchesPassword(loginDTO.getPassword(), sysUser.getPassword())) {
            //生成token
            String token = tokenService.createToken(sysUser.getUserId(),secret, UserIdentity.ADMIN.getValue());
            //返回token给客户端
            return R.ok(token);
        }

        return R.fail(ResultCode.FAILED_LOGIN);
    }
    //添加管理员
    @Override
    public int add(SysUserSaveDTO saveDTO) {
        //检查添加的管理员是否已经存在
        List<SysUser> sysUserList = userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserAccount,saveDTO.getUserAccount()));
        //用户存在时则抛出异常
        if(CollectionUtil.isNotEmpty(sysUserList)){
            throw new ServiceException(ResultCode.AILED_USER_EXISTS);
        }
        SysUser user=new SysUser();
        user.setUserAccount(saveDTO.getUserAccount());
        user.setPassword(BCryptUtils.encryptPassword(saveDTO.getPassword()));
        return userMapper.insert(user);
    }
}
