package com.oj.friend.controller.exam;

import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.R;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.friend.domain.exam.dto.ExamQueryDTO;
import com.oj.friend.domain.exam.dto.ExamRankDTO;
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
    @GetMapping("/semiLogin/redis/list")
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO){
         return examService.redisList(examQueryDTO);
    }
    @GetMapping("/rank/list")
    public TableDataInfo rankList(ExamRankDTO examRankDTO) {
        return examService.rankList(examRankDTO);
    }

    @GetMapping("/getFirstQuestion")
    public R<String> getFirstQuestion(Long examId) {
        // 代码逻辑： 获取竞赛中题目的顺序列表   先从redis  redis中没有数据查询数据库  list  数据类型  key: e:q:l:examId   value : questionId
        // 排在第一个的题目  返回给前端
        return R.ok(examService.getFirstQuestion(examId));
    }
    @GetMapping("/preQuestion")
    public R<String> preQuestion(Long examId, Long questionId) {
        return R.ok(examService.preQuestion(examId, questionId));
    }

    @GetMapping("/nextQuestion")
    public R<String> nextQuestion(Long examId, Long questionId) {
        return R.ok(examService.nextQuestion(examId, questionId));
    }}
