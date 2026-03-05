package com.oj.friend.domain.exam.dto;

import com.oj.common.core.domain.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamQueryDTO extends PageQuery {

    private String title;

    private String startTime;

    private String endTime;
    //0: 未结束   1:历史竞赛
    private Integer type;
}
