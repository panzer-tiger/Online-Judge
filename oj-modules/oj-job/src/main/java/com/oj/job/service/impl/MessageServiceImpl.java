package com.oj.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.job.domain.message.Message;
import com.oj.job.mapper.message.MessageMapper;
import com.oj.job.service.MessageService;
import com.oj.job.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public boolean batchInsert(List<Message> messageList) {
        return saveBatch(messageList);
    }
}
