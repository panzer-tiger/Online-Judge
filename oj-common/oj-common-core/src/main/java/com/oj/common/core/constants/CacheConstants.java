package com.oj.common.core.constants;

public class CacheConstants {
    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long EXPIRATION = 500;

    /**
     * ⽤⼾⾝份认证缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";
    /**
     * 用户token的延长时间的值,低于这个值token需要延长
     * */
    public static final long REFRESH = 4;
    /**
     *手机验证码
     */
    public static final String PHONE_KEY="p:c:";
    /**
     * 验证码发送的次数
     */
    public static final String SEND_TIME="c:t:";
}