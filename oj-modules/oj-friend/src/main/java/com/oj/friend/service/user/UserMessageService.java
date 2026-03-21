package com.oj.friend.service.user;

import com.oj.common.core.domain.PageQuery;
import com.oj.common.core.domain.TableDataInfo;

public interface UserMessageService {
    TableDataInfo list(PageQuery dto);
}
