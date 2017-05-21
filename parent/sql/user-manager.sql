/*
Navicat MySQL Data Transfer

Source Server         : MySQL
Source Server Version : 50525
Source Host           : localhost:3306
Source Database       : ssm

Target Server Type    : MYSQL
Target Server Version : 50525
File Encoding         : 65001

Date: 2017-05-21 13:41:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `name` varchar(50) DEFAULT NULL COMMENT '名字',
  `phone` varchar(50) NOT NULL COMMENT '手机号',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `headImg` varchar(512) DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=181 DEFAULT CHARSET=latin1;
