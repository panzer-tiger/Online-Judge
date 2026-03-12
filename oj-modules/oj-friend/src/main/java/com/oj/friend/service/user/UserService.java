package com.oj.friend.service.user;

import com.oj.common.core.domain.R;
import com.oj.common.core.domain.vo.LoginUserVO;
import com.oj.friend.domain.user.dto.UserDTO;
import com.oj.friend.domain.user.dto.UserUpdateDTO;
import com.oj.friend.domain.user.vo.UserVO;

public interface UserService {
    int sendCode(UserDTO userDTO);

    String login(UserDTO userDTO);

    boolean logout(String token);

    R<LoginUserVO> info(String token);

    UserVO detail();

    int edit(UserUpdateDTO userUpdateDTO);

    int updateHeadImage(String headImage);
}
