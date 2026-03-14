package com.oj.job.service;

import com.oj.job.domain.message.Message;

import java.util.List;

public interface MessageService {

    boolean batchInsert(List<Message> messageTextList);
}
