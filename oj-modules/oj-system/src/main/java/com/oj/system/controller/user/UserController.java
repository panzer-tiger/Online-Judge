package com.oj.system.controller.user;

import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.R;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.system.domain.user.dto.UserQueryDTO;
import com.oj.system.domain.user.dto.UserUpdateStatusDTO;
import com.oj.system.service.user.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public R<Void> updateUserStatus(@RequestBody UserUpdateStatusDTO userUpdateStatusDTO){
        return toR(userService.updateUserStatus(userUpdateStatusDTO));
    }

}
