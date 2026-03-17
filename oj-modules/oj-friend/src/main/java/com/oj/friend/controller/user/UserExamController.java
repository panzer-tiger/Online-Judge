package com.oj.friend.controller.user;

import com.oj.common.core.constants.HttpConstants;
import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.R;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.friend.aspect.CheckUserStatus;
import com.oj.friend.domain.exam.dto.ExamDTO;
import com.oj.friend.domain.exam.dto.ExamQueryDTO;
import com.oj.friend.service.user.UserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/exam")
public class UserExamController extends BaseController {
    @Autowired
    private UserExamService userExamService;
    @CheckUserStatus
    @PostMapping("/enter")
    public R<Void> enter (@RequestHeader(HttpConstants.AUTHENTICATION)String token,@RequestBody ExamDTO examDTO){
        return toR(userExamService.enter(token,examDTO));
    }
    //获取用户所有报名的竞赛
    @GetMapping("/list")
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO){
        return userExamService.list(examQueryDTO);
    }
}
