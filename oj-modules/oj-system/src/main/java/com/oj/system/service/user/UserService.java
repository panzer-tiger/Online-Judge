package com.oj.system.service.user;

import com.oj.system.domain.user.dto.UserQueryDTO;
import com.oj.system.domain.user.dto.UserUpdateStatus;
import com.oj.system.domain.user.vo.UserVO;

import java.util.List;

public interface UserService {
    List<UserVO> list(UserQueryDTO userQueryDTO);

    int updateUserStatus(UserUpdateStatus userUpdateStatus);
}
