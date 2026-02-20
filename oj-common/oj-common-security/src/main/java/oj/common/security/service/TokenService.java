package oj.common.security.service;

import cn.hutool.core.lang.UUID;
import com.oj.common.core.constants.CacheConstants;
import com.oj.common.core.constants.JwtConstants;
import com.oj.common.core.utils.JwtUtils;
import com.oj.common.redis.service.RedisService;
import com.oj.common.core.domain.LoginUser;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TokenService {
    @Autowired
    private RedisService redisService;
    /**
     * 用户登录成功后,将用户的信息存放到token和redis中
     * 验证用户信息时只需要解析token中的特定信息,在redis中查找到即可
     * 未查到就是token过期或验证失败
     * @param secret:jwt生成token所需要的密钥
     * @param Identity:用户是管理员还是普通用户
     * @param userId :用户的id
     * @return 返回jwt创建的token
     * **/
    public String createToken(Long userId,String secret,Integer Identity){
        //创建一个map存放token中的数据
        Map<String, Object> claims = new HashMap<>();
        //创建一个uuid
        String userKey=UUID.fastUUID().toString();
        //向token存放userid
        claims.put(JwtConstants.LOGIN_USER_ID, userId);
        //向token存放uuid
        claims.put(JwtConstants.LOGIN_USER_KEY,userKey);
        //登录成功生成用户的token,返回给客户端
        String token = JwtUtils.createToken(claims, secret);

        //在redis上存储token一个登录对象的实体类, 方便后续业务更改
        LoginUser loginUser = new LoginUser();
        //生成redis的key, key是userId作为身份的唯一标识
        String key= CacheConstants.LOGIN_TOKEN_KEY + userKey;
        //以Identity设置value, 表示用户是否是管理员还是普通用户
        loginUser.setIdentity(Identity);
        //将键值对存入redis中,并设置过期时间
        redisService.setCacheObject(key,loginUser,CacheConstants.EXPIRATION, TimeUnit.MINUTES);
        return token;
    }

    //将获取到的token在redis中查询并延长
    public void extendToken(String token,String secret) {
//
//        String token = (String) claims.get(JwtConstants.LOGIN_USER_KEY);

//        //当token小于一个值时,如果用户仍然在进行操作,需要进行延长
//
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret); //获取令牌中信息 解析payload中信息
            if (claims == null) {
                log.error("处理token:{}出错",token);
                return;
            }
        } catch (Exception e) {
                log.error("处理token:{}出错", token,e);
                return;
        }
        //获取到token中的userKey
        String userKey = JwtUtils.getUserKey(claims);
        //获取到redis中的key
        String tokenKey = getTokenKey(userKey);
        //在redis中查找对应的key的过期时间,
        Long expire = redisService.getExpire(tokenKey, TimeUnit.MINUTES);
        if (expire != null && expire <= CacheConstants.REFRESH) {
            //重新设置token的过期时间
            redisService.expire(token, CacheConstants.EXPIRATION);
        }
    }
    private String getTokenKey(String userKey){
        return CacheConstants.LOGIN_TOKEN_KEY+userKey;
    }

}
