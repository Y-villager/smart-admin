/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44-log)
 Source Host           : localhost:3306
 Source Schema         : smart_admin_v3

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44-log)
 File Encoding         : 65001

 Date: 07/03/2025 15:18:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `menu_type` int(11) NOT NULL COMMENT '类型',
  `parent_id` bigint(20) NOT NULL COMMENT '父菜单ID',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `perms_type` int(11) NULL DEFAULT NULL COMMENT '权限类型',
  `api_perms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '后端权限字符串',
  `web_perms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '前端权限字符串',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `context_menu_id` bigint(20) NULL DEFAULT NULL COMMENT '功能点关联菜单ID',
  `frame_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为外链',
  `frame_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '外链地址',
  `cache_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否缓存',
  `visible_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '显示状态',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 317 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_menu
-- ----------------------------
INSERT INTO `t_menu` VALUES (26, '菜单管理', 2, 50, 1, '/menu/list', '/system/menu/menu-list.vue', NULL, NULL, NULL, 'CopyOutlined', NULL, 0, NULL, 1, 1, 0, 0, 2, '2021-08-09 15:04:35', 1, '2023-12-01 19:39:03');
INSERT INTO `t_menu` VALUES (40, '删除', 3, 26, NULL, NULL, NULL, 1, 'system:menu:batchDelete', 'system:menu:batchDelete', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 09:45:56', 1, '2023-10-07 18:15:50');
INSERT INTO `t_menu` VALUES (45, '组织架构', 1, 0, 3, '/organization', NULL, NULL, NULL, NULL, 'UserSwitchOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 16:13:27', 1, '2024-07-02 19:27:44');
INSERT INTO `t_menu` VALUES (46, '员工管理', 2, 45, 3, '/organization/employee', '/system/employee/index.vue', NULL, NULL, NULL, 'AuditOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 16:21:50', 1, '2024-07-02 20:15:23');
INSERT INTO `t_menu` VALUES (47, '商品管理', 2, 48, 1, '/erp/goods/list', '/business/erp/goods/goods-list.vue', NULL, NULL, NULL, 'AliwangwangOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2021-08-12 17:58:39', 1, '2023-12-01 19:33:08');
INSERT INTO `t_menu` VALUES (48, '商品管理', 1, 138, 3, '/goods', NULL, NULL, NULL, NULL, 'BarcodeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 18:02:59', 1, '2024-07-08 13:58:46');
INSERT INTO `t_menu` VALUES (50, '系统设置', 1, 0, 6, '/setting', NULL, NULL, NULL, NULL, 'SettingOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-13 16:41:33', 1, '2023-12-01 19:38:03');
INSERT INTO `t_menu` VALUES (76, '角色管理', 2, 45, 4, '/organization/role', '/system/role/index.vue', NULL, NULL, NULL, 'SlidersOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-26 10:31:00', 1, '2024-07-02 20:15:28');
INSERT INTO `t_menu` VALUES (78, '商品分类', 2, 48, 2, '/erp/catalog/goods', '/business/erp/catalog/goods-catalog.vue', NULL, NULL, NULL, 'ApartmentOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2022-05-18 23:34:14', 1, '2023-12-01 19:33:13');
INSERT INTO `t_menu` VALUES (79, '自定义分组', 2, 48, 3, '/erp/catalog/custom', '/business/erp/catalog/custom-catalog.vue', NULL, NULL, NULL, 'AppstoreAddOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-18 23:37:53', 1, '2023-12-01 19:33:16');
INSERT INTO `t_menu` VALUES (81, '用户操作记录', 2, 213, 6, '/support/operate-log/operate-log-list', '/support/operate-log/operate-log-list.vue', NULL, NULL, NULL, 'VideoCameraOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-20 12:37:24', 44, '2024-08-13 14:34:10');
INSERT INTO `t_menu` VALUES (85, '组件演示', 2, 84, NULL, '/demonstration/index', '/support/demonstration/index.vue', NULL, NULL, NULL, 'ClearOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-20 23:16:46', NULL, '2022-05-20 23:16:46');
INSERT INTO `t_menu` VALUES (86, '添加部门', 3, 46, 1, NULL, NULL, 1, 'system:department:add', 'system:department:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-26 23:33:37', 1, '2023-10-07 18:26:35');
INSERT INTO `t_menu` VALUES (87, '修改部门', 3, 46, 2, NULL, NULL, 1, 'system:department:update', 'system:department:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-26 23:34:11', 1, '2023-10-07 18:26:44');
INSERT INTO `t_menu` VALUES (88, '删除部门', 3, 46, 3, NULL, NULL, 1, 'system:department:delete', 'system:department:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-26 23:34:49', 1, '2023-10-07 18:26:49');
INSERT INTO `t_menu` VALUES (91, '添加员工', 3, 46, NULL, NULL, NULL, 1, 'system:employee:add', 'system:employee:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:11:38', 1, '2023-10-07 18:27:46');
INSERT INTO `t_menu` VALUES (92, '编辑员工', 3, 46, NULL, NULL, NULL, 1, 'system:employee:update', 'system:employee:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:10', 1, '2023-10-07 18:27:49');
INSERT INTO `t_menu` VALUES (93, '禁用启用员工', 3, 46, NULL, NULL, NULL, 1, 'system:employee:disabled', 'system:employee:disabled', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:37', 1, '2023-10-07 18:27:53');
INSERT INTO `t_menu` VALUES (94, '调整员工部门', 3, 46, NULL, NULL, NULL, 1, 'system:employee:department:update', 'system:employee:department:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:59', 1, '2023-10-07 18:27:34');
INSERT INTO `t_menu` VALUES (95, '重置密码', 3, 46, NULL, NULL, NULL, 1, 'system:employee:password:reset', 'system:employee:password:reset', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:13:30', 1, '2023-10-07 18:27:57');
INSERT INTO `t_menu` VALUES (96, '删除员工', 3, 46, NULL, NULL, NULL, 1, 'system:employee:delete', 'system:employee:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:14:08', 1, '2023-10-07 18:28:01');
INSERT INTO `t_menu` VALUES (97, '添加角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:add', 'system:role:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:00', 1, '2023-10-07 18:42:31');
INSERT INTO `t_menu` VALUES (98, '删除角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:delete', 'system:role:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:19', 1, '2023-10-07 18:42:35');
INSERT INTO `t_menu` VALUES (99, '编辑角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:update', 'system:role:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:55', 1, '2023-10-07 18:42:44');
INSERT INTO `t_menu` VALUES (100, '更新数据范围', 3, 76, NULL, NULL, NULL, 1, 'system:role:dataScope:update', 'system:role:dataScope:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:37:03', 1, '2023-10-07 18:41:49');
INSERT INTO `t_menu` VALUES (101, '批量移除员工', 3, 76, NULL, NULL, NULL, 1, 'system:role:employee:batch:delete', 'system:role:employee:batch:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:05', 1, '2023-10-07 18:43:32');
INSERT INTO `t_menu` VALUES (102, '移除员工', 3, 76, NULL, NULL, NULL, 1, 'system:role:employee:delete', 'system:role:employee:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:21', 1, '2023-10-07 18:43:37');
INSERT INTO `t_menu` VALUES (103, '添加员工', 3, 76, NULL, NULL, NULL, 1, 'system:role:employee:add', 'system:role:employee:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:38', 1, '2023-10-07 18:44:05');
INSERT INTO `t_menu` VALUES (104, '修改权限', 3, 76, NULL, NULL, NULL, 1, 'system:role:menu:update', 'system:role:menu:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:41:55', 1, '2023-10-07 18:44:11');
INSERT INTO `t_menu` VALUES (105, '添加', 3, 26, NULL, NULL, NULL, 1, 'system:menu:add', 'system:menu:add', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:44:37', 1, '2023-10-07 17:35:35');
INSERT INTO `t_menu` VALUES (106, '编辑', 3, 26, NULL, NULL, NULL, 1, 'system:menu:update', 'system:menu:update', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:44:59', 1, '2023-10-07 17:35:48');
INSERT INTO `t_menu` VALUES (109, '参数配置', 2, 50, 3, '/config/config-list', '/support/config/config-list.vue', NULL, NULL, NULL, 'AntDesignOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 13:34:41', 1, '2022-06-23 16:24:16');
INSERT INTO `t_menu` VALUES (110, '数据字典', 2, 50, 4, '/setting/dict', '/support/dict/index.vue', NULL, NULL, NULL, 'BarcodeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 17:53:00', 1, '2022-05-27 18:09:14');
INSERT INTO `t_menu` VALUES (111, '监控服务', 1, 0, 100, '/monitor', NULL, NULL, NULL, NULL, 'BarChartOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-17 11:13:23', 1, '2023-11-28 17:43:56');
INSERT INTO `t_menu` VALUES (113, '查询', 3, 112, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-17 11:31:36', NULL, '2025-03-06 09:19:32');
INSERT INTO `t_menu` VALUES (114, '运维工具', 1, 0, 200, NULL, NULL, NULL, NULL, NULL, 'NodeCollapseOutlined', NULL, 0, NULL, 0, 1, 0, 1, 1, '2022-06-20 10:09:16', 1, '2023-12-01 19:36:18');
INSERT INTO `t_menu` VALUES (117, 'Reload', 2, 50, 12, '/hook', '/support/reload/reload-list.vue', NULL, NULL, NULL, 'ReloadOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-20 10:16:49', 1, '2023-12-01 19:39:17');
INSERT INTO `t_menu` VALUES (122, '数据库监控', 2, 111, 4, '/support/druid/index', NULL, NULL, NULL, NULL, 'ConsoleSqlOutlined', NULL, 1, 'http://localhost:1024/druid', 1, 1, 0, 0, 1, '2022-06-20 14:49:33', 1, '2023-02-16 19:15:58');
INSERT INTO `t_menu` VALUES (130, '单号管理', 2, 50, 6, '/support/serial-number/serial-number-list', '/support/serial-number/serial-number-list.vue', NULL, NULL, NULL, 'NumberOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 14:45:22', 1, '2022-06-28 16:23:41');
INSERT INTO `t_menu` VALUES (132, '公告管理', 2, 138, 2, '/oa/notice/notice-list', '/business/oa/notice/notice-list.vue', NULL, NULL, NULL, 'SoundOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2022-06-24 18:23:09', 1, '2024-07-08 13:58:51');
INSERT INTO `t_menu` VALUES (133, '缓存管理', 2, 50, 11, '/support/cache/cache-list', '/support/cache/cache-list.vue', NULL, NULL, NULL, 'BorderInnerOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 18:52:25', 1, '2023-12-01 19:39:13');
INSERT INTO `t_menu` VALUES (138, '功能Demo', 1, 0, 1, NULL, NULL, NULL, NULL, NULL, 'BankOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 20:09:18', 1, '2024-07-08 13:46:54');
INSERT INTO `t_menu` VALUES (142, '公告详情', 2, 132, NULL, '/oa/notice/notice-detail', '/business/oa/notice/notice-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-06-25 16:38:47', 1, '2022-09-14 19:46:17');
INSERT INTO `t_menu` VALUES (143, '登录登出记录', 2, 213, 5, '/support/login-log/login-log-list', '/support/login-log/login-log-list.vue', NULL, NULL, NULL, 'LoginOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-28 15:01:38', 44, '2024-08-13 14:33:49');
INSERT INTO `t_menu` VALUES (144, '企业管理', 2, 138, 1, '/oa/enterprise/enterprise-list', '/business/oa/enterprise/enterprise-list.vue', NULL, NULL, NULL, 'ShopOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 17:00:07', 1, '2024-07-08 13:48:24');
INSERT INTO `t_menu` VALUES (145, '企业详情', 2, 138, NULL, '/oa/enterprise/enterprise-detail', '/business/oa/enterprise/enterprise-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-09-14 18:52:52', 1, '2022-11-22 10:39:07');
INSERT INTO `t_menu` VALUES (147, '帮助文档', 2, 218, 1, '/help-doc/help-doc-manage-list', '/support/help-doc/management/help-doc-manage-list.vue', NULL, NULL, NULL, 'FolderViewOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 19:59:01', 1, '2023-12-01 19:38:23');
INSERT INTO `t_menu` VALUES (148, '意见反馈', 2, 218, 2, '/feedback/feedback-list', '/support/feedback/feedback-list.vue', NULL, NULL, NULL, 'CoffeeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 19:59:52', 1, '2023-12-01 19:38:40');
INSERT INTO `t_menu` VALUES (149, '我的通知', 2, 132, NULL, '/oa/notice/notice-employee-list', '/business/oa/notice/notice-employee-list.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-09-14 20:29:41', 1, '2022-09-14 20:31:23');
INSERT INTO `t_menu` VALUES (150, '我的通知公告详情', 2, 132, NULL, '/oa/notice/notice-employee-detail', '/business/oa/notice/notice-employee-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-09-14 20:30:25', 1, '2022-09-14 20:31:38');
INSERT INTO `t_menu` VALUES (151, '代码生成', 2, 0, 600, '/support/code-generator', '/support/code-generator/code-generator-list.vue', NULL, NULL, NULL, 'CoffeeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-21 18:25:05', 1, '2022-10-22 11:27:58');
INSERT INTO `t_menu` VALUES (152, '更新日志', 2, 218, 3, '/support/change-log/change-log-list', '/support/change-log/change-log-list.vue', NULL, NULL, NULL, 'HeartOutlined', NULL, 0, NULL, 0, 1, 0, 0, 44, '2022-10-10 10:31:20', 1, '2023-12-01 19:38:51');
INSERT INTO `t_menu` VALUES (153, '清除缓存', 3, 133, NULL, NULL, NULL, 1, 'support:cache:delete', 'support:cache:delete', NULL, 133, 0, NULL, 0, 1, 1, 0, 1, '2022-10-15 22:45:13', 1, '2023-10-07 16:22:29');
INSERT INTO `t_menu` VALUES (154, '获取缓存key', 3, 133, NULL, NULL, NULL, 1, 'support:cache:keys', 'support:cache:keys', NULL, 133, 0, NULL, 0, 1, 1, 0, 1, '2022-10-15 22:45:48', 1, '2023-10-07 16:22:35');
INSERT INTO `t_menu` VALUES (156, '查看结果', 3, 117, NULL, NULL, NULL, 1, 'support:reload:result', 'support:reload:result', NULL, 117, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:17:23', 1, '2023-10-07 14:31:47');
INSERT INTO `t_menu` VALUES (157, '单号生成', 3, 130, NULL, NULL, NULL, 1, 'support:serialNumber:generate', 'support:serialNumber:generate', NULL, 130, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:21:06', 1, '2023-10-07 18:22:46');
INSERT INTO `t_menu` VALUES (158, '生成记录', 3, 130, NULL, NULL, NULL, 1, 'support:serialNumber:record', 'support:serialNumber:record', NULL, 130, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:21:34', 1, '2023-10-07 18:22:55');
INSERT INTO `t_menu` VALUES (159, '新建', 3, 110, NULL, NULL, NULL, 1, 'support:dict:add', 'support:dict:add', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:23:51', 1, '2023-10-07 18:18:24');
INSERT INTO `t_menu` VALUES (160, '编辑', 3, 110, NULL, NULL, NULL, 1, 'support:dict:edit', 'support:dict:edit', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:05', 1, '2023-10-07 18:19:17');
INSERT INTO `t_menu` VALUES (161, '批量删除', 3, 110, NULL, NULL, NULL, 1, 'support:dict:delete', 'support:dict:delete', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:34', 1, '2023-10-07 18:19:39');
INSERT INTO `t_menu` VALUES (162, '刷新缓存', 3, 110, NULL, NULL, NULL, 1, 'support:dict:refresh', 'support:dict:refresh', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:55', 1, '2023-10-07 18:18:37');
INSERT INTO `t_menu` VALUES (163, '新建', 3, 109, NULL, NULL, NULL, 1, 'support:config:add', 'support:config:add', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:26:56', 1, '2023-10-07 18:16:17');
INSERT INTO `t_menu` VALUES (164, '编辑', 3, 109, NULL, NULL, NULL, 1, 'support:config:update', 'support:config:update', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:27:07', 1, '2023-10-07 18:16:24');
INSERT INTO `t_menu` VALUES (165, '查询', 3, 47, NULL, NULL, NULL, 1, 'goods:query', 'goods:query', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 19:55:39', 1, '2023-10-07 13:58:28');
INSERT INTO `t_menu` VALUES (166, '新建', 3, 47, NULL, NULL, NULL, 1, 'goods:add', 'goods:add', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 19:56:00', 1, '2023-10-07 13:58:32');
INSERT INTO `t_menu` VALUES (167, '批量删除', 3, 47, NULL, NULL, NULL, 1, 'goods:batchDelete', 'goods:batchDelete', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 19:56:15', 1, '2023-10-07 13:58:35');
INSERT INTO `t_menu` VALUES (168, '查询', 3, 147, 11, NULL, NULL, 1, 'support:helpDoc:query', 'support:helpDoc:query', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:13', 1, '2023-10-07 14:05:49');
INSERT INTO `t_menu` VALUES (169, '新建', 3, 147, 12, NULL, NULL, 1, 'support:helpDoc:add', 'support:helpDoc:add', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:37', 1, '2023-10-07 14:05:56');
INSERT INTO `t_menu` VALUES (170, '新建目录', 3, 147, 1, NULL, NULL, 1, 'support:helpDocCatalog:addCategory', 'support:helpDocCatalog:addCategory', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:57', 1, '2023-10-07 14:06:38');
INSERT INTO `t_menu` VALUES (171, '修改目录', 3, 147, 2, NULL, NULL, 1, 'support:helpDocCatalog:update', 'support:helpDocCatalog:update', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:13:46', 1, '2023-10-07 14:06:49');
INSERT INTO `t_menu` VALUES (173, '新建', 3, 78, NULL, NULL, NULL, 1, 'category:add', 'category:add', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:02', 1, '2023-10-07 13:54:01');
INSERT INTO `t_menu` VALUES (174, '查询', 3, 78, NULL, NULL, NULL, 1, 'category:tree', 'category:tree', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:22', 1, '2023-10-07 13:54:33');
INSERT INTO `t_menu` VALUES (175, '编辑', 3, 78, NULL, NULL, NULL, 1, 'category:update', 'category:update', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:38', 1, '2023-10-07 13:54:18');
INSERT INTO `t_menu` VALUES (176, '删除', 3, 78, NULL, NULL, NULL, 1, 'category:delete', 'category:delete', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:50', 1, '2023-10-07 13:54:27');
INSERT INTO `t_menu` VALUES (177, '新建', 3, 79, NULL, NULL, NULL, 1, 'category:add', 'custom:category:add', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:02', 1, '2023-10-07 13:57:32');
INSERT INTO `t_menu` VALUES (178, '查询', 3, 79, NULL, NULL, NULL, 1, 'category:tree', 'custom:category:tree', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:22', 1, '2023-10-07 13:57:50');
INSERT INTO `t_menu` VALUES (179, '编辑', 3, 79, NULL, NULL, NULL, 1, 'category:update', 'custom:category:update', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:38', 1, '2023-10-07 13:58:02');
INSERT INTO `t_menu` VALUES (180, '删除', 3, 79, NULL, NULL, NULL, 1, 'category:delete', 'custom:category:delete', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:50', 1, '2023-10-07 13:58:12');
INSERT INTO `t_menu` VALUES (181, '查询', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:query', 'oa:enterprise:query', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:14', 1, '2023-10-07 12:00:09');
INSERT INTO `t_menu` VALUES (182, '新建', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:add', 'oa:enterprise:add', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:25', 1, '2023-10-07 12:00:17');
INSERT INTO `t_menu` VALUES (183, '编辑', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:update', 'oa:enterprise:update', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:36', 1, '2023-10-07 12:00:38');
INSERT INTO `t_menu` VALUES (184, '删除', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:delete', 'oa:enterprise:delete', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:53', 1, '2023-10-07 12:00:46');
INSERT INTO `t_menu` VALUES (185, '查询', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:query', 'oa:notice:query', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:26:38', 1, '2023-10-07 11:43:01');
INSERT INTO `t_menu` VALUES (186, '新建', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:add', 'oa:notice:add', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:27:04', 1, '2023-10-07 11:43:07');
INSERT INTO `t_menu` VALUES (187, '编辑', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:update', 'oa:notice:update', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:27:15', 1, '2023-10-07 11:43:12');
INSERT INTO `t_menu` VALUES (188, '删除', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:delete', 'oa:notice:delete', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:27:23', 1, '2023-10-07 11:43:18');
INSERT INTO `t_menu` VALUES (190, '查询', 3, 152, NULL, NULL, NULL, 1, '', 'support:changeLog:query', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:28:33', 1, '2023-10-07 14:25:05');
INSERT INTO `t_menu` VALUES (191, '新建', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:add', 'support:changeLog:add', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:28:46', 1, '2023-10-07 14:24:15');
INSERT INTO `t_menu` VALUES (192, '批量删除', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:batchDelete', 'support:changeLog:batchDelete', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:29:10', 1, '2023-10-07 14:24:22');
INSERT INTO `t_menu` VALUES (193, '文件管理', 2, 50, 20, '/support/file/file-list', '/support/file/file-list.vue', NULL, NULL, NULL, 'FolderOpenOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 11:26:11', 1, '2022-10-22 11:29:22');
INSERT INTO `t_menu` VALUES (194, '删除', 3, 47, NULL, NULL, NULL, 1, 'goods:delete', 'goods:delete', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:00:12', 1, '2023-10-07 13:58:39');
INSERT INTO `t_menu` VALUES (195, '修改', 3, 47, NULL, NULL, NULL, 1, 'goods:update', 'goods:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:05:23', 1, '2023-10-07 13:58:42');
INSERT INTO `t_menu` VALUES (196, '查看详情', 3, 145, NULL, NULL, NULL, 1, 'oa:enterprise:detail', 'oa:enterprise:detail', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:16:47', 1, '2023-10-07 11:48:59');
INSERT INTO `t_menu` VALUES (198, '删除', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:delete', 'support:changeLog:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:42:34', 1, '2023-10-07 14:24:32');
INSERT INTO `t_menu` VALUES (199, '查询', 3, 109, NULL, NULL, NULL, 1, 'support:config:query', 'support:config:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:45:14', 1, '2023-10-07 18:16:27');
INSERT INTO `t_menu` VALUES (200, '查询', 3, 193, NULL, NULL, NULL, 1, 'support:file:query', 'support:file:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:47:23', 1, '2023-10-07 18:24:43');
INSERT INTO `t_menu` VALUES (201, '删除', 3, 147, 14, NULL, NULL, 1, 'support:helpDoc:delete', 'support:helpDoc:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 21:03:20', 1, '2023-10-07 14:07:02');
INSERT INTO `t_menu` VALUES (202, '更新', 3, 147, 13, NULL, NULL, 1, 'support:helpDoc:update', 'support:helpDoc:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 21:03:32', 1, '2023-10-07 14:06:56');
INSERT INTO `t_menu` VALUES (203, '查询', 3, 143, NULL, NULL, NULL, 1, 'support:loginLog:query', 'support:loginLog:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 21:05:11', 1, '2023-10-07 14:27:23');
INSERT INTO `t_menu` VALUES (204, '查询', 3, 81, NULL, NULL, NULL, 1, 'support:operateLog:query', 'support:operateLog:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 10:33:31', 1, '2023-10-07 14:27:56');
INSERT INTO `t_menu` VALUES (205, '详情', 3, 81, NULL, NULL, NULL, 1, 'support:operateLog:detail', 'support:operateLog:detail', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 10:33:49', 1, '2023-10-07 14:28:04');
INSERT INTO `t_menu` VALUES (206, '心跳监控', 2, 111, 1, '/support/heart-beat/heart-beat-list', '/support/heart-beat/heart-beat-list.vue', 1, NULL, NULL, 'FallOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 10:47:03', 1, '2022-10-22 18:32:52');
INSERT INTO `t_menu` VALUES (207, '更新', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:update', 'support:changeLog:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 11:51:32', 1, '2023-10-07 14:24:39');
INSERT INTO `t_menu` VALUES (212, '查询', 3, 117, NULL, NULL, NULL, 1, 'support:reload:query', 'support:reload:query', NULL, NULL, 0, NULL, 1, 1, 1, 0, 1, '2023-10-07 14:31:36', NULL, '2023-10-07 14:31:36');
INSERT INTO `t_menu` VALUES (213, '网络安全', 1, 0, 5, NULL, NULL, 1, NULL, NULL, 'SafetyCertificateOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-10-17 19:03:08', 1, '2023-12-01 19:38:00');
INSERT INTO `t_menu` VALUES (214, '登录失败锁定', 2, 213, 4, '/support/login-fail', '/support/login-fail/login-fail-list.vue', 1, NULL, NULL, 'LockOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-10-17 19:04:24', 44, '2024-08-13 14:16:26');
INSERT INTO `t_menu` VALUES (215, '接口加解密', 2, 213, 2, '/support/api-encrypt', '/support/api-encrypt/api-encrypt-index.vue', 1, NULL, NULL, 'CodepenCircleOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-10-24 11:49:28', 44, '2024-08-13 12:00:14');
INSERT INTO `t_menu` VALUES (216, '商品导出', 3, 47, NULL, NULL, NULL, 1, 'goods:export', 'goods:export', NULL, NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-12-01 19:34:03', NULL, '2025-03-06 09:12:06');
INSERT INTO `t_menu` VALUES (217, '商品导入', 3, 47, 3, NULL, NULL, 1, 'goods:import', 'goods:import', NULL, NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-12-01 19:34:22', NULL, '2025-03-06 09:02:24');
INSERT INTO `t_menu` VALUES (218, '文档中心', 1, 0, 4, NULL, NULL, 1, NULL, NULL, 'FileSearchOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-12-01 19:37:28', 1, '2023-12-01 19:37:51');
INSERT INTO `t_menu` VALUES (219, '部门管理', 2, 45, 1, '/organization/department', '/system/department/department-list.vue', 1, NULL, NULL, 'ApartmentOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2024-06-22 16:40:21', 1, '2024-07-02 20:15:17');
INSERT INTO `t_menu` VALUES (221, '定时任务', 2, 50, 25, '/job/list', '/support/job/job-list.vue', 1, NULL, NULL, 'AppstoreOutlined', NULL, 0, NULL, 1, 1, 0, 0, 2, '2024-06-25 17:57:40', 2, '2024-06-25 19:49:21');
INSERT INTO `t_menu` VALUES (228, '职务管理', 2, 45, 2, '/organization/position', '/system/position/position-list.vue', 1, NULL, NULL, 'ApartmentOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2024-06-29 11:11:09', 1, '2024-07-02 20:15:11');
INSERT INTO `t_menu` VALUES (229, '查询任务', 3, 221, NULL, NULL, NULL, 1, 'support:job:query', 'support:job:query', NULL, 221, 0, NULL, 1, 1, 0, 0, 2, '2024-06-29 11:14:15', 2, '2024-06-29 11:15:00');
INSERT INTO `t_menu` VALUES (230, '更新任务', 3, 221, NULL, NULL, NULL, 1, 'support:job:update', 'support:job:update', NULL, 221, 0, NULL, 1, 1, 0, 0, 2, '2024-06-29 11:15:40', NULL, '2024-06-29 11:15:40');
INSERT INTO `t_menu` VALUES (231, '执行任务', 3, 221, NULL, NULL, NULL, 1, 'support:job:execute', 'support:job:execute', NULL, 221, 0, NULL, 1, 1, 0, 0, 2, '2024-06-29 11:16:03', NULL, '2024-06-29 11:16:03');
INSERT INTO `t_menu` VALUES (232, '查询记录', 3, 221, NULL, NULL, NULL, 1, 'support:job:log:query', 'support:job:log:query', NULL, 221, 0, NULL, 1, 1, 0, 0, 2, '2024-06-29 11:16:37', NULL, '2024-06-29 11:16:37');
INSERT INTO `t_menu` VALUES (233, 'knife4j文档', 2, 218, 4, '/knife4j', NULL, 1, NULL, NULL, 'FileWordOutlined', NULL, 1, 'http://localhost:1024/doc.html', 1, 1, 0, 0, 1, '2024-07-02 20:23:50', 1, '2024-07-08 13:49:15');
INSERT INTO `t_menu` VALUES (234, 'swagger文档', 2, 218, 5, '/swagger', 'http://localhost:1024/swagger-ui/index.html', 1, NULL, NULL, 'ApiOutlined', NULL, 1, 'http://localhost:1024/swagger-ui/index.html', 1, 1, 0, 0, 1, '2024-07-02 20:35:43', 1, '2024-07-08 13:49:26');
INSERT INTO `t_menu` VALUES (250, '三级等保设置', 2, 213, 1, '/support/level3protect/level3-protect-config-index', '/support/level3protect/level3-protect-config-index.vue', 1, NULL, NULL, 'SafetyOutlined', NULL, 0, NULL, 1, 1, 0, 0, 44, '2024-08-13 11:41:02', 44, '2024-08-13 11:58:12');
INSERT INTO `t_menu` VALUES (251, '敏感数据脱敏', 2, 213, 3, '/support/level3protect/data-masking-list', '/support/level3protect/data-masking-list.vue', 1, NULL, NULL, 'FileProtectOutlined', NULL, 0, NULL, 1, 1, 0, 0, 44, '2024-08-13 11:58:00', 44, '2024-08-13 11:59:49');
INSERT INTO `t_menu` VALUES (252, '业务员', 2, 262, NULL, '/vigorous/salesperson', '/vigorous/salesperson/salesperson-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2024-12-12 15:10:21', 1, '2024-12-16 13:28:55');
INSERT INTO `t_menu` VALUES (253, '查询', 3, 252, NULL, NULL, NULL, 1, 'salesperson:query', 'salesperson:query', NULL, 252, 0, NULL, 1, 1, 0, 0, 1, '2024-12-12 15:10:21', NULL, '2025-03-06 09:03:04');
INSERT INTO `t_menu` VALUES (254, '添加', 3, 252, NULL, NULL, NULL, 1, 'salesperson:add', 'salesperson:add', NULL, 252, 0, NULL, 1, 1, 0, 0, 1, '2024-12-12 15:10:21', NULL, '2025-03-06 09:03:06');
INSERT INTO `t_menu` VALUES (255, '更新', 3, 252, NULL, NULL, NULL, 1, 'salesperson:update', 'salesperson:update', NULL, 252, 0, NULL, 1, 1, 0, 0, 1, '2024-12-12 15:10:21', NULL, '2025-03-06 09:03:07');
INSERT INTO `t_menu` VALUES (256, '删除', 3, 252, NULL, NULL, NULL, 1, 'salesperson:delete', 'salesperson:delete', NULL, 252, 0, NULL, 1, 1, 0, 0, 1, '2024-12-12 15:10:21', NULL, '2025-03-06 09:03:09');
INSERT INTO `t_menu` VALUES (257, '导入', 3, 252, NULL, NULL, NULL, 1, 'salesperson:import', 'salesperson:import', NULL, 252, 0, NULL, 1, 1, 0, 0, 1, '2024-12-16 12:30:07', NULL, '2025-03-06 09:03:10');
INSERT INTO `t_menu` VALUES (258, '导出', 3, 252, NULL, NULL, NULL, 1, 'salesperson:export', 'salesperson:export', NULL, 252, 0, NULL, 1, 1, 0, 0, 1, '2024-12-16 12:30:42', NULL, '2025-03-06 09:03:12');
INSERT INTO `t_menu` VALUES (259, '提成级别', 1, 262, NULL, '/salesperson-level/list', '/vigorous/salesperson-level/salesperson-level-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2024-12-18 10:20:19', 1, '2025-01-09 10:43:42');
INSERT INTO `t_menu` VALUES (260, '查询', 3, 259, NULL, NULL, NULL, 1, 'salespersonLevel:query', 'salespersonLevel:query', NULL, 259, 0, NULL, 1, 1, 0, 0, 1, '2024-12-18 10:20:19', NULL, '2025-03-06 09:03:14');
INSERT INTO `t_menu` VALUES (261, '添加', 3, 259, NULL, NULL, NULL, 1, 'salespersonLevel:add', 'salespersonLevel:add', NULL, 259, 0, NULL, 1, 1, 0, 0, 1, '2024-12-18 10:20:19', NULL, '2025-03-06 09:03:15');
INSERT INTO `t_menu` VALUES (262, '数据管理', 1, 0, 1, NULL, NULL, 1, NULL, NULL, 'AuditOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2024-12-12 15:59:44', 1, '2024-12-27 10:56:03');
INSERT INTO `t_menu` VALUES (263, '客户', 1, 262, NULL, '/customer/list', '/vigorous/customer/customer-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2024-12-18 11:34:08', 1, '2024-12-19 15:52:55');
INSERT INTO `t_menu` VALUES (264, '查询', 3, 263, NULL, NULL, NULL, 1, 'customer:query', 'customer:query', NULL, 263, 0, NULL, 1, 1, 0, 0, 1, '2024-12-18 11:34:08', NULL, '2025-03-06 09:03:19');
INSERT INTO `t_menu` VALUES (265, '添加', 3, 263, NULL, NULL, NULL, 1, 'customer:add', 'customer:add', NULL, 263, 0, NULL, 1, 1, 0, 0, 1, '2024-12-18 11:34:08', NULL, '2025-03-06 09:03:21');
INSERT INTO `t_menu` VALUES (266, '更新', 3, 263, NULL, NULL, NULL, 1, 'customer:update', 'customer:update', NULL, 263, 0, NULL, 1, 1, 0, 0, 1, '2024-12-18 11:34:08', NULL, '2025-03-06 09:03:22');
INSERT INTO `t_menu` VALUES (267, '删除', 3, 263, NULL, NULL, NULL, 1, 'customer:delete', 'customer:delete', NULL, 263, 0, NULL, 1, 1, 0, 0, 1, '2024-12-18 11:34:08', NULL, '2025-03-06 09:03:24');
INSERT INTO `t_menu` VALUES (268, '批量删除', 3, 263, NULL, NULL, NULL, 1, 'customer:batchDelete', 'customer:batchDelete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 09:42:18', NULL, '2025-03-06 09:50:39');
INSERT INTO `t_menu` VALUES (269, '导入', 3, 263, NULL, NULL, NULL, 1, 'customer:import', 'customer:import', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 09:45:35', NULL, '2025-03-06 09:45:35');
INSERT INTO `t_menu` VALUES (270, '导出', 3, 263, NULL, NULL, NULL, 1, 'customer:export', 'customer:export', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 09:46:09', NULL, '2025-03-06 09:46:09');
INSERT INTO `t_menu` VALUES (273, '销售出库', 2, 262, NULL, '/sales-outbound/list', '/vigorous/sales-outbound/sales-outbound-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2024-12-18 12:43:09', 1, '2024-12-27 10:53:58');
INSERT INTO `t_menu` VALUES (274, '查询', 3, 273, NULL, NULL, NULL, 1, 'salesOutbound:query', 'salesOutbound:query', NULL, 273, 0, NULL, 1, 1, 0, 0, 1, '2024-12-18 12:43:09', NULL, '2025-03-06 09:03:26');
INSERT INTO `t_menu` VALUES (275, '添加', 3, 273, NULL, NULL, NULL, 1, 'salesOutbound:add', 'salesOutbound:add', NULL, 273, 0, NULL, 1, 1, 0, 0, 1, '2024-12-18 12:43:09', NULL, '2025-03-06 09:03:27');
INSERT INTO `t_menu` VALUES (276, '更新', 3, 273, NULL, NULL, NULL, 1, 'salesOutbound:update', 'salesOutbound:update', NULL, 273, 0, NULL, 1, 1, 0, 0, 1, '2024-12-18 12:43:09', NULL, '2025-03-06 09:03:29');
INSERT INTO `t_menu` VALUES (277, '删除', 3, 273, NULL, NULL, NULL, 1, 'salesOutbound:delete', 'salesOutbound:delete', NULL, 273, 0, NULL, 1, 1, 0, 0, 1, '2024-12-18 12:43:09', NULL, '2025-03-06 09:03:31');
INSERT INTO `t_menu` VALUES (278, '提成规则', 2, 262, NULL, '/commission-rule/list', '/vigorous/commission/rule/commission-rule-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2024-12-25 15:05:31', NULL, '2025-01-12 15:57:36');
INSERT INTO `t_menu` VALUES (279, '查询', 3, 278, NULL, NULL, NULL, 1, 'commissionRule:query', 'commissionRule:query', NULL, 278, 0, NULL, 1, 1, 0, 0, 1, '2024-12-25 15:05:31', NULL, '2025-03-06 09:03:35');
INSERT INTO `t_menu` VALUES (280, '添加', 3, 278, NULL, NULL, NULL, 1, 'commissionRule:add', 'commissionRule:add', NULL, 278, 0, NULL, 1, 1, 0, 0, 1, '2024-12-25 15:05:31', NULL, '2025-03-06 09:03:36');
INSERT INTO `t_menu` VALUES (281, '更新', 3, 278, NULL, NULL, NULL, 1, 'commissionRule:update', 'commissionRule:update', NULL, 278, 0, NULL, 1, 1, 0, 0, 1, '2024-12-25 15:05:31', NULL, '2025-03-06 09:03:38');
INSERT INTO `t_menu` VALUES (282, '删除', 3, 278, NULL, NULL, NULL, 1, 'commissionRule:delete', 'commissionRule:delete', NULL, 278, 0, NULL, 1, 1, 0, 0, 1, '2024-12-25 15:05:31', NULL, '2025-03-06 09:03:40');
INSERT INTO `t_menu` VALUES (283, '业绩提成计算', 1, 0, NULL, NULL, NULL, 1, NULL, NULL, 'CalculatorOutlined', 259, 0, NULL, 1, 1, 0, 0, 1, '2024-12-27 10:55:27', 1, '2024-12-27 10:55:40');
INSERT INTO `t_menu` VALUES (284, '应收单', 2, 262, NULL, '/receivables/list', '/vigorous/receivables/receivables-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2024-12-27 14:16:09', NULL, '2024-12-27 14:16:30');
INSERT INTO `t_menu` VALUES (285, '查询', 3, 284, NULL, NULL, NULL, 1, 'receivables:query', 'receivables:query', NULL, 284, 0, NULL, 1, 1, 0, 0, 1, '2024-12-27 14:16:09', NULL, '2025-03-06 09:03:42');
INSERT INTO `t_menu` VALUES (286, '添加', 3, 284, NULL, NULL, NULL, 1, 'receivables:add', 'receivables:add', NULL, 284, 0, NULL, 1, 1, 0, 0, 1, '2024-12-27 14:16:09', NULL, '2025-03-06 09:03:51');
INSERT INTO `t_menu` VALUES (287, '更新', 3, 284, NULL, NULL, NULL, 1, 'receivables:update', 'receivables:update', NULL, 284, 0, NULL, 1, 1, 0, 0, 1, '2024-12-27 14:16:09', NULL, '2025-03-06 09:03:51');
INSERT INTO `t_menu` VALUES (288, '删除', 3, 284, NULL, NULL, NULL, 1, 'receivables:delete', 'receivables:delete', NULL, 284, 0, NULL, 1, 1, 0, 0, 1, '2024-12-27 14:16:09', NULL, '2025-03-06 09:03:51');
INSERT INTO `t_menu` VALUES (289, '业务员级别变动记录', 2, 262, NULL, '/salesperson-level/record', '/vigorous/salesperson-level/salesperson-level-record-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-01-07 09:52:26', 1, '2025-01-12 08:49:38');
INSERT INTO `t_menu` VALUES (290, '查询', 3, 289, NULL, NULL, NULL, 1, 'salespersonLevelRecord:query', 'salespersonLevelRecord:query', NULL, 289, 0, NULL, 1, 1, 0, 0, 1, '2025-01-07 09:52:26', NULL, '2025-03-06 09:04:05');
INSERT INTO `t_menu` VALUES (291, '添加', 3, 289, NULL, NULL, NULL, 1, 'salespersonLevelRecord:add', 'salespersonLevelRecord:add', NULL, 289, 0, NULL, 1, 1, 0, 0, 1, '2025-01-07 09:52:26', NULL, '2025-03-06 09:04:05');
INSERT INTO `t_menu` VALUES (292, '更新', 3, 289, NULL, NULL, NULL, 1, 'salespersonLevelRecord:update', 'salespersonLevelRecord:update', NULL, 289, 0, NULL, 1, 1, 0, 0, 1, '2025-01-07 09:52:26', NULL, '2025-03-06 09:04:05');
INSERT INTO `t_menu` VALUES (293, '删除', 3, 289, NULL, NULL, NULL, 1, 'salespersonLevelRecord:delete', 'salespersonLevelRecord:delete', NULL, 289, 0, NULL, 1, 1, 0, 0, 1, '2025-01-07 09:52:26', NULL, '2025-03-06 09:04:05');
INSERT INTO `t_menu` VALUES (294, '业务提成记录', 2, 262, NULL, '/commission-record/list', '/vigorous/commission/calc/commission-record-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-01-12 15:33:23', NULL, '2025-01-12 16:06:44');
INSERT INTO `t_menu` VALUES (295, '查询', 3, 294, NULL, NULL, NULL, 1, 'commissionRecord:query', 'commissionRecord:query', NULL, 294, 0, NULL, 1, 1, 0, 0, 1, '2025-01-12 15:33:23', NULL, '2025-03-06 09:04:05');
INSERT INTO `t_menu` VALUES (296, '添加', 3, 294, NULL, NULL, NULL, 1, 'commissionRecord:add', 'commissionRecord:add', NULL, 294, 0, NULL, 1, 1, 0, 0, 1, '2025-01-12 15:33:23', NULL, '2025-03-06 09:04:05');
INSERT INTO `t_menu` VALUES (297, '更新', 3, 294, NULL, NULL, NULL, 1, 'commissionRecord:update', 'commissionRecord:update', NULL, 294, 0, NULL, 1, 1, 0, 0, 1, '2025-01-12 15:33:23', NULL, '2025-03-06 09:04:05');
INSERT INTO `t_menu` VALUES (298, '删除', 3, 294, NULL, NULL, NULL, 1, 'commissionRecord:delete', 'commissionRecord:delete', NULL, 294, 0, NULL, 1, 1, 0, 0, 1, '2025-01-12 15:33:23', NULL, '2025-03-06 09:04:05');
INSERT INTO `t_menu` VALUES (299, '导出', 3, 294, NULL, NULL, NULL, 1, 'commissionRecord:export', 'commissionRecord:export', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 08:36:07', NULL, '2025-03-06 09:04:05');
INSERT INTO `t_menu` VALUES (302, '更新', 3, 259, NULL, NULL, NULL, 1, 'salespersonLevel:update', 'salespersonLevel:update', NULL, 259, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 09:38:24', NULL, '2025-03-06 09:52:51');
INSERT INTO `t_menu` VALUES (303, '删除', 3, 259, NULL, NULL, NULL, 1, 'salespersonLevel:delete', 'salespersonLevel:delete', NULL, 259, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 09:39:02', NULL, '2025-03-06 09:52:49');
INSERT INTO `t_menu` VALUES (304, '批量删除', 3, 259, NULL, NULL, NULL, 1, 'salespersonLevel:batchDelete', 'salespersonLevel:batchDelete', NULL, 259, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 09:40:17', NULL, '2025-03-06 09:52:45');
INSERT INTO `t_menu` VALUES (305, '导入', 3, 259, NULL, NULL, NULL, 1, 'salespersonLevel:import', 'salespersonLevel:import', NULL, 259, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 10:26:16', NULL, '2025-03-06 10:26:16');
INSERT INTO `t_menu` VALUES (306, '导出', 3, 259, NULL, NULL, NULL, 1, 'salespersonLevel:export', 'salespersonLevel:export', NULL, 259, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 10:26:47', NULL, '2025-03-06 10:27:34');
INSERT INTO `t_menu` VALUES (307, '批量删除', 3, 284, NULL, NULL, NULL, 1, 'receivables:batchDelete', 'receivables:batchDelete', NULL, 284, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 09:55:32', NULL, '2025-03-06 10:25:27');
INSERT INTO `t_menu` VALUES (308, '导入', 3, 284, NULL, NULL, NULL, 1, 'receivables:import', 'receivables:import', NULL, 284, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 09:56:11', NULL, '2025-03-06 10:25:23');
INSERT INTO `t_menu` VALUES (309, '导出', 3, 284, NULL, NULL, NULL, 1, 'receivables:export', 'receivables:export', NULL, 284, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 09:56:47', NULL, '2025-03-06 10:25:20');
INSERT INTO `t_menu` VALUES (310, '批量删除', 3, 273, NULL, NULL, NULL, 1, 'salesOutbound:batchDelete', 'salesOutbound:batchDelete', NULL, 273, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 10:23:17', NULL, '2025-03-06 10:25:18');
INSERT INTO `t_menu` VALUES (311, '导入', 3, 273, NULL, NULL, NULL, 1, 'salesOutbound:import', 'salesOutbound:import', NULL, 273, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 10:23:51', NULL, '2025-03-06 10:25:16');
INSERT INTO `t_menu` VALUES (312, '导出', 3, 273, NULL, NULL, NULL, 1, 'salesOutbound:export', 'salesOutbound:export', NULL, 273, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 10:24:16', NULL, '2025-03-06 10:25:12');
INSERT INTO `t_menu` VALUES (313, '生成提成', 3, 273, NULL, NULL, NULL, 1, 'salesOutbound:createCommission', 'salesOutbound:createCommission', NULL, 273, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 10:31:35', NULL, '2025-03-06 10:31:35');
INSERT INTO `t_menu` VALUES (314, '修改提成状态', 3, 273, NULL, NULL, NULL, 1, 'salesOutbound:updateFlag', 'salesOutbound:updateFlag', NULL, 273, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 10:33:36', NULL, '2025-03-06 10:33:36');
INSERT INTO `t_menu` VALUES (315, '批量删除', 3, 252, NULL, NULL, NULL, 1, 'salesperson:batchDelete', 'salesperson:batchDelete', NULL, 252, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 10:39:02', NULL, '2025-03-06 10:39:02');
INSERT INTO `t_menu` VALUES (316, '批量删除', 3, 294, NULL, NULL, NULL, 1, 'commissionRecord:batchDelete', 'commissionRecord:batchDelete', NULL, 294, 0, NULL, 0, 1, 0, 0, 1, '2025-03-06 10:41:41', NULL, '2025-03-06 10:41:41');

SET FOREIGN_KEY_CHECKS = 1;
