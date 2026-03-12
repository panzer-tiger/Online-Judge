package com.oj.system.service.user.impl;

import com.github.pagehelper.PageHelper;
import com.oj.common.core.enums.ResultCode;
import com.oj.system.domain.user.User;
import com.oj.system.domain.user.dto.UserQueryDTO;
import com.oj.system.domain.user.dto.UserUpdateStatusDTO;
import com.oj.system.domain.user.vo.UserVO;
import com.oj.system.manager.UserCacheManager;
import com.oj.system.mapper.user.UserMapper;
import com.oj.system.service.user.UserService;
import oj.common.security.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserCacheManager userCacheManager;
    @Override
    public List<UserVO> list(UserQueryDTO userQueryDTO) {
        PageHelper.startPage(userQueryDTO.getPageNum(),userQueryDTO.getPageSize());
        return userMapper.selectUserList(userQueryDTO);
    }

    @Override
    public int updateUserStatus( UserUpdateStatusDTO userUpdateStatusDTO) {
        User user = userMapper.selectById(userUpdateStatusDTO.getUserId());
        if(user==null){
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setStatus(userUpdateStatusDTO.getStatus());
        userCacheManager.updateStatus(user.getUserId(), userUpdateStatusDTO.getStatus());
        return userMapper.updateById(user);
    }

}
