package com.oj.judge.rabbit;

import com.oj.api.domain.dto.JudgeSubmitDTO;
import com.oj.common.core.constants.RabbitMQConstants;
import com.oj.judge.service.JudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JudgeConsumer {

    @Autowired
    private JudgeService judgeService;

    @RabbitListener(queues = RabbitMQConstants.OJ_WORK_QUEUE)
    public void consume(JudgeSubmitDTO judgeSubmitDTO) {
        log.info("收到消息为: {}", judgeSubmitDTO);
        judgeService.doJudgeJavaCode(judgeSubmitDTO);
    }
}
