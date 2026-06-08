-- =============================================
-- Online Judge 统一数据库初始化脚本
-- 数据库名: online_judge
-- 组件版本: Nacos v2.2.3 / XXL-Job v2.4.0
-- 执行方式: mysql -u root -p < init.sql
-- =============================================

CREATE DATABASE IF NOT EXISTS `online_judge` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `online_judge`;

SET NAMES utf8mb4;

-- =============================================
-- 一、项目业务表
-- =============================================

-- 系统管理员表
CREATE TABLE IF NOT EXISTS `tb_sys_user` (
  `user_id` bigint unsigned NOT NULL COMMENT '用户id（主键）',
  `user_account` varchar(20) NOT NULL COMMENT '账号',
  `nick_name` varchar(20) COMMENT '昵称',
  `password` char(60) NOT NULL COMMENT '密码',
  `create_by` bigint unsigned NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` bigint unsigned COMMENT '更新人',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_user_account` (`user_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统管理员表';

-- 普通用户表
CREATE TABLE IF NOT EXISTS `tb_user` (
  `user_id` bigint unsigned NOT NULL COMMENT '用户id（主键）',
  `nick_name` varchar(20) COMMENT '用户昵称',
  `head_image` varchar(100) COMMENT '用户头像',
  `sex` tinyint COMMENT '用户性别 1:男 2:女',
  `phone` char(11) NOT NULL COMMENT '手机号',
  `code` char(6) COMMENT '验证码',
  `email` varchar(20) COMMENT '邮箱',
  `wechat` varchar(20) COMMENT '微信号',
  `school_name` varchar(20) COMMENT '学校',
  `major_name` varchar(20) COMMENT '专业',
  `introduce` varchar(100) COMMENT '个人介绍',
  `status` tinyint NOT NULL COMMENT '用户状态 0:拉黑 1:正常',
  `create_by` bigint unsigned NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` bigint unsigned COMMENT '更新人',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='普通用户表';

-- 题目表
CREATE TABLE IF NOT EXISTS `tb_question` (
  `question_id` bigint unsigned NOT NULL COMMENT '题目id',
  `title` varchar(50) NOT NULL COMMENT '题目标题',
  `difficulty` tinyint NOT NULL COMMENT '题目难度 1:简单 2:中等 3:困难',
  `time_limit` int NOT NULL COMMENT '时间限制（毫秒）',
  `space_limit` int NOT NULL COMMENT '空间限制（字节）`,
  `content` varchar(1000) NOT NULL COMMENT '题目内容',
  `question_case` varchar(1000) COMMENT '题目用例',
  `default_code` varchar(500) NOT NULL COMMENT '默认代码块',
  `main_fuc` varchar(500) NOT NULL COMMENT 'main函数',
  `create_by` bigint unsigned NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` bigint unsigned COMMENT '更新人',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 竞赛表
CREATE TABLE IF NOT EXISTS `tb_exam` (
  `exam_id` bigint unsigned NOT NULL COMMENT '竞赛id（主键）',
  `title` varchar(50) NOT NULL COMMENT '竞赛标题',
  `start_time` datetime NOT NULL COMMENT '竞赛开始时间',
  `end_time` datetime NOT NULL COMMENT '竞赛结束时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '是否发布 0:未发布 1:已发布',
  `create_by` bigint unsigned NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` bigint unsigned COMMENT '更新人',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞赛表';

-- 竞赛题目关系表
CREATE TABLE IF NOT EXISTS `tb_exam_question` (
  `exam_question_id` bigint unsigned NOT NULL COMMENT '竞赛题目关系id（主键）',
  `question_id` bigint unsigned NOT NULL COMMENT '题目id',
  `exam_id` bigint unsigned NOT NULL COMMENT '竞赛id',
  `question_order` int NOT NULL COMMENT '题目顺序',
  `create_by` bigint unsigned NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` bigint unsigned COMMENT '更新人',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`exam_question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞赛题目关系表';

-- 用户竞赛关系表（报名）
CREATE TABLE IF NOT EXISTS `tb_user_exam` (
  `user_exam_id` bigint unsigned NOT NULL COMMENT '用户竞赛关系id',
  `user_id` bigint unsigned NOT NULL COMMENT '用户id',
  `exam_id` bigint unsigned NOT NULL COMMENT '竞赛id',
  `score` int unsigned COMMENT '得分',
  `exam_rank` int unsigned COMMENT '排名',
  `create_by` bigint unsigned NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` bigint unsigned COMMENT '更新人',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`user_exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户竞赛关系表';

-- 用户提交表
CREATE TABLE IF NOT EXISTS `tb_user_submit` (
  `submit_id` bigint unsigned NOT NULL COMMENT '提交记录id',
  `user_id` bigint unsigned NOT NULL COMMENT '用户id',
  `question_id` bigint unsigned NOT NULL COMMENT '题目id',
  `exam_id` bigint unsigned COMMENT '竞赛id',
  `program_type` tinyint NOT NULL COMMENT '代码类型 0:java 1:CPP',
  `user_code` text NOT NULL COMMENT '用户代码',
  `pass` tinyint NOT NULL COMMENT '0:未通过 1:通过',
  `exe_message` varchar(500) NOT NULL COMMENT '执行结果',
  `score` int NOT NULL DEFAULT '0' COMMENT '得分',
  `create_by` bigint unsigned NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` bigint unsigned COMMENT '更新人',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`submit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户提交表';

-- 消息内容表
CREATE TABLE IF NOT EXISTS `tb_message_text` (
  `text_id` bigint unsigned NOT NULL COMMENT '消息内容id（主键）',
  `message_title` varchar(10) NOT NULL COMMENT '消息标题',
  `message_content` varchar(200) NOT NULL COMMENT '消息内容',
  `create_by` bigint unsigned NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` bigint unsigned COMMENT '更新人',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`text_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息内容表';

-- 消息表
CREATE TABLE IF NOT EXISTS `tb_message` (
  `message_id` bigint unsigned NOT NULL COMMENT '消息id（主键）',
  `text_id` bigint unsigned NOT NULL COMMENT '消息内容id',
  `send_id` bigint unsigned NOT NULL COMMENT '消息发送人id',
  `rec_id` bigint unsigned NOT NULL COMMENT '消息接收人id',
  `create_by` bigint unsigned NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` bigint unsigned COMMENT '更新人',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';


COMMIT;
