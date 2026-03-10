package com.oj.friend.controller.user;

import com.oj.common.core.constants.HttpConstants;
import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.R;
import com.oj.common.core.domain.vo.LoginUserVO;
import com.oj.friend.domain.user.dto.UserDTO;
import com.oj.friend.domain.user.dto.UserUpdateDTO;
import com.oj.friend.domain.user.vo.UserVO;
import com.oj.friend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestBody UserDTO userDTO) {
        return toR(userService.sendCode(userDTO));
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody UserDTO userDTO) {
        return R.ok(userService.login(userDTO));
    }
    @DeleteMapping("/logout")
    public R<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION)String token ){
        return toR(userService.logout(token));
    }
    @GetMapping("/info")
    public R<LoginUserVO> info(@RequestHeader(HttpConstants.AUTHENTICATION)String token){
        return userService.info(token);
    }
    @GetMapping("/detail")
    public R<UserVO> detail() {
        return R.ok(userService.detail());
    }
    @PutMapping("/edit")
    public R<Void> edit(@RequestBody UserUpdateDTO userUpdateDTO) {
        return toR(userService.edit(userUpdateDTO));
    }
    @PutMapping("/head-image/update")
    public R<Void> updateHeadImage(@RequestBody UserUpdateDTO userUpdateDTO) {
        return toR(userService.updateHeadImage(userUpdateDTO.getHeadImage()));
    }
}


