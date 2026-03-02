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

    private String excludeIdStr;       //竞赛题目中已经包含的题目id  ;

    private Set<Long> excludeIdSet;    //将所有的题目id存放到一个集合中

}
