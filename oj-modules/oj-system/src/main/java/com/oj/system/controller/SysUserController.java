package com.oj.system.controller;

import com.oj.common.core.domain.R;
import com.oj.system.domain.LoginDTO;
import com.oj.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    //@RequestBody将前端传过来的数据进行序列化为LoginDTO对象
    public R<Void> login(@RequestBody LoginDTO loginDTO){
        return sysUserService.login(loginDTO);
    }
}
