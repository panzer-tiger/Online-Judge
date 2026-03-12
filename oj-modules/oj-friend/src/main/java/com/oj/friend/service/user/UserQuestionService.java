package com.oj.friend.service.user;

import com.oj.api.domain.vo.UserQuestionResultVO;
import com.oj.common.core.domain.R;
import com.oj.friend.domain.user.dto.UserSubmitDTO;

public interface UserQuestionService {
    R<UserQuestionResultVO> submit(UserSubmitDTO submitDTO);


    boolean rabbitSubmit(UserSubmitDTO submitDTO);

    UserQuestionResultVO exeResult(Long examId, Long questionId, String currentTime);
}
