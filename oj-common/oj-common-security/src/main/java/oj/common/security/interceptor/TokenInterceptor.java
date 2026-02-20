package oj.common.security.interceptor;

import cn.hutool.core.util.StrUtil;
import com.oj.common.core.constants.HttpConstants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import oj.common.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
//拦截请求的token并检查是否需要延长
public class TokenInterceptor implements HandlerInterceptor {
    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    TokenService tokenService;
    @Override
    //在用户进行操作前检查token的过期时间
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token=getToken(request);
        tokenService.extendToken(token,secret);
        return true;
    }
    //从请求头中获取token
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpConstants.AUTHENTICATION);
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, "");
        }
        return token;
    }
}
