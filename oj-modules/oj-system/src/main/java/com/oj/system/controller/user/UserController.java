package com.oj.system.controller.user;

import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.R;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.system.domain.user.dto.UserQueryDTO;
import com.oj.system.domain.user.dto.UserUpdateStatus;
import com.oj.system.service.user.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/list")
    public TableDataInfo list(UserQueryDTO userQueryDTO){
        return toList(userService.list(userQueryDTO));
    }

    @PutMapping("/updateStatus")
    public R<Void> updateUserStatus(@RequestBody UserUpdateStatus userUpdateStatus){
        return toR(userService.updateUserStatus(userUpdateStatus));
    }

}
