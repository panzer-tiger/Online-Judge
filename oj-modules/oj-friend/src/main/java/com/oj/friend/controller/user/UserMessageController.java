package com.oj.friend.controller.user;

import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.PageQuery;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.friend.service.user.UserMessageService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/message")
public class UserMessageController extends BaseController {

    @Autowired
    private UserMessageService userMessageService;

    @GetMapping("/list")
    public TableDataInfo list(PageQuery dto) {
        return userMessageService.list(dto);
    }
}
