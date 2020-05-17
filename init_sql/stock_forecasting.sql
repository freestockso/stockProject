/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50724
Source Host           : localhost:3306
Source Database       : stock_project

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2020-05-17 12:11:43
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for stock_forecasting
-- ----------------------------
DROP TABLE IF EXISTS `stock_forecasting`;
CREATE TABLE `stock_forecasting` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '无意义自增序列',
  `code` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '股票码',
  `up_rate` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '预测的结果-分号分隔',
  `param` text CHARACTER SET utf8mb4 COMMENT '优化的结果参数',
  `date` int(11) DEFAULT NULL COMMENT '历史的最后一天日期',
  `cci_history` text CHARACTER SET utf8mb4 COMMENT '历史的cci数据(该股票的历史数据,一般超过14)',
  `cci_now` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '待预测的cci数据(长度固定为14)',
  `up_history` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for stock_info
-- ----------------------------
DROP TABLE IF EXISTS `stock_info`;
CREATE TABLE `stock_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `ten` int(2) DEFAULT NULL COMMENT '价格是否翻10倍',
  `buy_price` bigint(20) DEFAULT NULL,
  `sale_price` bigint(20) DEFAULT NULL,
  `last_cci` double(255,0) DEFAULT NULL COMMENT '上一个交易日的CCI',
  `last_price` bigint(20) DEFAULT NULL COMMENT '上一个交易日的股票最后价格',
  `last_buy_date` int(11) DEFAULT NULL COMMENT '上一个cci小于-100的日期',
  `last_sale_date` int(11) DEFAULT NULL COMMENT '上一个cci大于100的日期',
  `average_earning_cycle` int(11) DEFAULT NULL COMMENT '平均赚钱周期',
  `longest_earning_cycle` int(11) DEFAULT NULL COMMENT '最长赚钱周期',
  `times_of_making_money` int(11) DEFAULT NULL COMMENT '赚钱次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for stock_transaction_info
-- ----------------------------
DROP TABLE IF EXISTS `stock_transaction_info`;
CREATE TABLE `stock_transaction_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `open` bigint(11) DEFAULT NULL COMMENT '开盘价',
  `close` bigint(11) DEFAULT NULL COMMENT '收盘价',
  `high` bigint(11) DEFAULT NULL COMMENT '最高价',
  `low` bigint(11) DEFAULT NULL COMMENT '最低价',
  `vol` bigint(11) DEFAULT NULL COMMENT '股数',
  `amount` bigint(11) DEFAULT NULL COMMENT '成交额',
  `date` bigint(11) DEFAULT NULL,
  `cci` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `code_index` (`code`) USING BTREE,
  KEY `date_index` (`date`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for telephone_code
-- ----------------------------
DROP TABLE IF EXISTS `telephone_code`;
CREATE TABLE `telephone_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `date_time` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `budget` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user_stock_hold_info
-- ----------------------------
DROP TABLE IF EXISTS `user_stock_hold_info`;
CREATE TABLE `user_stock_hold_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
