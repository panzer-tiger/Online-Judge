package com.oj.job.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oj.common.core.constants.CacheConstants;
import com.oj.common.core.constants.Constants;
import com.oj.common.core.enums.ExamListType;
import com.oj.common.redis.service.RedisService;
import com.oj.job.domain.exam.Exam;
import com.oj.job.domain.exam.vo.ExamVO;
import com.oj.job.mapper.exam.ExamMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ExamXxlJob {
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private RedisService redisService;

    @XxlJob("examListOrganizeHandler")
    public void examListOrganizeHandler(){
        log.info("*** examListOrganizeHandler ***");
        List<Exam> examList = new ArrayList<>();
        //查询未完赛的竞赛列表
        List<Exam> unFinishedList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                .gt(Exam::getEndTime, LocalDateTime.now())
                .eq(Exam::getStatus, Constants.TRUE)
                .orderByDesc(Exam::getCreateTime));
        //刷新redis的相应缓存
        refreshCache(unFinishedList,CacheConstants.EXAM_UNFINISHED_LIST);
        //查询历史竞赛
        List<Exam> hisStoryList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                .le(Exam::getEndTime, LocalDateTime.now())
                .eq(Exam::getStatus, Constants.TRUE)
                .orderByDesc(Exam::getCreateTime));
        refreshCache(hisStoryList,CacheConstants.EXAM_HISTORY_LIST);
    }
    public void refreshCache(List<Exam> examList,String examListType) {
        if (CollectionUtil.isEmpty(examList)) {
            return;
        }
        Map<String, Exam> examMap = new HashMap<>();
        List<Long> examIdList = new ArrayList<>();
        for (Exam exam : examList) {
            examMap.put(getDetailKey(exam.getExamId()), exam);
            examIdList.add(exam.getExamId());
        }
        redisService.multiSet(examMap);  //刷新详情缓存
        redisService.deleteObject(examListType);//删除redis中可能有错误的数据
        redisService.rightPushAll(examListType,examIdList);      //刷新列表缓存
    }
    private String getDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL + examId;
    }
}
