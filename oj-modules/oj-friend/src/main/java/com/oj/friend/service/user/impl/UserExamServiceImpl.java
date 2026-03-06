package com.oj.friend.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oj.common.core.constants.Constants;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.common.core.enums.ExamListType;
import com.oj.common.core.enums.ResultCode;
import com.oj.common.core.utils.ThreadLocalUtil;
import com.oj.friend.domain.exam.Exam;
import com.oj.friend.domain.exam.dto.ExamDTO;
import com.oj.friend.domain.exam.dto.ExamQueryDTO;
import com.oj.friend.domain.exam.vo.ExamVO;
import com.oj.friend.domain.user.UserExam;
import com.oj.friend.manager.ExamCacheManager;
import com.oj.friend.mapper.exam.ExamMapper;
import com.oj.friend.mapper.user.UserExamMapper;
import com.oj.friend.service.user.UserExamService;
import oj.common.security.exception.ServiceException;
import oj.common.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserExamServiceImpl implements UserExamService {
    @Autowired
    private UserExamMapper userExamMapper;
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ExamCacheManager examCacheManager;
    @Value("${jwt.secret}")
    private String secret;
    @Override
    public int enter(String token, ExamDTO examDTO) {
        //查到用户想要报名的比赛
        Exam exam = examMapper.selectById(examDTO.getExamId());
        if(exam==null){
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        //报名的比赛已经开赛了,无法报名
        if(exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_STARTED);
        }
        //检查用户想要报名的竞赛是否已经报名过了
//        Long userId = tokenService.getUserId(token,secret);
        Long userId = ThreadLocalUtil.get(Constants.USER_ID,Long.class);
        UserExam userExam = userExamMapper.selectOne(new LambdaQueryWrapper<UserExam>()
                .eq(UserExam::getExamId, examDTO.getExamId())
                .eq(UserExam::getUserId, userId));
        if(userExam!=null){
            //已经报名了
            throw new ServiceException(ResultCode.USER_EXAM_HAS_ENTER);
        }
        examCacheManager.addUserExamCache(userId,examDTO.getExamId());
        userExam = new UserExam();
        userExam.setExamId(examDTO.getExamId());
        userExam.setUserId(userId);
        return userExamMapper.insert(userExam);
    }

    @Override
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        //获取登录用户的id
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        examQueryDTO.setType(ExamListType.USER_EXAM_LIST.getValue());
        Long total = examCacheManager.getListSize(ExamListType.USER_EXAM_LIST.getValue(), userId);
        List<ExamVO> examVOList;
        if (total == null || total <= 0) {
            //从数据库中查询 我的竞赛 列表
            PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
            examVOList = userExamMapper.selectUserExamList(userId);
            examCacheManager.refreshCache(ExamListType.USER_EXAM_LIST.getValue(), userId);
            total = new PageInfo<>(examVOList).getTotal();
        } else {
            examVOList = examCacheManager.getExamVOList(examQueryDTO, userId);
            total = examCacheManager.getListSize(examQueryDTO.getType(), userId);
        }
        if (CollectionUtil.isEmpty(examVOList)) {
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(examVOList, total);
    }
}
