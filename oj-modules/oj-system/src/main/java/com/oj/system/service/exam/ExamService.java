package com.oj.system.service.exam;

import com.oj.common.core.domain.TableDataInfo;
import com.oj.system.domain.exam.dto.ExamAddDTO;
import com.oj.system.domain.exam.dto.ExamEditDTO;
import com.oj.system.domain.exam.dto.ExamQueryDTO;
import com.oj.system.domain.exam.dto.ExamQuestAddDTO;
import com.oj.system.domain.exam.vo.ExamDetailVO;
import com.oj.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamService {
    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    String add(ExamAddDTO examAddDTO);


    boolean examQuestionAdd(ExamQuestAddDTO examQuestAddDTO);

    ExamDetailVO detail(long examId);

    int examEdit(ExamEditDTO examEditDTO);

    int questionDelete(Long examId, Long questionId);
}
