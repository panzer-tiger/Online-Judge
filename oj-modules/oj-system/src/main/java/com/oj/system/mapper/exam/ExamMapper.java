package com.oj.system.mapper.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oj.system.domain.exam.Exam;
import com.oj.system.domain.exam.dto.ExamQueryDTO;

import java.util.List;


public interface ExamMapper extends BaseMapper<Exam> {
    List<com.oj.system.domain.exam.vo.ExamVO> selectExamList(ExamQueryDTO examQueryDTO);
}
