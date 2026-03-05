package com.oj.friend.domain.exam.dto;

import com.oj.common.core.domain.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamRankDTO extends PageQuery {

    private Long examId;
}
