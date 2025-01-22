-- 初始化角色
INSERT INTO ww_role (id, name, code, description, sort, enabled, create_time, update_time, deleted)
VALUES 
(1, '系统管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限', 1, 1, NOW(), NOW(), 0),
(2, '维修人员', 'ROLE_STAFF', '维修人员，处理报修工单', 2, 1, NOW(), NOW(), 0),
(3, '普通用户', 'ROLE_USER', '普通用户，可以提交报修工单', 3, 1, NOW(), NOW(), 0);

-- 初始化权限
INSERT INTO ww_permission (id, name, code, type, parent_id, path, icon, component, sort, enabled, description, create_time, update_time, deleted)
VALUES 
-- 系统管理
(1, '系统管理', 'system', 1, NULL, '/system', 'setting', NULL, 1, 1, '系统管理菜单', NOW(), NOW(), 0),
(2, '用户管理', 'system:user', 1, 1, '/system/user', 'user', 'system/user/index', 1, 1, '用户管理菜单', NOW(), NOW(), 0),
(3, '角色管理', 'system:role', 1, 1, '/system/role', 'team', 'system/role/index', 2, 1, '角色管理菜单', NOW(), NOW(), 0),
(4, '权限管理', 'system:permission', 1, 1, '/system/permission', 'safety', 'system/permission/index', 3, 1, '权限管理菜单', NOW(), NOW(), 0),

-- 工单管理
(10, '工单管理', 'repair', 1, NULL, '/repair', 'tool', NULL, 2, 1, '工单管理菜单', NOW(), NOW(), 0),
(11, '我的工单', 'repair:my', 1, 10, '/repair/my', 'profile', 'repair/my/index', 1, 1, '我的工单菜单', NOW(), NOW(), 0),
(12, '处理工单', 'repair:handle', 1, 10, '/repair/handle', 'form', 'repair/handle/index', 2, 1, '处理工单菜单', NOW(), NOW(), 0),
(13, '工单统计', 'repair:stats', 1, 10, '/repair/stats', 'bar-chart', 'repair/stats/index', 3, 1, '工单统计菜单', NOW(), NOW(), 0),

-- 知识库管理
(20, '知识库管理', 'knowledge', 1, NULL, '/knowledge', 'read', NULL, 3, 1, '知识库管理菜单', NOW(), NOW(), 0),
(21, '分类管理', 'knowledge:category', 1, 20, '/knowledge/category', 'folder', 'knowledge/category/index', 1, 1, '分类管理菜单', NOW(), NOW(), 0),
(22, '文章管理', 'knowledge:article', 1, 20, '/knowledge/article', 'file-text', 'knowledge/article/index', 2, 1, '文章管理菜单', NOW(), NOW(), 0);

-- 初始化角色权限关系
-- 管理员拥有所有权限
INSERT INTO ww_role_permission (role_id, permission_id, create_time, update_time, deleted)
SELECT 1, id, NOW(), NOW(), 0 FROM ww_permission WHERE deleted = 0;

-- 维修人员拥有工单管理权限
INSERT INTO ww_role_permission (role_id, permission_id, create_time, update_time, deleted)
SELECT 2, id, NOW(), NOW(), 0 FROM ww_permission WHERE code LIKE 'repair:%' AND deleted = 0;

-- 普通用户拥有我的工单权限
INSERT INTO ww_role_permission (role_id, permission_id, create_time, update_time, deleted)
VALUES (3, 11, NOW(), NOW(), 0);

-- 初始化管理员账号
-- 密码为admin123，使用BCryptPasswordEncoder加密
INSERT INTO ww_user (id, username, password, phone, email, real_name, student_id, enabled, account_non_expired, account_non_locked, credentials_non_expired, create_time, update_time, deleted)
VALUES (1, 'admin', '$2a$12$U0R7LVOhDGxQUaALnNKIb.IYpE0odEoav3aRZ5ktNblO0Z1O5/mba', '13800138000', 'admin@example.com', '系统管理员', '00000000', 1, 1, 1, 1, NOW(), NOW(), 0);

-- 初始化管理员角色
INSERT INTO ww_user_role (user_id, role_id, create_time, update_time, deleted)
VALUES (1, 1, NOW(), NOW(), 0);

-- 初始化知识库分类
INSERT INTO ww_knowledge_category (id, name, code, parent_id, description, sort, icon, enabled, create_time, update_time, deleted)
VALUES 
(1, '网络故障', 'network', NULL, '常见网络故障及解决方案', 1, 'global', 1, NOW(), NOW(), 0),
(2, '硬件故障', 'hardware', NULL, '常见硬件故障及解决方案', 2, 'laptop', 1, NOW(), NOW(), 0),
(3, '软件故障', 'software', NULL, '常见软件故障及解决方案', 3, 'desktop', 1, NOW(), NOW(), 0),
(4, '账号问题', 'account', NULL, '账号相关问题及解决方案', 4, 'user', 1, NOW(), NOW(), 0),

(11, '网络连接', 'network-connection', 1, '网络连接相关问题', 1, 'link', 1, NOW(), NOW(), 0),
(12, '网络速度', 'network-speed', 1, '网络速度相关问题', 2, 'thunderbolt', 1, NOW(), NOW(), 0),
(13, '网络安全', 'network-security', 1, '网络安全相关问题', 3, 'safety-certificate', 1, NOW(), NOW(), 0),

(21, '电脑故障', 'pc-hardware', 2, '电脑硬件故障', 1, 'desktop', 1, NOW(), NOW(), 0),
(22, '打印机故障', 'printer-hardware', 2, '打印机故障', 2, 'printer', 1, NOW(), NOW(), 0),
(23, '网络设备故障', 'network-hardware', 2, '网络设备故障', 3, 'cluster', 1, NOW(), NOW(), 0),

(31, '系统故障', 'system-software', 3, '操作系统故障', 1, 'windows', 1, NOW(), NOW(), 0),
(32, '应用故障', 'app-software', 3, '应用软件故障', 2, 'appstore', 1, NOW(), NOW(), 0),
(33, '病毒防护', 'virus-software', 3, '病毒防护相关', 3, 'security-scan', 1, NOW(), NOW(), 0),

(41, '密码重置', 'password-reset', 4, '密码重置相关', 1, 'key', 1, NOW(), NOW(), 0),
(42, '账号激活', 'account-activate', 4, '账号激活相关', 2, 'check-circle', 1, NOW(), NOW(), 0),
(43, '权限申请', 'permission-apply', 4, '权限申请相关', 3, 'safety', 1, NOW(), NOW(), 0);

-- 初始化测试用户
-- 普通用户
INSERT INTO ww_user (id, username, password, phone, email, real_name, student_id, enabled, account_non_expired, account_non_locked, credentials_non_expired, create_time, update_time, deleted)
VALUES 
(2, 'testuser', '$2a$12$U0R7LVOhDGxQUaALnNKIb.IYpE0odEoav3aRZ5ktNblO0Z1O5/mba', '13800138001', 'testuser@example.com', '测试用户', '20240001', 1, 1, 1, 1, NOW(), NOW(), 0);

INSERT INTO ww_user_role (user_id, role_id, create_time, update_time, deleted)
VALUES (2, 3, NOW(), NOW(), 0);

-- 维修人员
INSERT INTO ww_user (id, username, password, phone, email, real_name, student_id, enabled, account_non_expired, account_non_locked, credentials_non_expired, create_time, update_time, deleted)
VALUES 
(3, 'teststaff', '$2a$12$U0R7LVOhDGxQUaALnNKIb.IYpE0odEoav3aRZ5ktNblO0Z1O5/mba', '13800138002', 'teststaff@example.com', '测试维修员', '20240002', 1, 1, 1, 1, NOW(), NOW(), 0);

INSERT INTO ww_user_role (user_id, role_id, create_time, update_time, deleted)
VALUES (3, 2, NOW(), NOW(), 0);

-- 注意：测试用户账号
-- 普通用户
-- 用户名：testuser
-- 密码：test123
-- 密码加密方式：BCryptPasswordEncoder
-- 加密后的密码：$2a$12$U0R7LVOhDGxQUaALnNKIb.IYpE0odEoav3aRZ5ktNblO0Z1O5/mba

-- 维修人员
-- 用户名：teststaff
-- 密码：test123
-- 密码加密方式：BCryptPasswordEncoder
-- 加密后的密码：$2a$12$U0R7LVOhDGxQUaALnNKIb.IYpE0odEoav3aRZ5ktNblO0Z1O5/mba