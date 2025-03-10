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

 Date: 07/03/2025 15:40:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for v_salesperson
-- ----------------------------
DROP TABLE IF EXISTS `v_salesperson`;
CREATE TABLE `v_salesperson`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '业务员编号',
  `salesperson_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务员编码',
  `salesperson_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务员名称',
  `department_id` bigint(20) NOT NULL COMMENT '部门编号',
  `salesperson_level_id` int(10) NULL DEFAULT NULL COMMENT '业务员级别编号',
  `path` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上级路径',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '上级编号',
  `deleted_flag` tinyint(3) NULL DEFAULT 0 COMMENT '是否删除0否 1是',
  `disabled_flag` tinyint(3) NULL DEFAULT 0 COMMENT '是否被禁用 0否1是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of v_salesperson
-- ----------------------------
INSERT INTO `v_salesperson` VALUES (20, '0028_GW000019_1', '叶芳芳', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (21, '0029_GW000019_1', '应伟红', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (22, '0030_GW000019_1', '沈晓燚', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (23, '0050_GW000030_1', '郑俊山', 10, 1, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (24, '0051_GW000031_1', '陈伊莉', 10, 4, NULL, 23, 0, 0);
INSERT INTO `v_salesperson` VALUES (25, '0052_GW000031_1', '潘晓萍', 10, 2, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (26, '0053_GW000031_1', '白莎', 10, 3, NULL, 23, 0, 0);
INSERT INTO `v_salesperson` VALUES (27, '0054_GW000031_1', '金婷婷', 10, 6, NULL, 23, 0, 0);
INSERT INTO `v_salesperson` VALUES (28, '0055_GW000031_1', '蒋虹', 10, 3, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (29, '0056_GW000031_1', '林滢', 10, NULL, NULL, NULL, 0, 1);
INSERT INTO `v_salesperson` VALUES (30, '0009_GW000008_1', '张棱棱', 11, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (31, '0031_GW000019_1', '陆君眉', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (32, '0022_GW000018_1', '黄正军', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (33, '0023_GW000018_1', '李恩峰', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (34, '0024_GW000018_1', '蒋波', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (35, '0025_GW000018_1', '江文彬', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (36, '0026_GW000018_1', '何俊', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (37, '0027_GW000018_1', '王次东', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (38, '0033_GW000018_1', '耿恒', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (39, '0177_GW000019_1', '梁依灵', 9, NULL, NULL, NULL, 0, 0);
INSERT INTO `v_salesperson` VALUES (40, '0075_GW000038_1', '徐程程', 12, NULL, NULL, NULL, 0, 0);

SET FOREIGN_KEY_CHECKS = 1;
