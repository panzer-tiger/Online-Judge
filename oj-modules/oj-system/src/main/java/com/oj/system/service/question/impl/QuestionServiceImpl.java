package com.oj.system.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.oj.common.core.enums.ResultCode;
import com.oj.system.domain.question.Question;
import com.oj.system.domain.question.dto.QuestionAddDTO;
import com.oj.system.domain.question.dto.QuestionEditDTO;
import com.oj.system.domain.question.dto.QuestionQueryDTO;
import com.oj.system.domain.question.vo.QuestionDetailVO;
import com.oj.system.domain.question.vo.QuestionVO;
import com.oj.system.mapper.question.QuestionMapper;
import com.oj.system.service.question.QuestionService;
import oj.common.security.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    QuestionMapper questionMapper;
    @Override
    public List<QuestionVO> list(QuestionQueryDTO questionQueryDTO) {
        //将查询到的数据存放到列表
        //pageHelper的自动查询分页功能
        PageHelper.startPage(questionQueryDTO.getPageNum(),questionQueryDTO.getPageSize());
        List<QuestionVO> questionVO = questionMapper.selectQuestionList(questionQueryDTO);
        return questionVO;
    }

    @Override
    public int add(QuestionAddDTO questionAddDTO) {
        //将传过来的数据的标题在数据库中查找是否有一样的
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .eq(Question::getTitle, questionAddDTO.getTitle()));
        //判断标题结果是否一致,一样就不再往数据库中添加
        if (CollectionUtil.isNotEmpty(questionList)) {
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }
        Question question =new Question();
        //将DTO形式转化为数据库存储鹅形式
        BeanUtil.copyProperties(questionAddDTO,question);

        return questionMapper.insert(question);
    }

    @Override
    public QuestionDetailVO detail(long questionId) {
        Question question = questionMapper.selectById(questionId);
        if(question==null){
            throw new ServiceException((ResultCode.FAILED_NOT_EXISTS));
        }
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        BeanUtil.copyProperties(question,questionDetailVO);
        return questionDetailVO;
    }

    @Override
    public int edit(QuestionEditDTO questionEditDTO) {
        Question oldQuestion = questionMapper.selectById(questionEditDTO.getQuestionId());
        if (oldQuestion == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        //修改题目的属性赋值为修改后的
        oldQuestion.setTitle(questionEditDTO.getTitle());
        oldQuestion.setDifficulty(questionEditDTO.getDifficulty());
        oldQuestion.setTimeLimit(questionEditDTO.getTimeLimit());
        oldQuestion.setSpaceLimit(questionEditDTO.getSpaceLimit());
        oldQuestion.setContent(questionEditDTO.getContent());
        oldQuestion.setQuestionCase(questionEditDTO.getQuestionCase());
        oldQuestion.setDefaultCode(questionEditDTO.getDefaultCode());
        oldQuestion.setMainFuc(questionEditDTO.getMainFuc());
        //将新的数据存入数据库中
         return questionMapper.updateById(oldQuestion);
    }

    @Override
    public int delete(long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        //根据id删除数据
        return questionMapper.deleteById(questionId);
    }
}
