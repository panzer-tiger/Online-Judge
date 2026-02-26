package com.oj.system.controller.question;

import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.R;
import com.oj.common.core.domain.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
@Tag(name = "题目管理接口")
public class QuestionController extends BaseController {
    @GetMapping("/list")
    public TableDataInfo list(){
        return null;
    }
}
