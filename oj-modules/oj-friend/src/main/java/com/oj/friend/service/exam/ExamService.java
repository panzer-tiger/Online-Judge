package com.oj.friend.service.exam;

import com.oj.common.core.domain.TableDataInfo;
import com.oj.friend.domain.exam.dto.ExamQueryDTO;
import com.oj.friend.domain.exam.dto.ExamRankDTO;
import com.oj.friend.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamService {
    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    TableDataInfo redisList(ExamQueryDTO examQueryDTO);

    String getFirstQuestion(Long examId);

    String nextQuestion(Long examId, Long questionId);

    String preQuestion(Long examId, Long questionId);

    TableDataInfo rankList(ExamRankDTO examRankDTO);
}
