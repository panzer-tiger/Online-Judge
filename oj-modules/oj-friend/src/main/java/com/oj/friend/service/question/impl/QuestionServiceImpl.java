package com.oj.friend.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.friend.domain.question.Question;
import com.oj.friend.domain.question.dto.QuestionQueryDTO;
import com.oj.friend.domain.question.es.QuestionES;
import com.oj.friend.domain.question.vo.QuestionDetailVO;
import com.oj.friend.domain.question.vo.QuestionVO;
import com.oj.friend.elasticsearch.QuestionRepository;
import com.oj.friend.manager.QuestionCacheManager;
import com.oj.friend.mapper.question.QuestionMapper;
import com.oj.friend.service.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionCacheManager questionCacheManager;
    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        //查找es中的题目数据
        long count = questionRepository.count();
        if(count<=0){
            //es中无对应数据,转向数据库中查找,并刷新到es中
            refresh(questionQueryDTO);
        }
        //按照题目创建时间降序进行排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(questionQueryDTO.getPageNum() - 1, questionQueryDTO.getPageSize(), sort);
        //获取查询参数
        Integer difficulty = questionQueryDTO.getDifficulty();
        String keyword = questionQueryDTO.getKeyword();
        Page<QuestionES> questionESPage;
        //根据查询参数情况进行查询
        if (difficulty == null && StrUtil.isEmpty(keyword)) {
            questionESPage = questionRepository.findAll(pageable);
        } else if (StrUtil.isEmpty(keyword)) {
            questionESPage = questionRepository.findQuestionByDifficulty(difficulty, pageable);
        } else if (difficulty == null) {
            questionESPage = questionRepository.findByTitleOrContent(keyword, keyword, pageable);
        } else {
            questionESPage = questionRepository.findByTitleOrContentAndDifficulty(keyword, keyword, difficulty, pageable);
        }

        long total = questionESPage.getTotalElements();
        if (total <= 0) {
            return TableDataInfo.empty();
        }
        //将数据转化为前端接收的数据类型和
        List<QuestionES> questionESList = questionESPage.getContent();
        List<QuestionVO> questionVOList = BeanUtil.copyToList(questionESList, QuestionVO.class);
        return TableDataInfo.success(questionVOList, total);
    }

    @Override
    public QuestionDetailVO detail(Long questionId) {
        //从es中查找题目详情
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        if (questionES != null) {
            BeanUtil.copyProperties(questionES, questionDetailVO);
            return questionDetailVO;
        }
        //es中未查找就在数据库中查找
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            return null;
        }
        //
        refreshQuestion();
        BeanUtil.copyProperties(question, questionDetailVO);
        return questionDetailVO;
    }

    @Override
    public String preQuestion(Long questionId) {
        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize <= 0) {
            questionCacheManager.refreshCache();
        }
        return questionCacheManager.preQuestion(questionId).toString();
    }

    @Override
    public String nextQuestion(Long questionId) {
        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize <= 0) {
            questionCacheManager.refreshCache();
        }
        return questionCacheManager.nextQuestion(questionId).toString();
    }

    private void refresh(QuestionQueryDTO questionQueryDTO) {
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>());
        if(questionList==null){
            return;
        }
        //将查询到的数据存入es中
        List<QuestionES> questionESList = BeanUtil.copyToList(questionList,QuestionES.class);
        questionRepository.saveAll(questionESList);
    }
    private void refreshQuestion() {
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>());
        if (CollectionUtil.isEmpty(questionList)) {
            return;
        }
        List<QuestionES> questionESList = BeanUtil.copyToList(questionList, QuestionES.class);
        questionRepository.saveAll(questionESList);
    }

}
