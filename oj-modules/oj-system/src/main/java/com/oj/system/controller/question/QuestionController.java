package com.oj.system.controller.question;

import com.oj.common.core.controller.BaseController;
import com.oj.common.core.domain.R;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.system.domain.question.Question;
import com.oj.system.domain.question.dto.QuestionAddDTO;
import com.oj.system.domain.question.dto.QuestionEditDTO;
import com.oj.system.domain.question.dto.QuestionQueryDTO;
import com.oj.system.domain.question.vo.QuestionDetailVO;
import com.oj.system.domain.question.vo.QuestionVO;
import com.oj.system.service.question.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
@Tag(name = "题目管理接口")
public class QuestionController extends BaseController {
    @Autowired
    QuestionService questionService;
    @GetMapping("/list")
    @Operation(summary = "获取题目列表", description = "用户输入查询条件进行题目查询")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO){
        return toList(questionService.list(questionQueryDTO));
    }
    @PostMapping ("/add")
    @Operation(summary = "添加题目", description = "用户进行填写题目基本信息后进行添加题目")
    public R<Void> add(@RequestBody QuestionAddDTO questionAddDTO){
       return toR(questionService.add(questionAddDTO));
    }

    @GetMapping("/detail")
    @Operation(summary = "获取题目详情", description = "获取题目详情")
     public R<QuestionDetailVO> detail(long questionId){
        return R.ok(questionService.detail(questionId));
    }

    @PutMapping("/edit")
    @Operation(summary = "修改题目详情", description = "修改题目详情")
    public R<Void> edit(@RequestBody QuestionEditDTO questionEditDTO){
       return toR(questionService.edit(questionEditDTO)) ;

    }

    @DeleteMapping("/del")
    @Operation(summary = "修改题目详情", description = "修改题目详情")
    public R<Void> delete(long questionId){
        return toR(questionService.delete(questionId));
    }
}
