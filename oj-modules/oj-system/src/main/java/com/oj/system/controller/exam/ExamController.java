package com.oj.system.controller.exam;

import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.R;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.system.domain.exam.dto.ExamAddDTO;
import com.oj.system.domain.exam.dto.ExamEditDTO;
import com.oj.system.domain.exam.dto.ExamQueryDTO;
import com.oj.system.domain.exam.dto.ExamQuestAddDTO;
import com.oj.system.domain.exam.vo.ExamDetailVO;
import com.oj.system.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {
    @Autowired
    private ExamService examService;
    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO){
        return toList(examService.list(examQueryDTO));
    }
    @PostMapping("/add")
    public R<String> add(@RequestBody ExamAddDTO examAddDTO){
        return R.ok(examService.add(examAddDTO));
    }
    @PostMapping("/question/add")
    public R<Void> examQuestionAdd(@RequestBody ExamQuestAddDTO examQuestAddDTO){
        return   toR(examService.examQuestionAdd(examQuestAddDTO));
    }
    @GetMapping("/detail")
    public R<ExamDetailVO> detail(long examId){
        return R.ok(examService.detail(examId));
    }
    @PutMapping("/edit")
    public R<Void> edit(@RequestBody ExamEditDTO examEditDTO){
        return toR(examService.examEdit(examEditDTO));
    }
    @DeleteMapping("/question/delete")
    public R<Void> questionDelete( Long examId,Long questionId){
        return toR(examService.questionDelete(examId,questionId));
    }
    @DeleteMapping("/delete")
    public R<Void> ExamDelete( Long examId){
        return toR(examService.examDelete(examId));
    }
    @PutMapping("/publish")
    public R<Void> publish( Long examId){
        return toR(examService.publish(examId));
    }
    @PutMapping("/cancelPublish")
    public R<Void> cancelPublish(Long examId){
        return toR(examService.cancelPublish(examId));
    }
}
