package com.oj.system.controller;

import com.oj.common.core.domain.R;
import com.oj.common.core.enums.ResultCode;
import com.oj.system.domain.LoginDTO;
import com.oj.system.domain.SysUser;
import com.oj.system.domain.SysUserSaveDTO;
import com.oj.system.domain.SysUserVO;
import com.oj.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.DeleteExchange;

@Tag(name = "管理员后台系统")
@RestController
@RequestMapping("/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    //登录方法
    //@RequestBody将前端传过来的数据进行序列化为LoginDTO对象
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "输入用户名密码进行登录操作")
    @ApiResponse(responseCode = "3102", description = "用户不存在")
    @ApiResponse(responseCode = "3103", description = "账号或密码错误")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    public R<Void> login(@RequestBody LoginDTO loginDTO) {
        return sysUserService.login(loginDTO);
    }

    //新增管理员方法
    @PostMapping("/add")
    @Operation(summary = "新增管理员", description = "根据提供的信息新增管理员⽤⼾")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    @ApiResponse(responseCode = "3101", description = "⽤⼾已存在")
    public R<Void> add(@RequestBody SysUserSaveDTO saveDTO) {
        return null;
    }

    @GetMapping("/detail")
    @Operation(summary = "⽤⼾详情", description = "根据查询条件查询⽤⼾详情")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.QUERY, description = "⽤⼾ID"),
            @Parameter(name = "sex", in = ParameterIn.QUERY, description = "⽤⼾性别")
    })
    @ApiResponse(responseCode = "1000", description = "成功获取⽤⼾信息")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    @ApiResponse(responseCode = "3101", description = "⽤⼾不存在")
    public R<SysUserVO> detail(Long userId, @RequestParam(required = false) String sex) {
        return null;
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除⽤⼾", description = "通过⽤⼾id删除⽤⼾")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.PATH, description = "⽤⼾ID")})
    @ApiResponse(responseCode = "1000", description = "成功删除⽤⼾")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    @ApiResponse(responseCode = "3101", description = "⽤⼾不存在")
    public R<Void> delete(@PathVariable Long userId) {
     return null;
    }

}
