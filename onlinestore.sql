/*
Navicat MySQL Data Transfer

Source Server         : onlinestore
Source Server Version : 80033
Source Host           : 117.72.197.93:3306
Source Database       : onlinestore

Target Server Type    : MYSQL
Target Server Version : 80033
File Encoding         : 65001

Date: 2025-07-07 11:26:45
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货地址',
  `is_default` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `address_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES ('1', '1', '北京市海淀区中关村大街1号', '1');
INSERT INTO `address` VALUES ('2', '2', '上海市浦东新区世纪大道100号', '1');
INSERT INTO `address` VALUES ('3', '7', 'gz', '1');
INSERT INTO `address` VALUES ('4', '7', 'aaa', '0');
INSERT INTO `address` VALUES ('5', '7', 'bnbb', '0');
INSERT INTO `address` VALUES ('8', '3', '广州航海学院', '1');
INSERT INTO `address` VALUES ('9', '3', '北四1136', '0');
INSERT INTO `address` VALUES ('10', '3', '翁裕苑5号店', '0');
INSERT INTO `address` VALUES ('12', '9', '教学楼110', '1');
INSERT INTO `address` VALUES ('13', '9', '信息楼217', '0');

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `author` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `publisher` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `isbn` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `price` double DEFAULT NULL,
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cover` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面图片URL',
  `category_id` int NOT NULL,
  `status` varchar(255) COLLATE utf8mb4_general_ci DEFAULT 'on',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `isbn` (`isbn`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `book_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES ('1', '三体', '刘慈欣', '重庆出版社', '9787536692930', '59.9', '100', '著名科幻小说《三体》第一部', 'https://www.zazhi.com.cn/upload/book/201711/09/d255-b31d.jpg', '1', 'on', '2024-06-20 10:00:00');
INSERT INTO `book` VALUES ('2', '活着', '余华', '作家出版社', '9787506365437', '39', '50', '余华代表作', 'https://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%27LV8xNzM1ODU1OTE1LjM5NDY1OTVfMTczNTg1NTkxNy4zMzMxNzcz%27/1.png', '2', 'on', '2024-06-20 10:05:00');
INSERT INTO `book` VALUES ('3', '深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', '9787111558426', '128', '30', '经典CS教材', 'http://t15.baidu.com/it/u=1973211167,771202713&fm=224&app=112&f=JPEG?w=411&h=500', '3', 'on', '2024-06-20 10:10:00');
INSERT INTO `book` VALUES ('4', '安徒生童话', '安徒生', '清华大学出版社', '9876578968', '55', '100', '安徒生小朋友', 'https://img14.360buyimg.com/pop/jfs/t532/130/144106532/672283/33f79da1/54520950N8dd2d22e.jpg', '2', 'on', '2025-06-23 11:41:02');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES ('1', '1');
INSERT INTO `cart` VALUES ('2', '2');
INSERT INTO `cart` VALUES ('3', '3');
INSERT INTO `cart` VALUES ('33', '5');
INSERT INTO `cart` VALUES ('34', '7');
INSERT INTO `cart` VALUES ('35', '9');

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cart_id` int NOT NULL,
  `book_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cart_id` (`cart_id`),
  KEY `book_id` (`book_id`),
  CONSTRAINT `cart_item_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`) ON DELETE CASCADE,
  CONSTRAINT `cart_item_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of cart_item
-- ----------------------------
INSERT INTO `cart_item` VALUES ('30', '33', '1', '1', '59.8');
INSERT INTO `cart_item` VALUES ('31', '33', '2', '2', '39');
INSERT INTO `cart_item` VALUES ('32', '33', '3', '3', '128');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分类描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES ('1', '科幻', '科幻类图书');
INSERT INTO `category` VALUES ('2', '文学', '文学类图书');
INSERT INTO `category` VALUES ('3', '计算机', '计算机类图书');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_number` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_id` int NOT NULL,
  `address_id` int NOT NULL,
  `total_price` double DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `user_id` (`user_id`),
  KEY `address_id` (`address_id`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `order_ibfk_2` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES ('1', '202406200001', '1', '1', '59.8', 'PENDING', '2024-06-20 10:20:00');
INSERT INTO `order` VALUES ('2', '202406200002', '2', '2', '39', 'COMPLETED', '2024-06-20 10:25:00');
INSERT INTO `order` VALUES ('3', 'ORD1750640229607_7', '7', '3', '119.6', 'CANCELLED', null);
INSERT INTO `order` VALUES ('4', '202506231750642644606', '7', '3', '167', 'PENDING', null);
INSERT INTO `order` VALUES ('5', '202506231750643364308', '7', '5', '128', 'PENDING', '2025-06-23 01:49:25');
INSERT INTO `order` VALUES ('8', '202506231750645284442', '7', '4', '39', 'PENDING', '2025-06-23 02:21:25');
INSERT INTO `order` VALUES ('9', '202506231750645408519', '7', '4', '78', 'PENDING', '2025-06-23 02:23:29');
INSERT INTO `order` VALUES ('11', '202506231750660253400', '3', '10', '55', 'cancelled', '2025-06-23 06:30:54');
INSERT INTO `order` VALUES ('15', '202506231750692203859', '9', '12', '39', 'shipped', '2025-06-23 15:23:24');
INSERT INTO `order` VALUES ('16', '202506231750692289990', '3', '8', '167', 'PENDING', '2025-06-23 15:24:50');
INSERT INTO `order` VALUES ('17', '202506241750749780770', '9', '12', '213.8', 'PENDING', '2025-06-24 07:23:01');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `book_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `book_id` (`book_id`),
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE,
  CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES ('1', '1', '1', '1', '59.8');
INSERT INTO `order_item` VALUES ('2', '2', '2', '1', '39');
INSERT INTO `order_item` VALUES ('5', '8', '2', '1', '39');
INSERT INTO `order_item` VALUES ('6', '9', '2', '2', '39');
INSERT INTO `order_item` VALUES ('10', '11', '4', '1', '55');
INSERT INTO `order_item` VALUES ('16', '15', '2', '1', '39');
INSERT INTO `order_item` VALUES ('17', '16', '2', '1', '39');
INSERT INTO `order_item` VALUES ('18', '16', '3', '1', '128');
INSERT INTO `order_item` VALUES ('19', '17', '1', '2', '59.9');
INSERT INTO `order_item` VALUES ('20', '17', '2', '1', '39');
INSERT INTO `order_item` VALUES ('21', '17', '4', '1', '55');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '加密密码',
  `role` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `real_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'alice', 'alice@example.com', '13800000001', '123456', 'USER', '1', '2025-06-20 15:26:08', '爱丽丝');
INSERT INTO `user` VALUES ('2', 'bob', 'bob@example.com', '13800000002', '123456', 'USER', '1', '2025-06-20 15:26:08', '鲍勃');
INSERT INTO `user` VALUES ('3', 'admin', 'admin@example.com', '13800000000', 'admin123', 'ADMIN', '1', '2025-06-20 15:26:08', '管理员');
INSERT INTO `user` VALUES ('5', 'ljh', '2838817850@qq.com', '18022841309', '123456', null, '1', null, '李锦洪');
INSERT INTO `user` VALUES ('7', 'bbb', 'bbb@qq.com', '13312345678', '123456', 'USER', '1', '2025-06-22 12:48:26', '啵啵啵');
INSERT INTO `user` VALUES ('8', 'aaa', 'aaa@163.com', '18712345678', '123456', 'user', '1', '2025-06-23 11:01:42', null);
INSERT INTO `user` VALUES ('9', 'libai', 'libai@qq.com', '17712345678', '1234567', 'USER', '1', '2025-06-23 13:53:54', '李白');
