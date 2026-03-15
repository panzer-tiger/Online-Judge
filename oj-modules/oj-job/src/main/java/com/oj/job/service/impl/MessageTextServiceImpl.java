package com.oj.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.job.domain.message.MessageText;
import com.oj.job.mapper.message.MessageTextMapper;
import com.oj.job.service.MessageTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MessageTextServiceImpl extends ServiceImpl<MessageTextMapper, MessageText> implements MessageTextService {

    @Override
    public boolean batchInsert(List<MessageText> messageTextList) {
        return saveBatch(messageTextList);
    }
}
