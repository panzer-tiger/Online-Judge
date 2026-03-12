package com.oj.friend.domain.question.dto;

import com.oj.common.core.domain.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionQueryDTO extends PageQuery {

    private String keyword;

    private Integer difficulty;
}
