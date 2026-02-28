package com.oj.system.mapper.question;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.system.domain.question.Question;
import com.oj.system.domain.question.dto.QuestionQueryDTO;
import com.oj.system.domain.question.vo.QuestionVO;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {

    List<QuestionVO> selectQuestionList(QuestionQueryDTO questionQueryDTO);
}
