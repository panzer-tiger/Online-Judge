package com.oj.friend.service.question;

import com.oj.common.core.domain.TableDataInfo;
import com.oj.friend.domain.question.vo.QuestionDetailVO;

public interface QuestionService {
    TableDataInfo list(com.oj.friend.domain.question.dto.QuestionQueryDTO questionQueryDTO);

    QuestionDetailVO detail(Long questionId);

    String preQuestion(Long questionId);

    String nextQuestion(Long questionId);
}
