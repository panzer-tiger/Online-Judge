package com.oj.friend.service.user;

import com.oj.common.core.domain.TableDataInfo;
import com.oj.friend.domain.exam.dto.ExamDTO;
import com.oj.friend.domain.exam.dto.ExamQueryDTO;

public interface UserExamService {
    int enter(String token, ExamDTO examDTO);

    TableDataInfo list(ExamQueryDTO examQueryDTO);
}
