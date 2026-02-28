package com.oj.system.service.question;

import com.oj.system.domain.question.dto.QuestionAddDTO;
import com.oj.system.domain.question.dto.QuestionEditDTO;
import com.oj.system.domain.question.dto.QuestionQueryDTO;
import com.oj.system.domain.question.vo.QuestionDetailVO;
import com.oj.system.domain.question.vo.QuestionVO;

import java.util.List;

public interface QuestionService {
    List<QuestionVO> list(QuestionQueryDTO questionQueryDTO);

    int add(QuestionAddDTO questionAddDTO);

    QuestionDetailVO detail(long questionId);

    int edit(QuestionEditDTO questionEditDTO);

    int delete(long questionId);
}
