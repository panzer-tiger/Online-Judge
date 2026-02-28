package com.oj.system.domain.question.dto;


import com.oj.common.core.domain.PageQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class QuestionQueryDTO extends PageQuery {

    private Integer difficulty;

    private String title;


}
