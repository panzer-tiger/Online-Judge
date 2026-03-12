package com.oj.friend.controller.question;

import com.oj.common.core.domain.R;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.friend.domain.question.vo.QuestionDetailVO;
import com.oj.friend.service.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.oj.friend.domain.question.dto.QuestionQueryDTO;
@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @GetMapping("/semiLogin/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO){
        return questionService.list(questionQueryDTO);
    }
    @GetMapping("/detail")
    public R<QuestionDetailVO> detail(Long questionId) {
        return R.ok(questionService.detail(questionId));
    }
    //题目的顺序列表 : 先从redis  redis中没有数据查询数据库
    // 当前题目是哪个（questionId）
    //redis  list  数据类型  key: q:l value : questionId
    @GetMapping("/preQuestion")
    public R<String> preQuestion(Long questionId) {
        return R.ok(questionService.preQuestion(questionId));
    }

    @GetMapping("/nextQuestion")
    public R<String> nextQuestion(Long questionId) {
        return R.ok(questionService.nextQuestion(questionId));
    }
}
