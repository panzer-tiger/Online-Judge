package com.oj.friend.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oj.common.core.constants.Constants;
import com.oj.common.core.domain.PageQuery;
import com.oj.common.core.domain.TableDataInfo;
import com.oj.common.core.utils.ThreadLocalUtil;
import com.oj.friend.domain.message.vo.MessageTextVO;
import com.oj.friend.manager.MessageCacheManager;
import com.oj.friend.mapper.message.MessageTextMapper;
import com.oj.friend.service.user.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserMessageServiceImpl implements UserMessageService {
    @Autowired
    private MessageCacheManager messageCacheManager;

    @Autowired
    private MessageTextMapper messageTextMapper;

    @Override
    public TableDataInfo list(PageQuery dto) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        Long total = messageCacheManager.getListSize(userId);
        List<MessageTextVO> messageTextVOList;
        if (total == null || total <= 0) {
            //从数据库中查询我的竞赛列表
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
            messageTextVOList = messageTextMapper.selectUserMsgList(userId);
            messageCacheManager.refreshCache(userId);
            total = new PageInfo<>(messageTextVOList).getTotal();
        } else {
            messageTextVOList = messageCacheManager.getMsgTextVOList(dto, userId);
        }
        if (CollectionUtil.isEmpty(messageTextVOList)) {
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(messageTextVOList, total);
    }
}
