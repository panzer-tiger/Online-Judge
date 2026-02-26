package com.oj.system.domain.question.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class QuestionQueryDTO  {

    private Integer difficulty;

    private String title;

    private Integer pageSize=10; // 一页展示多少数据

    private Integer pageNum=1; // 需要查询第几页的数据
}
