package com.oj.system.domain.exam.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class ExamQuestAddDTO {
    //竞赛id
    private Long examId;
    //存储题目的id,可能有多个题目需要添加
    private LinkedHashSet<Long> questionIdSet;
}
