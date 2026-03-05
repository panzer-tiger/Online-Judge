package com.oj.job.domain.exam.dto;

import com.oj.common.core.domain.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamQueryDTO extends PageQuery {

    private String title;

    private String startTime;

    private String endTime;
}
