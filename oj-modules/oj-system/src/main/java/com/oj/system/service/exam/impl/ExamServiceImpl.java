package com.oj.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.oj.common.core.constants.Constants;
import com.oj.common.core.enums.ResultCode;
import com.oj.system.domain.exam.Exam;
import com.oj.system.domain.exam.ExamQuestion;
import com.oj.system.domain.exam.dto.ExamAddDTO;
import com.oj.system.domain.exam.dto.ExamEditDTO;
import com.oj.system.domain.exam.dto.ExamQueryDTO;
import com.oj.system.domain.exam.dto.ExamQuestAddDTO;
import com.oj.system.domain.exam.vo.ExamDetailVO;
import com.oj.system.domain.question.Question;
import com.oj.system.domain.question.vo.QuestionVO;
import com.oj.system.manager.ExamCacheManager;
import com.oj.system.mapper.exam.ExamMapper;
import com.oj.system.mapper.exam.ExamQuestMapper;
import com.oj.system.mapper.question.QuestionMapper;
import com.oj.system.service.exam.ExamService;
import oj.common.security.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamQuestMapper, ExamQuestion> implements ExamService {
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private ExamQuestMapper examQuestMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ExamCacheManager examCacheManager;
    @Override
    public List<com.oj.system.domain.exam.vo.ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(),examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public String add(ExamAddDTO examAddDTO) {
        checkParams(examAddDTO,null);
        Exam exam = new Exam();
        BeanUtil.copyProperties(examAddDTO, exam);
        checkExamStart(exam);
         examMapper.insert(exam);
         //返回新创建的examId
        return exam.getExamId().toString();
    }

    @Override
    public boolean examQuestionAdd(ExamQuestAddDTO examQuestAddDTO) {
        //竞赛id是否存在
        Exam exam = getExam(examQuestAddDTO.getExamId());
        //判断题目是否在数据库中存在
        LinkedHashSet<Long> questionIds = examQuestAddDTO.getQuestionIdSet();
        if(CollectionUtil.isEmpty(questionIds) ){
            return true;
        }
        //将列表中所有待查的题目id进行查询并放到questions中
        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        //当questions的大小小于输入的题目id数量时,则证明输入的题目id有的不对
        if(questions==null || questions.size()<questionIds.size()){
            throw new ServiceException(ResultCode.EXAM_QUESTION_NOT_EXISTS);
        }
        //创建一个新的列表存放竞赛中的所有题目
        List<ExamQuestion> examQuestions = new ArrayList<>();
        return addQuestion(exam, questionIds, examQuestions);
    }

    @Override
    public ExamDetailVO detail(long examId) {
        ExamDetailVO examDetailVO = new ExamDetailVO();
        //获取竞赛的基本信息, 时间,标题
        Exam exam = getExam(examId);
        BeanUtil.copyProperties(exam,examDetailVO);
        //获取竞赛的题目信息
        List<ExamQuestion> examQuestions = examQuestMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                .select(ExamQuestion::getQuestionId)
                .eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getQuestionOrder));
        if(CollectionUtil.isEmpty(examQuestions)){
            return examDetailVO;
        }
        //获取到所有的题目id
        List<Long> questionIds = examQuestions.stream().map(ExamQuestion::getQuestionId).collect(Collectors.toList());
        //根据题目id将所有查询到的题目信息存入questions中
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().
                select(Question::getTitle,Question::getQuestionId,Question::getDifficulty)
                .in(Question::getQuestionId,questionIds));
        //将题目完整的信息转化为前端需要展示的信息返回
        List<QuestionVO> questionVOList=BeanUtil.copyToList(questions,QuestionVO.class);
        examDetailVO.setExamQuestionList(questionVOList);
        return examDetailVO;
    }

    @Override
    public int examEdit(ExamEditDTO examEditDTO) {
        checkParams(examEditDTO,examEditDTO.getExamId());
        Exam exam = getExam(examEditDTO.getExamId());
        checkExamStart(exam);
        //为修改的竞赛进行赋值成更新后的值
        exam.setStartTime(examEditDTO.getStartTime());
        exam.setEndTime(examEditDTO.getEndTime());
        exam.setTitle(examEditDTO.getTitle());
        return examMapper.updateById(exam);
    }

    @Override
    public int questionDelete(Long examId, Long questionId) {
        Exam exam = getExam(examId);
        checkExamStart(exam);
        return examQuestMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getQuestionId,questionId)
                .eq(ExamQuestion::getExamId,examId));
    }

    @Override
    public int examDelete(Long examId) {
        Exam exam = getExam(examId);
        checkExamStart(exam);
        examQuestMapper.delete(new LambdaQueryWrapper<ExamQuestion>().
                eq(ExamQuestion::getExamId,examId));
        return examMapper.deleteById(examId);
    }

    @Override
    public int publish(Long examId) {
        Exam exam = getExam(examId);
        //竞赛已经结束
        if(exam.getEndTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_IS_FINISH);
        }
        checkExamStart(exam);
        //获取竞赛中的题目数量
        Long count = examQuestMapper.selectCount(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId));
        //没有题目就无法发布竞赛
        if(count==null||count<=0){
            throw new ServiceException(ResultCode.EXAM_NOT_HAS_QUESTION);
        }
        //设置竞赛为已经发布状态
        exam.setStatus(Constants.TRUE);
        //将竞赛信息存入redis中
        examCacheManager.addCache(exam);
        return examMapper.updateById(exam);
    }

    @Override
    public int cancelPublish(Long examId) {
        Exam exam = getExam(examId);
        //竞赛已经结束
        if(exam.getEndTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_IS_FINISH);
        }
        checkExamStart(exam);
        exam.setStatus(Constants.FALSE);
        //将取消发布的竞赛从redis中删除
        examCacheManager.deleteCache(examId);
        return examMapper.updateById(exam);
    }

    private void checkParams(ExamAddDTO examAddDTO,Long examId) {
        //查找有无名字重复的竞赛
        List<Exam> list = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getTitle, examAddDTO.getTitle())
                /*修改时,查看修改后的竞赛是否已经存在*/.ne(examId!=null, Exam::getExamId, examId));
        if(CollectionUtil.isNotEmpty(list)){
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }
        //判断开始和结束时间是否合法
        if(examAddDTO.getStartTime().isAfter(examAddDTO.getEndTime())){
            throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
        if(examAddDTO.getStartTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);
        }
    }
    //根据竞赛id获取竞赛信息
    private Exam getExam(long examId) {
        Exam exam = examMapper.selectById(examId);
        if(exam ==null){
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        return exam;
    }

    //向竞赛中添加题目
    private boolean addQuestion(Exam exam, Set<Long> questionIds, List<ExamQuestion> examQuestions) {
        int oder=1;
        for(Long questionId: questionIds){
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExamId(exam.getExamId());
            examQuestion.setQuestionId(questionId);
            examQuestion.setQuestionOrder(oder++);
            examQuestions.add(examQuestion);
        }
        //将所有题目进行多次插入
        return saveBatch(examQuestions);
    }
    private void checkExamStart(Exam exam){
        if(exam.getStartTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_STARTED);
        }
    }
}
