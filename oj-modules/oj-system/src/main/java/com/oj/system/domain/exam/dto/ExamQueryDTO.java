package com.oj.system.domain.exam.dto;

import com.oj.common.core.domain.PageQuery;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExamQueryDTO extends PageQuery {

    private String title;

    private String startTime;

    private String endTime;
}
