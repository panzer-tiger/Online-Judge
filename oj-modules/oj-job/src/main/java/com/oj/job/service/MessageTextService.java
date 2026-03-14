package com.oj.job.service;

import com.oj.job.domain.message.MessageText;

import java.util.List;

public interface MessageTextService {

    boolean batchInsert(List<MessageText> messageTextList);
}
