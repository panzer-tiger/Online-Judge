package com.oj.friend.service.question;

import com.oj.common.core.domain.TableDataInfo;

public interface QuestionService {
    TableDataInfo list(com.oj.friend.domain.question.dto.QuestionQueryDTO questionQueryDTO);
}
