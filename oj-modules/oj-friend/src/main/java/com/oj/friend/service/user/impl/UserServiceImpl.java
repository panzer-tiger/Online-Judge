package com.oj.friend.service.user.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oj.common.core.constants.CacheConstants;
import com.oj.common.core.constants.Constants;
import com.oj.common.core.constants.HttpConstants;
import com.oj.common.core.domain.LoginUser;
import com.oj.common.core.domain.R;
import com.oj.common.core.domain.vo.LoginUserVO;
import com.oj.common.core.enums.ResultCode;
import com.oj.common.core.enums.UserIdentity;
import com.oj.common.core.enums.UserStatus;
import com.oj.common.core.utils.ThreadLocalUtil;
import com.oj.common.redis.service.RedisService;
import com.oj.friend.domain.user.User;
import com.oj.friend.domain.user.dto.UserDTO;
import com.oj.friend.domain.user.dto.UserUpdateDTO;
import com.oj.friend.domain.user.vo.UserVO;
import com.oj.friend.manager.UserCacheManager;
import com.oj.friend.mapper.user.UserMapper;
import com.oj.friend.service.user.UserService;
import oj.common.security.exception.ServiceException;
import oj.common.security.service.TokenService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserCacheManager userCacheManager;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${file.oss.downloadUrl}")
    private String downloadUrl;
    //手机验证码持续时间
    private Long phoneExpire= 5l;
    //验证码发送次数
    private Long sendLimit=20l;
    @Override
    public int sendCode(UserDTO userDTO) {
        if(!checkPhone(userDTO.getPhone())){
            throw new ServiceException(ResultCode.FAILED_USER_PHONE);
        }
        String phoneKey = getPhoneKey(userDTO);
        Long expire = redisService.getExpire(phoneKey, TimeUnit.SECONDS);
        //验证码获取时间间隔小于60,且不是第一次发验证码
        if(phoneExpire*60-expire<60&&expire!=null){
            throw new ServiceException(ResultCode.FAILED_FREQUENT);
        }
        //检验发送验证码次数是否到限制
        //每天的验证码获取次数有一个限制  50次  第二天  计数清0 重新开始计数     计数  怎么存  存在哪
        //操作这个次数数据频繁   、 不需要存储、  记录的次数 有有效时间的（当天有效） redis  String  key：c:t:手机号
        //获取已经请求的次数  和50 进行比较     如果大于限制抛出异常。如果不大于限制，正常执行后续逻辑，并且将获取计数 + 1
        String codeTimeKey = getCodeTimeKey(userDTO);
        Long sendTimes = redisService.getCacheObject(codeTimeKey, Long.class);
        if (sendTimes != null && sendTimes >= sendLimit) {
            throw new ServiceException(ResultCode.FAILED_TIME_LIMIT);
        }
//        String code = RandomUtil.randomNumbers(6);
        String code = "1234";
        //生成了随机数验证码,存到redis中
        redisService.setCacheObject(phoneKey,code,phoneExpire,TimeUnit.MINUTES);

        redisService.increment(codeTimeKey);
        if (sendTimes == null) {  //说明是当天第一次发起获取验证码的请求
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
            //创建一个验证码发送次数的键值对,并设置过期时间为当天0点.
            redisService.expire(codeTimeKey, seconds, TimeUnit.SECONDS);
        }
        return 1;
    }

    @Override
    public String login(UserDTO userDTO) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, userDTO.getPhone()));
        String phoneKey = getPhoneKey(userDTO);
        //获取redis中存储的短信验证码,并验证验证码是否有效
        String cacheObject = redisService.getCacheObject(phoneKey, String.class);
        if (StrUtil.isEmpty(cacheObject)) {
            throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
        }
        if (!cacheObject.equals(userDTO.getCode())) {
            throw new ServiceException(ResultCode.FAILED_ERROR_CODE);
        }

        if (user == null) {
            //新用户的注册
            user = new User();
            user.setPhone(userDTO.getPhone());
            user.setStatus(UserStatus.Normal.getStatus());
            userMapper.insert(user);
        }
        //登录并但会token
        //验证完毕后将验证码删除
        redisService.deleteObject(phoneKey);
        //给用户返回token
        return tokenService.createToken(user.getUserId(), secret, UserIdentity.ORDINARY.getValue(),
                user.getNickName(), user.getHeadImage());


    }

    @Override
    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            //替换前端传过来的token转化成可以进行处理的token形式
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        LoginUser loginUser = tokenService.getInfo(token, secret);
        return tokenService.deleteToken(token,secret);
    }

    @Override
    public R<LoginUserVO> info(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        LoginUser loginUser = tokenService.getInfo(token, secret);
        if(loginUser==null)
        {
            return R.fail();
        }
        //将数据转换为前端需要的格式
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setNickName(loginUser.getNickName());
        //从oss中获取数据
        if (StrUtil.isNotEmpty(loginUser.getHeadImage())) {
            loginUserVO.setHeadImage(downloadUrl + loginUser.getHeadImage());
        }

        return R.ok(loginUserVO);
    }
    @Override
    public UserVO detail() {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        UserVO userVO = userCacheManager.getUserById(userId);
        if (userVO == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (StrUtil.isNotEmpty(userVO.getHeadImage())) {
            userVO.setHeadImage(downloadUrl + userVO.getHeadImage());
        }
        return userVO;
    }

    @Override
    public int edit(UserUpdateDTO userUpdateDTO) {
        User user = getUser();
        //设置用户修改的信息
        user.setNickName(userUpdateDTO.getNickName());
        user.setSex(userUpdateDTO.getSex());
        user.setSchoolName(userUpdateDTO.getSchoolName());
        user.setMajorName(userUpdateDTO.getMajorName());
        user.setPhone(userUpdateDTO.getPhone());
        user.setEmail(userUpdateDTO.getEmail());
        user.setWechat(userUpdateDTO.getWechat());
        user.setIntroduce(userUpdateDTO.getIntroduce());
        //更新用户缓存
        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),
                ThreadLocalUtil.get(Constants.USER_KEY, String.class));
        return userMapper.updateById(user);
    }

    @Override
    public int updateHeadImage(String headImage) {
        User user = getUser();
        //传入用户上传的头像
        user.setHeadImage(headImage);
        //更新用户缓存
        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),
                ThreadLocalUtil.get(Constants.USER_KEY, String.class));
        return userMapper.updateById(user);
    }

    @NotNull
    private User getUser() {
        //从线程中拿出userId
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        return user;
    }

    private String getCodeTimeKey(UserDTO userDTO) {
        return CacheConstants.SEND_TIME + userDTO.getPhone();
    }

    //检查手机号格式是否有误
    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }
    @NotNull
    private String getPhoneKey(UserDTO userDTO) {
        return CacheConstants.PHONE_KEY + userDTO.getPhone();
    }
}
