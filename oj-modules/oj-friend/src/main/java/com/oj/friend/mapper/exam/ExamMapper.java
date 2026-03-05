package com.oj.friend.mapper.exam;

import com.oj.friend.domain.exam.dto.ExamQueryDTO;
import com.oj.friend.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamMapper {

    List<ExamVO> selectExamList(ExamQueryDTO examQueryDTO);
}
