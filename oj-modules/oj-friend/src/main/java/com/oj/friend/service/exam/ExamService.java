package com.oj.friend.service.exam;

import com.oj.friend.domain.exam.dto.ExamQueryDTO;
import com.oj.friend.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamService {
    List<ExamVO> list(ExamQueryDTO examQueryDTO);
}
