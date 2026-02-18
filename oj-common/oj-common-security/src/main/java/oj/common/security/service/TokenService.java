package oj.common.security.service;

import cn.hutool.core.lang.UUID;
import com.oj.common.core.constants.CacheConstants;
import com.oj.common.core.constants.JwtConstants;
import com.oj.common.core.utils.JwtUtils;
import com.oj.common.redis.service.RedisService;
import com.oj.common.core.domain.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
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
        //将键值对存入redis中
        redisService.setCacheObject(key,loginUser,CacheConstants.EXPIRATION, TimeUnit.MINUTES);
        return token;
    }
}
