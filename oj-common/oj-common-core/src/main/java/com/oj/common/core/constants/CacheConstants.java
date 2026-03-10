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
    public final static String EXAM_UNFINISHED_LIST = "e:t:l"; // 未完赛竞赛列表

    public final static String EXAM_HISTORY_LIST = "e:h:l";  // 历史竞赛列表

    public final static String EXAM_DETAIL = "e:d:";    //竞赛详情信息

    public final static String USER_EXAM_LIST = "u:e:l:";   //用户竞赛列表
    public static final String EXAM_RANK_LIST = "e:r:l:"; //竞赛排名
    public static final String EXAM_QUESTION_LIST = "e:q:l:";//竞赛的题目
    public final static long USER_EXP = 10;
    public final static String USER_DETAIL = "u:d:";   //用户详情信息
    public static final String USER_UPLOAD_TIMES_KEY = "u:u:t";
}