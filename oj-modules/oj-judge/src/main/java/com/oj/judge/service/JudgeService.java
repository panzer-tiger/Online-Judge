package com.oj.judge.service;

import com.oj.api.domain.dto.JudgeSubmitDTO;
import com.oj.api.domain.vo.UserQuestionResultVO;

public interface JudgeService {
    UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO);
}
