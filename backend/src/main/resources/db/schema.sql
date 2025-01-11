-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `student_id` VARCHAR(50) NOT NULL COMMENT '学号',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `account_non_expired` TINYINT(1) DEFAULT 1 COMMENT '账户是否未过期',
    `account_non_locked` TINYINT(1) DEFAULT 1 COMMENT '账户是否未锁定',
    `credentials_non_expired` TINYINT(1) DEFAULT 1 COMMENT '凭证是否未过期',
    `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
    `login_count` INT DEFAULT 0 COMMENT '登录次数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(255) COMMENT '角色描述',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `code` VARCHAR(50) NOT NULL COMMENT '权限编码',
    `type` TINYINT NOT NULL COMMENT '权限类型：1菜单 2按钮 3接口',
    `parent_id` BIGINT COMMENT '父权限ID',
    `path` VARCHAR(255) COMMENT '权限路径',
    `icon` VARCHAR(100) COMMENT '权限图标',
    `component` VARCHAR(255) COMMENT '组件路径',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `description` VARCHAR(255) COMMENT '权限描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 报修工单表
CREATE TABLE IF NOT EXISTS `repair_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '工单编号',
    `user_id` BIGINT NOT NULL COMMENT '报修用户ID',
    `type` TINYINT NOT NULL COMMENT '工单类型',
    `status` TINYINT NOT NULL COMMENT '工单状态',
    `description` TEXT NOT NULL COMMENT '故障描述',
    `location` VARCHAR(255) NOT NULL COMMENT '故障地点',
    `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `images` TEXT COMMENT '图片URL列表，多个URL用逗号分隔',
    `handler_id` BIGINT COMMENT '处理人ID',
    `handle_start_time` DATETIME COMMENT '处理开始时间',
    `handle_end_time` DATETIME COMMENT '处理完成时间',
    `handle_result` TEXT COMMENT '处理结果描述',
    `handle_remark` TEXT COMMENT '处理备注',
    `rating` TINYINT COMMENT '评分（1-5）',
    `evaluation` TEXT COMMENT '评价内容',
    `evaluation_time` DATETIME COMMENT '评价时间',
    `need_report` TINYINT(1) DEFAULT 0 COMMENT '是否需要上报',
    `report_reason` TEXT COMMENT '上报原因',
    `priority` TINYINT DEFAULT 3 COMMENT '优先级（1-5）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_handler_id` (`handler_id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修工单表';

-- 知识库分类表
CREATE TABLE IF NOT EXISTS `knowledge_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `code` VARCHAR(50) NOT NULL COMMENT '分类编码',
    `parent_id` BIGINT COMMENT '父分类ID',
    `description` VARCHAR(255) COMMENT '分类描述',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `icon` VARCHAR(100) COMMENT '图标',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库分类表';

-- 知识库文章表
CREATE TABLE IF NOT EXISTS `knowledge_article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
    `content` TEXT NOT NULL COMMENT '文章内容',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `author_id` BIGINT NOT NULL COMMENT '作者ID',
    `tags` VARCHAR(255) COMMENT '标签，多个标签用逗号分隔',
    `keywords` VARCHAR(255) COMMENT '关键词，多个关键词用逗号分隔',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT DEFAULT 0 COMMENT '点赞次数',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏次数',
    `is_top` TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    `is_recommend` TINYINT(1) DEFAULT 0 COMMENT '是否推荐',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0草稿 1已发布 2已下架',
    `publish_time` DATETIME COMMENT '发布时间',
    `allow_comment` TINYINT(1) DEFAULT 1 COMMENT '是否允许评论',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库文章表';