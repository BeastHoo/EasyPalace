/*
Navicat MySQL Data Transfer

Source Server         : admin
Source Server Version : 80021
Source Host           : localhost:3306
Source Database       : blogandpan

Target Server Type    : MYSQL
Target Server Version : 80021
File Encoding         : 65001

Date: 2022-01-06 15:34:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for blogs
-- ----------------------------
DROP TABLE IF EXISTS `blogs`;
CREATE TABLE `blogs` (
  `blog_id` varchar(12) NOT NULL,
  `editor` varchar(12) NOT NULL,
  `title` varchar(50) NOT NULL,
  `content` text NOT NULL,
  `edit_time` timestamp NOT NULL,
  PRIMARY KEY (`blog_id`),
  UNIQUE KEY `blogs_blog_id_uindex` (`blog_id`),
  KEY `blogs_user_username_fk` (`editor`),
  CONSTRAINT `blogs_user_username_fk` FOREIGN KEY (`editor`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for blog_details
-- ----------------------------
DROP TABLE IF EXISTS `blog_details`;
CREATE TABLE `blog_details` (
  `blog_id` varchar(12) NOT NULL,
  `description` text NOT NULL COMMENT '描述',
  `click_rate` int NOT NULL DEFAULT '0' COMMENT '点击率',
  `collect_num` int NOT NULL DEFAULT '0' COMMENT '收藏量',
  `tag_1` varchar(8) DEFAULT NULL,
  `tag_2` varchar(8) DEFAULT NULL,
  `tag_3` varchar(8) DEFAULT NULL,
  `tag_4` varchar(8) DEFAULT NULL,
  `tag_5` varchar(8) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `editor` varchar(12) NOT NULL,
  `edit_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`blog_id`),
  UNIQUE KEY `blog_details_blog_id_uindex` (`blog_id`),
  KEY `blog_details_editor_index` (`editor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for collectlist
-- ----------------------------
DROP TABLE IF EXISTS `collectlist`;
CREATE TABLE `collectlist` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `pfid` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `collectlist_user_username_fk` (`username`),
  KEY `collectlist_publicfiles_id_fk` (`pfid`),
  CONSTRAINT `collectlist_publicfiles_id_fk` FOREIGN KEY (`pfid`) REFERENCES `publicfiles` (`id`),
  CONSTRAINT `collectlist_user_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `userto` varchar(20) DEFAULT NULL,
  `userfrom` varchar(20) NOT NULL,
  `title` varchar(64) NOT NULL,
  `content` text,
  `mtime` timestamp NOT NULL,
  `rely` varchar(128) DEFAULT NULL,
  `status` int NOT NULL DEFAULT '0' COMMENT '1:已读 0:未读',
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `messages_user_username_fk` (`userto`),
  KEY `messages_user_username_fk_2` (`userfrom`),
  CONSTRAINT `messages_user_username_fk` FOREIGN KEY (`userto`) REFERENCES `user` (`username`),
  CONSTRAINT `messages_user_username_fk_2` FOREIGN KEY (`userfrom`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for privatefiles
-- ----------------------------
DROP TABLE IF EXISTS `privatefiles`;
CREATE TABLE `privatefiles` (
  `ismusic` tinyint(1) DEFAULT '0',
  `isplayable` tinyint(1) DEFAULT '0',
  `isvideo` tinyint(1) DEFAULT '0',
  `ispic` tinyint(1) DEFAULT '0',
  `msize` varchar(10) DEFAULT NULL,
  `username` varchar(20) DEFAULT NULL,
  `filename` varchar(128) DEFAULT NULL,
  `loc` varchar(255) DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `mtime` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `privatefiles_user_username_fk` (`username`),
  CONSTRAINT `privatefiles_user_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for publicfiles
-- ----------------------------
DROP TABLE IF EXISTS `publicfiles`;
CREATE TABLE `publicfiles` (
  `isplayable` tinyint(1) DEFAULT '0',
  `ismusic` tinyint(1) DEFAULT '0',
  `isvideo` tinyint(1) DEFAULT '0',
  `ispic` tinyint(1) DEFAULT '0',
  `msize` varchar(10) DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `filename` varchar(128) DEFAULT NULL,
  `loc` varchar(255) DEFAULT NULL,
  `collectnum` int DEFAULT '0',
  `downloadnum` int DEFAULT '0',
  `mtime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `publicfiles_filename_uindex` (`filename`),
  KEY `publicfiles_user_username_fk` (`username`),
  CONSTRAINT `publicfiles_user_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
  `tag_id` varchar(6) NOT NULL,
  `tag_name` varchar(16) NOT NULL,
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `tags_tag_id_uindex` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `username` varchar(20) NOT NULL,
  `password` varchar(64) NOT NULL,
  `email` varchar(50) NOT NULL,
  `registtime` timestamp NULL DEFAULT NULL,
  `salt` varchar(10) DEFAULT NULL,
  `signature` varchar(64) DEFAULT NULL,
  `imageUrl` varchar(128) DEFAULT NULL,
  `birthday` timestamp NULL DEFAULT NULL,
  `slogan` varchar(64) DEFAULT NULL,
  `gender` varchar(4) DEFAULT '男',
  `nickname` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`username`),
  UNIQUE KEY `user_username_uindex` (`username`),
  UNIQUE KEY `user_email_uindex` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
