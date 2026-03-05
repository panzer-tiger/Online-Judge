package com.oj.friend.controller.exam;

import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.friend.domain.exam.dto.ExamQueryDTO;
import com.oj.friend.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {
    @Autowired
    private ExamService examService;
    @GetMapping("/semiLogin/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO){
        return toList(examService.list(examQueryDTO));
    }
}
