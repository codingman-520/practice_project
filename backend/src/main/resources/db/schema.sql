CREATE DATABASE IF NOT EXISTS `ai_companion` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ai_companion`;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '用户昵称',
    `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色（STUDENT 或 ADMIN）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `skill_tree` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键自增',
    `name` VARCHAR(100) NOT NULL COMMENT '技能名称',
    `description` TEXT COMMENT '技能描述',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父节点ID',
    `category` VARCHAR(50) NOT NULL COMMENT '分类'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能树表';

CREATE TABLE IF NOT EXISTS `user_skill` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键自增',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `skill_id` BIGINT NOT NULL COMMENT '技能ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0未开始 1学习中 2已掌握',
    `learned_at` DATETIME COMMENT '掌握时间',
    UNIQUE KEY `uk_user_skill` (`user_id`, `skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户技能关联表';

-- 初始化顶级分类数据
INSERT IGNORE INTO `skill_tree` (`id`, `name`, `description`, `parent_id`, `category`) VALUES
(1, '前端开发', '前端技术栈体系', 0, 'FRONTEND'),
(2, '后端开发', '后端技术栈体系', 0, 'BACKEND'),
(3, '数据库', '数据库技术栈体系', 0, 'DATABASE'),
(4, 'DevOps', '运维与部署体系', 0, 'DEVOPS'),
(5, '软技能', '工程师软技能体系', 0, 'SOFT_SKILLS');

-- 初始化子节点数据
INSERT IGNORE INTO `skill_tree` (`id`, `name`, `description`, `parent_id`, `category`) VALUES
(6, 'HTML/CSS', '网页基础构建与样式', 1, 'FRONTEND'),
(7, 'JavaScript', '前端核心脚本语言', 1, 'FRONTEND'),
(8, 'Java', 'Java 核心编程语言', 2, 'BACKEND'),
(9, 'Spring Boot', 'Spring 生态核心框架', 2, 'BACKEND'),
(10, 'MySQL', '关系型数据库基础', 3, 'DATABASE');

-- 初始化第三级细分子技能数据
INSERT IGNORE INTO `skill_tree` (`id`, `name`, `description`, `parent_id`, `category`) VALUES
(11, 'Flex 布局', 'CSS 弹性盒子布局', 6, 'FRONTEND'),
(12, 'Grid 布局', 'CSS 网格布局', 6, 'FRONTEND'),
(13, 'ES6+ 语法', '现代 JavaScript 语法特性', 7, 'FRONTEND'),
(14, 'Promise/Async', '异步编程解决方案', 7, 'FRONTEND'),
(15, 'Java 集合框架', 'List, Set, Map 等数据结构', 8, 'BACKEND'),
(16, '多线程与并发', '并发编程基础', 8, 'BACKEND'),
(17, 'Spring MVC', 'Web 层核心组件', 9, 'BACKEND'),
(18, 'Spring Data JPA', '数据访问层组件', 9, 'BACKEND'),
(19, 'SQL 优化', '索引与查询优化', 10, 'DATABASE');

CREATE TABLE IF NOT EXISTS `interview_session` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话唯一标识',
    `user_id` BIGINT NOT NULL COMMENT '面试用户 ID',
    `job_position` VARCHAR(100) NOT NULL COMMENT '目标岗位/关联技能',
    `score` INT COMMENT '面试总分（0-100）',
    `feedback` TEXT COMMENT 'AI 综合考评语',
    `status` VARCHAR(20) DEFAULT 'IN_PROGRESS' COMMENT '状态：IN_PROGRESS / COMPLETED'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试会话表';

CREATE TABLE IF NOT EXISTS `interview_question` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '题目唯一标识',
    `session_id` BIGINT NOT NULL COMMENT '关联会话 ID',
    `question_text` TEXT NOT NULL COMMENT '题目内容',
    `user_answer` TEXT COMMENT '用户作答文本',
    `score` INT COMMENT '单题得分'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试题目关联表';
