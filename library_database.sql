/*
 Navicat Premium Data Transfer

 Source Server         : ybbb
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3306
 Source Schema         : library_database

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 30/12/2024 17:47:43
*/
USE library_database;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 图书表结构
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `title` varchar(100) NOT NULL COMMENT '书名',
    `author` varchar(50) NOT NULL COMMENT '作者',
    `isbn` varchar(20) NOT NULL COMMENT 'ISBN号',
    `category` varchar(50) NOT NULL COMMENT '图书分类',
    `publish_date` date NOT NULL COMMENT '出版日期',
    `extra_info` varchar(255) DEFAULT NULL COMMENT '额外信息(不同类型图书的特殊属性)',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_isbn` (`isbn`),
    KEY `idx_category` (`category`),
    KEY `idx_title` (`title`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '图书信息表';

-- ----------------------------
-- 用户表结构
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `role` varchar(20) NOT NULL COMMENT '角色(admin/user)',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `last_login` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1:启用,0:禁用)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户信息表';

-- ----------------------------
-- 图书借阅记录表
-- ----------------------------
DROP TABLE IF EXISTS `book_loans`;
CREATE TABLE `book_loans` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `book_id` int(11) NOT NULL COMMENT '图书ID',
    `user_id` int(11) NOT NULL COMMENT '用户ID',
    `borrow_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借出时间',
    `due_date` timestamp NOT NULL COMMENT '应还时间',
    `return_date` timestamp NULL DEFAULT NULL COMMENT '实际归还时间',
    `status` varchar(20) NOT NULL DEFAULT 'BORROWED' COMMENT '状态(BORROWED/RETURNED/OVERDUE)',
    PRIMARY KEY (`id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_book_loans_book` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
    CONSTRAINT `fk_book_loans_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '图书借阅记录表';

-- ----------------------------
-- 初始数据
-- ----------------------------
-- 添加管理员用户
INSERT INTO `users` (`username`, `password`, `role`) VALUES
('admin', '123456', 'admin'),
('user', '123456', 'user');

-- 添加示例图书
INSERT INTO `books` (`title`, `author`, `isbn`, `category`, `publish_date`, `extra_info`) VALUES
('Java编程思想', 'Bruce Eckel', '9787111213826', '计算机', '2007-06-01', 'Java'),
('算法导论', 'Thomas H.Cormen', '9787111187776', '计算机', '2006-09-01', 'Algorithm'),
('红楼梦', '曹雪芹', '9787020002207', '文学', '1996-12-01', '古典小说'),
('百年孤独', '加西亚·马尔克斯', '9787544253994', '文学', '2011-06-01', '魔幻现实主义');

SET FOREIGN_KEY_CHECKS = 1;
