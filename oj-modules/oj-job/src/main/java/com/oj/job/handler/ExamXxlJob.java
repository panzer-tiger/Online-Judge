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
import com.oj.job.domain.message.Message;
import com.oj.job.domain.message.MessageText;
import com.oj.job.domain.message.vo.MessageTextVO;
import com.oj.job.domain.user.UserScore;
import com.oj.job.mapper.exam.ExamMapper;
import com.oj.job.mapper.user.UserExamMapper;
import com.oj.job.mapper.user.UserSubmitMapper;
import com.oj.job.service.MessageService;
import com.oj.job.service.MessageTextService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ExamXxlJob {
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserSubmitMapper userSubmitMapper;
    @Autowired
    private UserExamMapper userExamMapper;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageTextService messageTextService;
    @XxlJob("examListOrganizeHandler")
    //统计前一天结束的竞赛,并放入历史竞赛列表中
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
    @XxlJob("examResultHandler")
    //统计前一天结束的竞赛的结果,并放入数据库中
    public void examResultHandler() {
        //计算前一天到现在的时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minusDateTime = now.minusDays(1);
        //查询在这个时间段内已经结束的examid和竞赛标题
        List<Exam> examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle)
                .eq(Exam::getStatus, Constants.TRUE)
                .ge(Exam::getEndTime, minusDateTime)
                .le(Exam::getEndTime, now));
        if (CollectionUtil.isEmpty(examList)) {
            return;
        }
        //用examid去数据库中查询每个竞赛的用户成绩
        Set<Long> examIdSet = examList.stream().map(Exam::getExamId).collect(Collectors.toSet());
        List<UserScore> userScoreList = userSubmitMapper.selectUserScoreList(examIdSet);
        //将examid和examid所对应的所有用户成绩用map形式存储
        Map<Long, List<UserScore>> userScoreMap = userScoreList.stream().collect(Collectors.groupingBy(UserScore::getExamId));
        //创建一个成绩消息发给用户
        createMessage(examList, userScoreMap);
    }
    //创建用户成绩消息
    private void createMessage(List<Exam> examList, Map<Long, List<UserScore>> userScoreMap) {
        List<MessageText> messageTextList = new ArrayList<>();
        List<Message> messageList = new ArrayList<>();
        for (Exam exam : examList) {
            //获取竞赛id和对应的成绩
            Long examId = exam.getExamId();
            List<UserScore> userScoreList = userScoreMap.get(examId);
            //参加竞赛的用户总数
            int totalUser = userScoreList.size();
            int examRank = 1;
            for (UserScore userScore : userScoreList) {
                String msgTitle =  exam.getTitle() + "——排名情况";
                String msgContent = "您所参与的竞赛：" + exam.getTitle()
                        + "，本次参与竞赛一共" + totalUser + "人， 您排名第"  + examRank + "名！";
                userScore.setExamRank(examRank);
                //
                MessageText messageText = new MessageText();
                messageText.setMessageTitle(msgTitle);
                messageText.setMessageContent(msgContent);
                messageText.setCreateBy(Constants.SYSTEM_USER_ID);
                //
                messageTextList.add(messageText);

                Message message = new Message();
                message.setSendId(Constants.SYSTEM_USER_ID);
                message.setCreateBy(Constants.SYSTEM_USER_ID);
                message.setRecId(userScore.getUserId());
                //
                messageList.add(message);
                examRank++;
            }
            //将竞赛的所有用户成绩存到数据库和缓存中
            userExamMapper.updateUserScoreAndRank(userScoreList);
            redisService.rightPushAll(getExamRankListKey(examId), userScoreList);
        }
        //将竞赛成绩消息存入数据库中
        messageTextService.batchInsert(messageTextList);
        //
        Map<String, MessageTextVO> messageTextVOMap = new HashMap<>();
        for (int i = 0; i < messageTextList.size(); i++) {
            //从messageTextList获取数据并转化为前端需要展示的数据类型
            MessageText messageText = messageTextList.get(i);
            MessageTextVO messageTextVO = new MessageTextVO();
            BeanUtil.copyProperties(messageText, messageTextVO);
            //将消息id作为主键,前端需要展示的数据作为value
            String msgDetailKey = getMsgDetailKey(messageText.getTextId());
            messageTextVOMap.put(msgDetailKey, messageTextVO);
            Message message = messageList.get(i);
            message.setTextId(messageText.getTextId());
        }
        messageService.batchInsert(messageList);
        //将用户要接收到的所有消息设置为"userid-message列表"的map结构
        Map<Long, List<Message>> userMsgMap = messageList.stream().collect(Collectors.groupingBy(Message::getRecId));
        Iterator<Map.Entry<Long, List<Message>>> iterator = userMsgMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<Message>> entry = iterator.next();
            Long recId = entry.getKey();
            String userMsgListKey = getUserMsgListKey(recId);
            List<Long> userMsgTextIdList = entry.getValue().stream().map(Message::getTextId).toList();
            //将用户的所有的消息存放到redis中
            redisService.rightPushAll(userMsgListKey, userMsgTextIdList);
        }
        //将消息详情存入redis中
        redisService.multiSet(messageTextVOMap);
    }


    public void refreshCache(List<Exam> examList, String examListKey) {
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
        redisService.deleteObject(examListKey);
        redisService.rightPushAll(examListKey, examIdList);      //刷新列表缓存
    }

    private String getDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL + examId;
    }

    private String getUserMsgListKey(Long userId) {
        return CacheConstants.USER_MESSAGE_LIST + userId;
    }

    private String getMsgDetailKey(Long textId) {
        return CacheConstants.MESSAGE_DETAIL + textId;
    }

    private String getExamRankListKey(Long examId) {
        return CacheConstants.EXAM_RANK_LIST + examId;
    }

}
