/*
SQLyog Ultimate v11.13 (64 bit)
MySQL - 5.7.30-log : Database - linfeng
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


/*Table structure for table `lf_active_user` */

DROP TABLE IF EXISTS `lf_active_user`;

CREATE TABLE `lf_active_user` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP',
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `active_count` int(11) NOT NULL DEFAULT '1' COMMENT '活跃次数',
  `terminal` varchar(50) DEFAULT NULL COMMENT '访问终端',
  `address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `type` int(1) DEFAULT '0' COMMENT '访问类型:0匿名1登录',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COMMENT='用户访问统计表';

/*Data for the table `lf_active_user` */

/*Table structure for table `lf_app_version` */

DROP TABLE IF EXISTS `lf_app_version`;

CREATE TABLE `lf_app_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `version` varchar(20) NOT NULL COMMENT '版本号(如1.0.1)',
  `min_version` varchar(20) DEFAULT NULL COMMENT '最低要求版本号(用于强制更新判断)',
  `is_force` tinyint(1) DEFAULT '0' COMMENT '是否强制更新(1:强制更新,0:非强制)',
  `content` text COMMENT '更新内容描述',
  `download_url` varchar(255) DEFAULT NULL COMMENT '通用下载地址',
  `android_url` varchar(255) DEFAULT NULL COMMENT 'Android应用市场地址',
  `ios_url` varchar(255) DEFAULT NULL COMMENT 'iOS应用市场地址',
  `status` tinyint(1) DEFAULT '1' COMMENT '版本状态(1:启用,0:禁用)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_version` (`version`) COMMENT '版本号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='APP版本信息表';

/*Data for the table `lf_app_version` */

/*Table structure for table `lf_bill` */

DROP TABLE IF EXISTS `lf_bill`;

CREATE TABLE `lf_bill` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户账单id',
  `uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户uid',
  `link_id` varchar(32) NOT NULL DEFAULT '0' COMMENT '关联id',
  `pm` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0 = 支出 1 = 获得',
  `title` varchar(64) NOT NULL DEFAULT '' COMMENT '账单标题',
  `category` varchar(64) NOT NULL DEFAULT '' COMMENT '明细种类',
  `type` varchar(64) NOT NULL DEFAULT '' COMMENT '明细类型',
  `number` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '明细数字',
  `balance` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '剩余',
  `mark` varchar(512) NOT NULL DEFAULT '' COMMENT '备注',
  `add_time` datetime NOT NULL COMMENT '添加时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0待确定 1有效 -1无效',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `openid` (`uid`) USING BTREE,
  KEY `status` (`status`) USING BTREE,
  KEY `add_time` (`add_time`) USING BTREE,
  KEY `pm` (`pm`) USING BTREE,
  KEY `type` (`category`,`type`,`link_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户账单表';

/*Data for the table `lf_bill` */

/*Table structure for table `lf_cash_out` */

DROP TABLE IF EXISTS `lf_cash_out`;

CREATE TABLE `lf_cash_out` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int(11) NOT NULL COMMENT '用户ID',
  `money_number` decimal(8,2) DEFAULT NULL COMMENT '申请提现金额',
  `url` varchar(255) NOT NULL COMMENT '收款码',
  `feedback` varchar(255) DEFAULT '无' COMMENT '审核反馈',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '打款类型0支付宝1微信',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现表';

/*Data for the table `lf_cash_out` */

/*Table structure for table `lf_category` */

DROP TABLE IF EXISTS `lf_category`;

CREATE TABLE `lf_category` (
  `cate_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类id',
  `cate_name` varchar(50) NOT NULL COMMENT '分类名称',
  `is_top` int(1) DEFAULT '0' COMMENT '是否推荐(0不推荐1推荐)',
  `cover_image` varchar(255) DEFAULT '' COMMENT '图片',
  PRIMARY KEY (`cate_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='圈子分类表';

/*Data for the table `lf_category` */

insert  into `lf_category`(`cate_id`,`cate_name`,`is_top`,`cover_image`) values (1,'校园',0,'http://pic.linfeng.tech/test/20220210/88148b0cd20f4ad4a09ce61866024281.jpg'),(2,'音乐',1,'http://pic.linfeng.tech/test/20220207/8d796540337d4dd48ae11143fc570b77.jpg'),(3,'生活',1,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(4,'兴趣',0,'http://pic.linfeng.tech/test/20220210/c7d2e8489a5642ba91b7b5f97c282141.jpg'),(5,'运动',0,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(6,'旅行',1,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(7,'知识',0,'http://pic.linfeng.tech/test/20220210/9621f4992cf64393be7567159b8d13f8.jpeg'),(8,'动漫',0,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(9,'情感',0,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(10,'娱乐',0,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(11,'宠物',0,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(13,'美食',0,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(14,'职场',0,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(15,'摄影',1,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(16,'时尚',0,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(17,'阅读',0,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(18,'游戏',0,'http://pic.linfeng.tech/test/20220207/69aaaff2132447dc9c4b893ce53e242d.jpg'),(19,'打卡',0,'http://pic.linfeng.tech/test/20220207/2858506e3dca4a9a8367201ed04b80bb.png'),(22,'开发',0,'http://pic.linfeng.tech/test/20220207/223ba0004cf54969a68a0ffad58cc51f.png'),(23,'交流',0,'http://pic.linfeng.tech/test/20220207/e334ab448fb44083b3df5d288f18d261.jpg');

/*Table structure for table `lf_chat_message` */

DROP TABLE IF EXISTS `lf_chat_message`;

CREATE TABLE `lf_chat_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '私聊ID',
  `session_id` bigint(20) DEFAULT NULL COMMENT '用户session',
  `sender_id` bigint(20) DEFAULT NULL COMMENT '发送者id',
  `receiver_id` bigint(255) DEFAULT NULL COMMENT '接收者id',
  `send_time` varchar(25) DEFAULT NULL COMMENT '发送时间',
  `content` varchar(600) DEFAULT NULL COMMENT '内容',
  `message_type` varchar(10) DEFAULT NULL COMMENT '类型',
  `is_withdrawn` int(11) DEFAULT NULL COMMENT '是否撤回',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='私聊表';

/*Data for the table `lf_chat_message` */

/*Table structure for table `lf_comment` */

DROP TABLE IF EXISTS `lf_comment`;

CREATE TABLE `lf_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `pid` int(11) DEFAULT '0' COMMENT '父级id',
  `type` int(1) NOT NULL DEFAULT '1' COMMENT '评论类型:1帖子',
  `uid` bigint(20) NOT NULL DEFAULT '0' COMMENT '评论作者ID',
  `to_uid` int(11) DEFAULT '0' COMMENT '被回复用户ID',
  `post_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '评论帖子ID',
  `content` varchar(255) NOT NULL DEFAULT '' COMMENT '评论内容',
  `status` tinyint(4) DEFAULT '1' COMMENT '评论状态',
  `create_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `img` varchar(255) DEFAULT NULL COMMENT '评论图片',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `uid` (`uid`) USING BTREE,
  KEY `pid` (`pid`) USING BTREE,
  KEY `to_uid` (`to_uid`) USING BTREE,
  KEY `post_id` (`post_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='用户评论表';

/*Data for the table `lf_comment` */

/*Table structure for table `lf_comment_thumbs` */

DROP TABLE IF EXISTS `lf_comment_thumbs`;

CREATE TABLE `lf_comment_thumbs` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `c_id` int(11) NOT NULL COMMENT '评论id',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `c_id` (`c_id`) USING BTREE,
  KEY `uid` (`uid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='评论点赞表';

/*Data for the table `lf_comment_thumbs` */

/*Table structure for table `lf_discuss` */

DROP TABLE IF EXISTS `lf_discuss`;

CREATE TABLE `lf_discuss` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '话题标签id',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `topic_id` int(11) NOT NULL COMMENT '圈子id',
  `title` varchar(30) NOT NULL COMMENT '标题',
  `introduce` varchar(255) NOT NULL COMMENT '描述',
  `read_count` int(255) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `top_type` int(1) DEFAULT '0' COMMENT '推荐位置0不推荐1首页推荐',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `topic_id` (`topic_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='话题表';

/*Data for the table `lf_discuss` */

insert  into `lf_discuss`(`id`,`uid`,`topic_id`,`title`,`introduce`,`read_count`,`top_type`,`create_time`) values (1,2,2,'生活日常','这是生活日常',124,0,'2023-03-12 18:52:34');

/*Table structure for table `lf_follow` */

DROP TABLE IF EXISTS `lf_follow`;

CREATE TABLE `lf_follow` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `follow_uid` int(11) NOT NULL COMMENT '关注的用户id',
  `create_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uid` (`uid`,`follow_uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户关注表';

/*Data for the table `lf_follow` */

/*Table structure for table `lf_friend` */

DROP TABLE IF EXISTS `lf_friend`;

CREATE TABLE `lf_friend` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `my_id` bigint(20) DEFAULT NULL COMMENT '本人id',
  `friend_id` bigint(20) DEFAULT NULL COMMENT '朋友id',
  `notation` varchar(30) DEFAULT NULL COMMENT '备注',
  `session_id` bigint(20) DEFAULT NULL COMMENT '好友标识',
  `last_message` varchar(255) DEFAULT NULL COMMENT '最后一条消息',
  `unread` int(11) DEFAULT NULL COMMENT '未读消息数',
  `is_hidden` bit(1) DEFAULT NULL COMMENT '是否隐藏',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='好友表';

/*Data for the table `lf_friend` */

/*Table structure for table `lf_link` */

DROP TABLE IF EXISTS `lf_link`;

CREATE TABLE `lf_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `cate_id` int(1) NOT NULL DEFAULT '0' COMMENT '分类',
  `url` varchar(255) NOT NULL COMMENT '路径',
  `img` varchar(255) NOT NULL COMMENT '图片',
  `type` int(1) DEFAULT '1' COMMENT '3圈子页轮播图',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='轮播图表';

/*Data for the table `lf_link` */

insert  into `lf_link`(`id`,`title`,`cate_id`,`url`,`img`,`type`,`create_time`) values (48,'用户等级',0,'/subpages/content/level/level','https://pic.linfeng.tech/test/20230810/24620a84971a40ec830b8e9611d984f3.png',3,'2022-01-26 14:03:38'),(62,'短视频',0,'/pages/post/video','https://pic.linfeng.tech/test/20231022/86588853cf3e481a91eb3ab609b152fb.png',3,'2023-10-22 17:39:08'),(64,'v1.12.0小封面',1,'/pages/post/detail?id=8079','https://pic.linfeng.tech/test/20231213/a42b0acd7d554f5dbfda5cce3ad01e53.png',3,'2023-12-13 10:51:55'),(65,'v1.13.0大海报',0,'/pages/post/detail?id=9248','https://pic.linfeng.tech/test/20240228/22cf911537754b8aa30699ed625b384f.png',3,'2024-02-28 14:44:16'),(66,'v1.13.0小海报',1,'/pages/post/detail?id=9248','https://pic.linfeng.tech/test/20240228/16d15637479d4c19bf8382d71f64ea4e.png',3,'2024-02-28 14:44:41');

/*Table structure for table `lf_luckdraw` */

DROP TABLE IF EXISTS `lf_luckdraw`;

CREATE TABLE `lf_luckdraw` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `prize_type` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '奖品类型[1积分2谢谢惠顾3红包]',
  `name` varchar(30) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '名称',
  `image` varchar(200) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '图片',
  `number` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '奖品数量',
  `probability` double(5,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '抽奖几率',
  `sort` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '抽奖状态[0禁用1使用]',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='抽奖物品表';

/*Data for the table `lf_luckdraw` */

insert  into `lf_luckdraw`(`id`,`prize_type`,`name`,`image`,`number`,`probability`,`sort`,`status`,`create_time`,`update_time`) values (1,1,'积分','http://pic.linfeng.tech/test/20220815/c8d6772d6f7e45778046ec59b3e1eb48.png',5,0.30,0,1,'2022-08-14 15:38:22','2022-08-14 15:38:26'),(2,2,'谢谢惠顾','http://pic.linfeng.tech/test/20220815/715e95d91d024b1eb2e299838c587ada.png',0,0.70,0,1,'2022-08-14 16:15:05','2022-08-14 16:15:07'),(3,1,'积分','http://pic.linfeng.tech/test/20220815/c8d6772d6f7e45778046ec59b3e1eb48.png',20,0.28,0,1,'2022-08-14 16:15:32','2022-08-14 16:15:35'),(4,3,'红包','http://pic.linfeng.tech/test/20220815/05e4abda61464c6eba23962258c1d7d2.png',1,0.01,0,1,'2022-08-14 16:16:02','2022-08-14 16:16:05'),(5,1,'积分','http://pic.linfeng.tech/test/20220815/c8d6772d6f7e45778046ec59b3e1eb48.png',30,0.15,0,1,'2022-08-14 16:19:41','2022-08-14 16:19:44'),(6,1,'积分','http://pic.linfeng.tech/test/20220815/c8d6772d6f7e45778046ec59b3e1eb48.png',45,0.08,0,1,'2022-08-14 16:20:11','2022-08-14 16:20:13'),(7,2,'谢谢惠顾','http://pic.linfeng.tech/test/20220815/715e95d91d024b1eb2e299838c587ada.png',0,0.80,0,1,'2022-08-14 16:21:04','2022-08-14 16:21:06'),(8,2,'谢谢惠顾','http://pic.linfeng.tech/test/20220815/715e95d91d024b1eb2e299838c587ada.png',0,0.50,0,1,'2022-08-14 16:21:09','2022-08-14 16:21:12');

/*Table structure for table `lf_luckdraw_record` */

DROP TABLE IF EXISTS `lf_luckdraw_record`;

CREATE TABLE `lf_luckdraw_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(10) unsigned NOT NULL COMMENT '用户ID',
  `prize_id` int(10) NOT NULL COMMENT '奖品ID',
  `prize_type` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '奖品类型',
  `prize_name` varchar(100) CHARACTER SET utf8mb4 NOT NULL COMMENT '奖品名称',
  `prize_image` varchar(200) CHARACTER SET utf8mb4 NOT NULL COMMENT '奖品图片',
  `number` int(10) unsigned NOT NULL COMMENT '获得数量',
  `create_time` datetime DEFAULT NULL COMMENT '抽奖时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='抽奖记录表';

/*Data for the table `lf_luckdraw_record` */

/*Table structure for table `lf_message` */

DROP TABLE IF EXISTS `lf_message`;

CREATE TABLE `lf_message` (
  `m_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `from_uid` int(11) NOT NULL COMMENT '发送者uid',
  `to_uid` int(11) NOT NULL COMMENT '接收者uid',
  `post_id` int(11) DEFAULT NULL COMMENT '帖子id',
  `title` varchar(255) DEFAULT '' COMMENT '推送标题',
  `content` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '消息内容',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '0未读，1已读',
  `type` int(1) NOT NULL COMMENT '1为点赞，2为评论  3为收藏 4为关注  5系统通知',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`m_id`) USING BTREE,
  KEY `from_uid` (`from_uid`) USING BTREE,
  KEY `to_uid` (`to_uid`) USING BTREE,
  KEY `status` (`status`) USING BTREE,
  KEY `post_id` (`post_id`) USING BTREE,
  KEY `type` (`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='消息表';

/*Data for the table `lf_message` */

/*Table structure for table `lf_name_change` */

DROP TABLE IF EXISTS `lf_name_change`;

CREATE TABLE `lf_name_change` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='用户名称修改表';

/*Data for the table `lf_name_change` */

/*Table structure for table `lf_navigation` */

DROP TABLE IF EXISTS `lf_navigation`;

CREATE TABLE `lf_navigation` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(10) NOT NULL COMMENT '标题',
  `img` varchar(255) NOT NULL COMMENT '图片',
  `url` varchar(255) DEFAULT NULL COMMENT '跳转路径',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '跳转类型0页面1外链',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态0正常1禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COMMENT='导航栏表';

/*Data for the table `lf_navigation` */

insert  into `lf_navigation`(`id`,`title`,`img`,`url`,`type`,`status`,`create_time`,`update_time`) values (1,'视频专区','https://pic.linfeng.tech/test/20230810/b3c38245336e44f08c5b3791dde9b8be.png','/pages/post/video',0,0,'2023-02-04 10:42:40','2024-02-20 19:59:14'),(2,'积分签到','https://pic.linfeng.tech/test/20230810/7a91ead9a02b41cd9b6a850475b54bf2.png','/pages/sign/sign',0,0,'2023-02-04 22:49:51','2023-10-21 20:11:08'),(3,'抽奖转盘','https://pic.linfeng.tech/test/20230810/209ed9b464264a9b9b2e0342743fcc93.png','/pages/luck-draw/luck-draw',0,0,'2023-02-04 22:50:24','2024-02-18 20:52:55'),(4,'投票专区','https://pic.linfeng.tech/test/20230810/45da7fdc599643ddbbbc16b54a7cc5f4.png','/pages/my/post?type=4',0,0,'2023-02-04 22:51:00','2023-09-09 16:40:33'),(5,'长文专区','https://pic.linfeng.tech/test/20230810/3fdf593908c74f909710e2ba401602f5.png','/pages/my/post?type=3',0,0,'2023-03-13 09:16:43','2023-09-09 16:40:39'),(7,'圈子分类','https://pic.linfeng.tech/test/20230909/515fa73d0fde44638c16c1fcded7dc47.png','/pages/topic/class-list',0,0,'2023-09-09 18:50:52','2023-09-09 18:50:52'),(8,'我的账户','https://pic.linfeng.tech/test/20230909/e7678df82c304fd78d08b5e254a7266b.png','/pages/account/account',0,0,'2023-09-09 18:52:24','2024-02-20 19:59:20'),(9,'经验等级','https://pic.linfeng.tech/test/20230909/5a95a204c3f14be3bb11b3f7635b0280.png','/subpages/content/level/level',0,0,'2023-09-09 18:53:38','2024-02-18 20:53:08'),(10,'会员中心','https://pic.linfeng.tech/test/20230909/6414031ffe3647388feab7abaa98ca72.png','/pages/user/vip/vip',0,0,'2023-09-09 18:54:41','2024-02-20 19:59:26'),(11,'我的客服','https://pic.linfeng.tech/test/20230909/4bf0c5d496134dcda92532af8ddab70f.png','/pages/user/contact',0,0,'2023-09-09 18:56:29','2023-09-09 18:56:29'),(12,'我的圈子','https://pic.linfeng.tech/test/20230909/40452c98b6864bc0a1ed7651e00b8682.png','/pages/my/topic',0,0,'2023-09-09 18:59:01','2023-09-09 18:59:01'),(13,'系统设置','https://pic.linfeng.tech/test/20230909/9068fb0f9cc7419eaa67c94fc03de31f.png','/pages/user/edit-info/setting',0,0,'2023-09-09 18:59:49','2023-12-12 15:52:53'),(14,'会话中心','https://pic.linfeng.tech/test/20230909/82c995725ac74b2f80acbf24daa7bf02.png','/pages/im/chat-list/chat-list',0,0,'2023-09-09 19:05:27','2023-10-27 10:24:59'),(15,'点赞帖子','https://pic.linfeng.tech/test/20230909/cf723a43c6a8444198971df1b066a486.png','/pages/my/thumb',0,0,'2023-09-09 19:10:45','2023-09-09 19:12:06'),(16,'我的主页','https://pic.linfeng.tech/test/20230909/1eacc8b7bd694d6a98ce58e0ab0674d6.png','/pages/user/home',0,0,'2023-09-09 19:13:05','2023-09-09 19:13:05');

/*Table structure for table `lf_notice` */

DROP TABLE IF EXISTS `lf_notice`;

CREATE TABLE `lf_notice` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `sender_id` bigint(20) DEFAULT NULL COMMENT '发送者id',
  `receiver_id` bigint(20) DEFAULT NULL COMMENT '接收者id',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  `information` tinytext COMMENT '消息内容',
  `is_read` bit(1) DEFAULT b'0' COMMENT '是否已读',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='IM通知表';

/*Data for the table `lf_notice` */

/*Table structure for table `lf_post` */

DROP TABLE IF EXISTS `lf_post`;

CREATE TABLE `lf_post` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `topic_id` int(11) DEFAULT '0' COMMENT '圈子id',
  `discuss_id` int(11) DEFAULT '0' COMMENT '话题id',
  `vote_id` int(11) DEFAULT NULL COMMENT '投票id',
  `title` varchar(50) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COMMENT '内容',
  `media` text COMMENT '文件',
  `read_count` int(255) DEFAULT '0' COMMENT '浏览量',
  `post_top` int(1) DEFAULT '0' COMMENT '置顶',
  `type` int(1) DEFAULT '1' COMMENT '帖子类型：1图文2视频3文章4投票',
  `address` varchar(255) DEFAULT NULL COMMENT '地址名称',
  `longitude` double(10,6) DEFAULT '0.000000' COMMENT '经度',
  `latitude` double(10,6) DEFAULT '0.000000' COMMENT '纬度',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '状态0正常1审核',
  `cut` int(1) NOT NULL DEFAULT '0' COMMENT '0 普通贴  1 付费贴  2 红包贴',
  `pay` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '付费贴价格',
  `brief` varchar(255) DEFAULT '' COMMENT '付费简介',
  `is_private` int(1) DEFAULT '0' COMMENT '关联私密圈:0公开1私密',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `uid` (`uid`) USING BTREE,
  KEY `topic_id` (`topic_id`) USING BTREE,
  KEY `status` (`status`) USING BTREE,
  KEY `idx_is_private` (`is_private`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='帖子表';

/*Data for the table `lf_post` */

insert  into `lf_post`(`id`,`uid`,`topic_id`,`discuss_id`,`vote_id`,`title`,`content`,`media`,`read_count`,`post_top`,`type`,`address`,`longitude`,`latitude`,`create_time`,`status`,`cut`,`pay`,`brief`,`is_private`) values (102,3,2,0,NULL,'卷着微风拂面','想做旷野里的风 \n自由自在不问西东 ​​​','[]',0,0,1,'',0.000000,0.000000,'2025-07-28 10:24:46',0,0,0.00,'',0);

/*Table structure for table `lf_post_collection` */

DROP TABLE IF EXISTS `lf_post_collection`;

CREATE TABLE `lf_post_collection` (
  `uid` int(11) NOT NULL COMMENT '用户id',
  `post_id` int(11) NOT NULL COMMENT '帖子id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='帖子点赞表';

/*Data for the table `lf_post_collection` */

/*Table structure for table `lf_post_fabulous` */

DROP TABLE IF EXISTS `lf_post_fabulous`;

CREATE TABLE `lf_post_fabulous` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `post_id` int(11) NOT NULL COMMENT '帖子id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='帖子收藏表(废弃)';

/*Data for the table `lf_post_fabulous` */

/*Table structure for table `lf_recharge` */

DROP TABLE IF EXISTS `lf_recharge`;

CREATE TABLE `lf_recharge` (
  `id` int(5) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `price` decimal(8,2) DEFAULT NULL COMMENT '充值金额',
  `give_price` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '赠送金额',
  `sort` int(10) NOT NULL DEFAULT '0' COMMENT '排序',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='钱包充值菜单表';

/*Data for the table `lf_recharge` */

insert  into `lf_recharge`(`id`,`price`,`give_price`,`sort`,`status`) values (1,100.00,10.00,10,0),(2,50.00,4.00,11,0),(3,0.01,0.01,12,0);

/*Table structure for table `lf_report` */

DROP TABLE IF EXISTS `lf_report`;

CREATE TABLE `lf_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `media` text COMMENT '文件',
  `content` longtext COMMENT '描述',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `type` int(1) DEFAULT NULL COMMENT '类型1帖子 2评论 3用户 4圈子',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '状态0待审核 1已处理 2已驳回',
  `feedback` varchar(500) DEFAULT NULL COMMENT '平台反馈',
  `link_id` int(11) NOT NULL DEFAULT '0' COMMENT '关联id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='用户举报表';

/*Data for the table `lf_report` */

/*Table structure for table `lf_search` */

DROP TABLE IF EXISTS `lf_search`;

CREATE TABLE `lf_search` (
  `search_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '搜索id',
  `uid` bigint(20) NOT NULL COMMENT '客户id',
  `content` varchar(55) DEFAULT NULL COMMENT '搜索内容',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`search_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8 COMMENT='用户历史搜索信息表';

/*Data for the table `lf_search` */

/*Table structure for table `lf_sensitive` */

DROP TABLE IF EXISTS `lf_sensitive`;

CREATE TABLE `lf_sensitive` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sensitive_word` varchar(500) DEFAULT NULL COMMENT '敏感词库',
  `state` tinyint(1) DEFAULT '0' COMMENT '是否开启 1-是 0-否',
  `handle_measures` tinyint(1) DEFAULT NULL COMMENT '处理措施  1-禁止发布 2-需审核',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='敏感词库信息表';

/*Data for the table `lf_sensitive` */

insert  into `lf_sensitive`(`id`,`sensitive_word`,`state`,`handle_measures`) values (1,'妈的,卧槽',1,1);

/*Table structure for table `lf_sign_config` */

DROP TABLE IF EXISTS `lf_sign_config`;

CREATE TABLE `lf_sign_config` (
  `id` int(5) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `day` varchar(50) DEFAULT NULL COMMENT '天数',
  `sign_num` varchar(10) DEFAULT NULL COMMENT '积分数',
  `sort` int(10) NOT NULL DEFAULT '1' COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='签到配置表';

/*Data for the table `lf_sign_config` */

insert  into `lf_sign_config`(`id`,`day`,`sign_num`,`sort`) values (1,'第一天','10',1),(2,'第二天','20',2),(3,'第三天','30',3),(4,'第四天','40',4),(5,'第五天','50',5),(6,'第六天','60',6),(7,'第七天','100',7);

/*Table structure for table `lf_system` */

DROP TABLE IF EXISTS `lf_system`;

CREATE TABLE `lf_system` (
  `config` varchar(50) NOT NULL,
  `value` text,
  `extend` text,
  `intro` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`config`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='参数配置表(废弃)';

/*Data for the table `lf_system` */

/*Table structure for table `lf_topic` */

DROP TABLE IF EXISTS `lf_topic`;

CREATE TABLE `lf_topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '圈子id',
  `uid` int(11) NOT NULL COMMENT '创建用户id',
  `cate_id` int(11) NOT NULL COMMENT '分类id',
  `topic_name` varchar(20) NOT NULL COMMENT '圈子名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '描述',
  `cover_image` varchar(100) DEFAULT '' COMMENT 'logo',
  `bg_image` varchar(100) DEFAULT NULL COMMENT '背景图',
  `top_type` int(1) DEFAULT '0' COMMENT '推荐类型：0 不推荐1首页推荐2圈子页推荐',
  `status` int(1) DEFAULT '0' COMMENT '圈子状态：0 正常 ，1禁用',
  `index_recommend` int(1) DEFAULT '1' COMMENT '是否首页推荐圈子内容',
  `user_num` int(11) DEFAULT '1' COMMENT '加入人数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `rest` int(1) DEFAULT '0' COMMENT '进圈条件0无限制1答题并审核',
  `question` varchar(255) DEFAULT NULL COMMENT '问题内容设置',
  `is_privacy` int(1) DEFAULT '0' COMMENT '是否私密：0公开1私密',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `topic_name` (`topic_name`) USING BTREE,
  KEY `idx_lt_uid` (`uid`),
  KEY `idx_lt_is_privacy` (`is_privacy`),
  KEY `idx_lt_cate_id` (`cate_id`),
  KEY `idx_lt_status` (`status`),
  KEY `idx_lt_rest` (`rest`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='圈子表';

/*Data for the table `lf_topic` */

insert  into `lf_topic`(`id`,`uid`,`cate_id`,`topic_name`,`description`,`cover_image`,`bg_image`,`top_type`,`status`,`index_recommend`,`user_num`,`create_time`,`rest`,`question`,`is_privacy`) values (2,4,4,'官方圈子','林风官方圈子，用户默认加入本圈','https://pic.linfeng.tech/test/20241209/ec48f96a4f97481684a368cb206a0e4c.png','https://pic.linfeng.tech/test/20220424/524e8b5e5cd846bb9170bc8392f00805.jpg',0,0,1,3,'2025-01-01 10:00:00',0,NULL,0);

/*Table structure for table `lf_topic_admin` */

DROP TABLE IF EXISTS `lf_topic_admin`;

CREATE TABLE `lf_topic_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `topic_id` int(11) NOT NULL COMMENT '圈子id',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `topic_id` (`topic_id`,`uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='圈子管理员表';

/*Data for the table `lf_topic_admin` */

insert  into `lf_topic_admin`(`id`,`topic_id`,`uid`,`create_time`) values (1,20,4,'2022-01-30 19:33:14'),(2,20,3,'2022-01-30 19:33:29');

/*Table structure for table `lf_topic_apply` */

DROP TABLE IF EXISTS `lf_topic_apply`;

CREATE TABLE `lf_topic_apply` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `topic_id` int(11) NOT NULL COMMENT '圈子id',
  `status` int(1) DEFAULT '0' COMMENT '状态0待审核1已通过2已拒绝',
  `answer` varchar(255) DEFAULT NULL COMMENT '回答',
  `question` varchar(255) DEFAULT NULL COMMENT '问题',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `operate_id` int(11) DEFAULT NULL COMMENT '审核用户id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='进圈申请表';

/*Data for the table `lf_topic_apply` */

/*Table structure for table `lf_topic_block` */

DROP TABLE IF EXISTS `lf_topic_block`;

CREATE TABLE `lf_topic_block` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `operate_id` int(11) NOT NULL COMMENT '操作用户id',
  `topic_id` int(11) NOT NULL COMMENT '圈子id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='圈子用户拉黑表';

/*Data for the table `lf_topic_block` */

/*Table structure for table `lf_topic_top` */

DROP TABLE IF EXISTS `lf_topic_top`;

CREATE TABLE `lf_topic_top` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `topic_id` int(11) NOT NULL COMMENT '圈子id',
  `post_id` int(11) NOT NULL COMMENT '帖子id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `topic_id` (`topic_id`,`post_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='圈内帖子置顶表';

/*Data for the table `lf_topic_top` */

/*Table structure for table `lf_user` */

DROP TABLE IF EXISTS `lf_user`;

CREATE TABLE `lf_user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(11) CHARACTER SET utf8 DEFAULT NULL COMMENT '手机号',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8 DEFAULT NULL COMMENT '密码',
  `group_id` int(11) NOT NULL DEFAULT '2' COMMENT '用户组',
  `avatar` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '头像',
  `gender` int(1) NOT NULL DEFAULT '0' COMMENT '性别(0未知，1男，2女)',
  `province` varchar(50) CHARACTER SET utf8 DEFAULT '' COMMENT '省份',
  `city` varchar(20) CHARACTER SET utf8 DEFAULT '' COMMENT '城市',
  `openid` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '小程序openid',
  `mp_openid` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '公众号openid',
  `unionid` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT 'unionid',
  `status` int(10) NOT NULL DEFAULT '0' COMMENT '状态',
  `intro` varchar(255) DEFAULT '这个人很懒，没留下什么' COMMENT '个性签名',
  `money` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '用户余额',
  `sign_num` int(2) NOT NULL DEFAULT '0' COMMENT '连续签到天数',
  `integral` int(255) DEFAULT '0' COMMENT '积分',
  `last_login_ip` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '最后登录ip',
  `tag_str` varchar(255) CHARACTER SET utf8 DEFAULT '' COMMENT '用户标签',
  `vip` int(2) DEFAULT '0' COMMENT '0普通用户1会员',
  `vip_expire_time` datetime DEFAULT NULL COMMENT '会员过期时间',
  `type` int(1) DEFAULT '0' COMMENT '0为普通用户，1官方账号2马甲虚拟用户',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `level` int(2) DEFAULT '1' COMMENT '用户等级',
  `bg_img` varchar(255) DEFAULT NULL COMMENT '背景图',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='用户表';

/*Data for the table `lf_user` */

insert  into `lf_user`(`uid`,`email`,`mobile`,`username`,`password`,`group_id`,`avatar`,`gender`,`province`,`city`,`openid`,`mp_openid`,`unionid`,`status`,`intro`,`money`,`sign_num`,`integral`,`last_login_ip`,`tag_str`,`vip`,`vip_expire_time`,`type`,`update_time`,`create_time`,`level`,`bg_img`) values (1,NULL,'15678876545','林风社交论坛',NULL,2,'http://pic.linfeng.tech/test/20220126/4515fc2cbed74d0b9163d35a12bd4c3b.png',2,'','浙江省宁波市','o7yfg4vyubvvxgjtaIuuuvcxcfGA',NULL,'',0,'这个人很懒，没留下什么',0.00,0,0,'39.130.106.136','[\"萌新\",\"码农\",\"发帖达人\",\"运动\"]',0,NULL,0,'2025-07-01 15:12:28','2025-07-01 15:12:28',1,NULL),(2,NULL,'15678870000','linfengtech',NULL,2,'http://pic.linfeng.tech/test/20220126/a58ae30c4bc44942afa4b9752bedc59b.jpeg',1,'','浙江省宁波市','o8cxg9vudddhsjjOK33TavcklbG',NULL,NULL,0,'这个人很懒，没留下什么',0.00,0,0,'39.130.106.135','[\"萌新\",\"码农\",\"发帖达人\",\"运动\"]',0,NULL,0,'2025-07-01 15:12:28','2025-07-01 15:12:28',1,NULL),(3,NULL,'15105701666','山觉林风',NULL,2,'https://pic.linfeng.tech/test/20240121/a596708da9ea45adb43958db0a937101.jpg',0,'','浙江省宁波市','o6z2m5Cc8jzhvANf42L5LQQRXsu',NULL,NULL,0,'哈哈哈哈哈',3803.58,1,10,'0:0:0:0:0:0:0:1','[\"萌新\",\"圈子达人\",\"旅行\",\"软件开发\"]',0,NULL,0,'2025-07-01 15:12:28','2025-07-01 15:12:28',1,NULL),(4,NULL,'15869090000','林风',NULL,2,'https://thirdwx.qlogo.cn/mmopen/vi_32/DicojWMHWdvIFD9tQa7XBt1iaTBQtYJ4icLE9z971WErrNXLLdFmPvACiaHpmibuCKicoTR8S1Z7iayXYynRwEFgVqlnw/132',1,'','浙江省宁波市','o6z2m5DdnR0-9BdqZfK3Z3f_i460',NULL,NULL,0,'这个人真的很懒',910.00,0,0,'0:0:0:0:0:0:0:1','[\"萌新\",\"码农\",\"发帖达人\",\"运动\"]',0,NULL,1,'2025-07-01 15:12:28','2025-07-01 15:12:28',1,NULL);

/*Table structure for table `lf_user_level` */

DROP TABLE IF EXISTS `lf_user_level`;

CREATE TABLE `lf_user_level` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(20) NOT NULL COMMENT '名称',
  `min_num` int(10) DEFAULT '0' COMMENT '最小要求值',
  `max_num` int(10) DEFAULT '0' COMMENT '最大要求值',
  `level_id` int(10) DEFAULT '0' COMMENT '等级id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='用户经验值设置表';

/*Data for the table `lf_user_level` */

insert  into `lf_user_level`(`id`,`name`,`min_num`,`max_num`,`level_id`) values (1,'LV1',0,100,1),(2,'LV2',101,300,2),(3,'LV3',301,500,3),(4,'LV4',501,1000,4),(5,'LV5',1001,50000,5);

/*Table structure for table `lf_user_menu` */

DROP TABLE IF EXISTS `lf_user_menu`;

CREATE TABLE `lf_user_menu` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `url` varchar(255) NOT NULL COMMENT '跳转路径',
  `img` varchar(255) NOT NULL COMMENT '图片地址',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `sort` int(10) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COMMENT='用户菜单';

/*Data for the table `lf_user_menu` */

insert  into `lf_user_menu`(`id`,`url`,`img`,`name`,`sort`,`status`) values (1,'/pages/my/discuss','http://pic.linfeng.tech/test/20220722/fbf429d896ef468ca9fafce3bdd0a137.png','我的话题',100,0),(2,'/pages/my/topic','http://pic.linfeng.tech/test/20220722/f4ced6e7a8384225ba6c320ad4e46dbe.png','我的圈子',99,0),(3,'/pages/my/post','http://pic.linfeng.tech/test/20220722/71a8cd4a20ae45a7843d353ede04b13f.png','我的帖子',98,0),(4,'/pages/my/thumb','http://pic.linfeng.tech/test/20220722/389251d678a34c7990275715a52ad7b1.png','点赞帖子',97,0),(5,'/pages/my/fans','http://pic.linfeng.tech/test/20220722/1626406dcdb146f2a3dd51800150323a.png','我的粉丝',96,0),(6,'/pages/my/follow','http://pic.linfeng.tech/test/20220722/2f9a6ded66e94554a9a7a166de053ed9.png','我的关注',95,0),(7,'/pages/pay/pay','http://pic.linfeng.tech/test/20220722/199b6519fcf842e381e2813c2f789a1d.png','账户充值',94,0),(8,'/pages/account/account','http://pic.linfeng.tech/test/20220722/85e84b1958ee462aacd54200081cd9fb.png','我的账户',93,0),(9,'/pages/sign/sign','http://pic.linfeng.tech/test/20220722/f1d127ca595d4c9e83ec6d3643819d09.png','积分签到',92,0),(10,'/pages/sign/integral','http://pic.linfeng.tech/test/20220722/513bcc2451ae4fada898baa7357af7da.png','我的积分',91,0),(11,'/pages/im/chat-list/chat-list','http://pic.linfeng.tech/test/20220722/db60a7442c96491f941d167d4dde990f.png','私信列表',90,0),(12,'/pages/user/home','http://pic.linfeng.tech/test/20220722/e19447ac0fae4885aa113d68b5ed4362.png','我的主页',89,0),(13,'/pages/user/contact','http://pic.linfeng.tech/test/20220812/acbea265fbee4d588fe60b8a0fc1a263.png','客服中心',0,0),(14,'/pages/luck-draw/luck-draw','http://pic.linfeng.tech/test/20220815/9b4eadd5daf24b23aa7491fda990e354.png','积分抽奖',88,0),(15,'/pages/report/list','http://pic.linfeng.tech/test/20220901/b9954958aaa241c0a28c75df684a0f60.png','举报反馈',85,0),(16,'/pages/user/vip/vip','http://pic.linfeng.tech/test/20221007/ec5ddb72e2084b3a9d013f9df889cc62.png','会员中心',84,0),(17,'/pages/user/edit-info/setting','https://pic.linfeng.tech/test/20221128/785f87fb7ce946a2a340048bc4510f8d.png','系统设置',83,0),(18,'/subpages/content/level/level','https://pic.linfeng.tech/test/20230808/4bd3f38abd9d4f868caeee04a7551e28.png','用户等级',82,0),(19,'/pages/topic/class-list','https://pic.linfeng.tech/test/20230809/71ffd27341bc4a5ba1cf01689860a2b6.png','全部圈子',83,0);

/*Table structure for table `lf_user_recharge` */

DROP TABLE IF EXISTS `lf_user_recharge`;

CREATE TABLE `lf_user_recharge` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int(10) DEFAULT NULL COMMENT '充值用户UID',
  `nickname` varchar(50) DEFAULT '' COMMENT '用户昵称',
  `order_id` varchar(32) DEFAULT NULL COMMENT '订单号',
  `price` decimal(8,2) DEFAULT NULL COMMENT '充值金额',
  `give_price` decimal(8,2) DEFAULT '0.00' COMMENT '购买赠送金额或会员套餐ID',
  `recharge_type` varchar(32) DEFAULT NULL COMMENT '充值类型',
  `paid` tinyint(1) DEFAULT NULL COMMENT '是否充值',
  `pay_time` datetime DEFAULT NULL COMMENT '充值支付时间',
  `add_time` datetime DEFAULT NULL COMMENT '充值时间',
  `refund_price` decimal(10,2) DEFAULT '0.00' COMMENT '退款金额',
  `transaction_id` varchar(255) DEFAULT NULL COMMENT '支付生成的订单号',
  `out_trade_no` varchar(255) DEFAULT NULL COMMENT '订单支付编号',
  `type` int(2) DEFAULT '0' COMMENT '类型0钱包充值1会员购买充值',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `order_id` (`order_id`) USING BTREE,
  KEY `uid` (`uid`) USING BTREE,
  KEY `recharge_type` (`recharge_type`) USING BTREE,
  KEY `paid` (`paid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户充值表';

/*Data for the table `lf_user_recharge` */

/*Table structure for table `lf_user_setting` */

DROP TABLE IF EXISTS `lf_user_setting`;

CREATE TABLE `lf_user_setting` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `uid` int(10) DEFAULT NULL COMMENT '用户ID',
  `is_follow` tinyint(1) DEFAULT '0' COMMENT '隐藏粉丝 0否1是',
  `is_watch` tinyint(1) DEFAULT '0' COMMENT '隐藏关注 0否1是',
  `is_post` tinyint(1) DEFAULT '0' COMMENT '隐藏作品 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='用户隐私设置表';

/*Data for the table `lf_user_setting` */

/*Table structure for table `lf_user_sign` */

DROP TABLE IF EXISTS `lf_user_sign`;

CREATE TABLE `lf_user_sign` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `uid` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '签到说明',
  `number` int(11) NOT NULL DEFAULT '0' COMMENT '获得积分',
  `balance` int(11) NOT NULL DEFAULT '0' COMMENT '剩余积分',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `uid` (`uid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='签到记录表';

/*Data for the table `lf_user_sign` */

/*Table structure for table `lf_user_topic` */

DROP TABLE IF EXISTS `lf_user_topic`;

CREATE TABLE `lf_user_topic` (
  `uid` int(11) NOT NULL COMMENT '用户id',
  `topic_id` int(11) NOT NULL COMMENT '圈子id',
  `create_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  UNIQUE KEY `uid` (`uid`,`topic_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='圈内用户表';

/*Data for the table `lf_user_topic` */

insert  into `lf_user_topic`(`uid`,`topic_id`,`create_time`) values (1,2,'2022-01-25 21:48:07'),(3,2,'2022-02-20 16:52:54'),(4,2,'2022-03-05 19:42:28');

/*Table structure for table `lf_vip_benefit` */

DROP TABLE IF EXISTS `lf_vip_benefit`;

CREATE TABLE `lf_vip_benefit` (
  `id` int(5) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(255) DEFAULT NULL COMMENT '会员权益标题',
  `describes` varchar(255) DEFAULT NULL COMMENT '会员权益描述',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `status` int(1) DEFAULT '0' COMMENT '状态0有效1无效',
  `sort` int(10) DEFAULT '1' COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='会员权益表';

/*Data for the table `lf_vip_benefit` */

insert  into `lf_vip_benefit`(`id`,`title`,`describes`,`icon`,`status`,`sort`) values (1,'专属标志','会员尊享专属身份标识','https://pic.linfeng.tech/test/20231222/40eb111c1d8e4d80ba94590d599a2f2e.png',0,25),(2,'改名次数','会员更改昵称次数：8次/月','https://pic.linfeng.tech/test/20231222/b458c7e755a3466da654b048971ebbcf.png',0,21),(3,'圈子数量','会员可创建8个圈子','https://pic.linfeng.tech/test/20231222/795ca4a5b61a4175a2eadae28bb85393.png',0,22),(4,'积分特权','会员可尊享积分签到奖励、发帖奖励2倍特权','https://pic.linfeng.tech/test/20231222/4d368ce7860940b9affe6d6947d37b08.png',0,23),(5,'广告过滤','会员自动过滤页面广告内容','https://pic.linfeng.tech/test/20231222/5bc6fc6aa99d40dba73a97437697a08c.png',0,22),(6,'付费贴发布','会员可解锁付费贴发布权限','https://pic.linfeng.tech/test/20231222/4e95d8776f7f420e9c698330dd2b9f92.png',0,18),(7,'私密圈创建','会员可解锁私密圈创建权限','https://pic.linfeng.tech/test/20231222/9c3a8c0e27a94a4d9a8e7e709d323c1b.png',0,17),(8,'经验等级','会员的经验等级增长更快速','https://pic.linfeng.tech/test/20231222/d6c3f536e5d249638ab4226bc274af9c.png',0,16);

/*Table structure for table `lf_vip_option` */

DROP TABLE IF EXISTS `lf_vip_option`;

CREATE TABLE `lf_vip_option` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `valid_days` int(10) NOT NULL DEFAULT '0' COMMENT '有效天数',
  `price` decimal(8,2) DEFAULT NULL COMMENT '价格',
  `remark` varchar(50) DEFAULT NULL COMMENT '描述',
  `sort` int(5) NOT NULL DEFAULT '1' COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='会员充值选项表';

/*Data for the table `lf_vip_option` */

insert  into `lf_vip_option`(`id`,`name`,`valid_days`,`price`,`remark`,`sort`) values (1,'一个月',30,9.99,'一个月优惠价',1),(2,'两个月',60,18.99,'两个月惊爆价',2),(3,'三个月',90,27.99,'三个月活动价',3);

/*Table structure for table `lf_vote_option` */

DROP TABLE IF EXISTS `lf_vote_option`;

CREATE TABLE `lf_vote_option` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `vote_id` int(11) NOT NULL COMMENT '投票题目id',
  `content` varchar(14) NOT NULL COMMENT '内容',
  `ticket_num` int(11) DEFAULT '0' COMMENT '投票数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='投票选项票数表';

/*Data for the table `lf_vote_option` */

/*Table structure for table `lf_vote_result` */

DROP TABLE IF EXISTS `lf_vote_result`;

CREATE TABLE `lf_vote_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `vote_id` int(11) NOT NULL COMMENT '投票id',
  `result` varchar(255) NOT NULL COMMENT '结果',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uid` (`uid`,`vote_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='投票结果表';

/*Data for the table `lf_vote_result` */

/*Table structure for table `lf_vote_subject` */

DROP TABLE IF EXISTS `lf_vote_subject`;

CREATE TABLE `lf_vote_subject` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(20) NOT NULL COMMENT '标题',
  `type` int(1) DEFAULT '1' COMMENT '1单选2多选',
  `expire_time` datetime NOT NULL COMMENT '到期时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='投票问题信息表';

/*Data for the table `lf_vote_subject` */

/*Table structure for table `qrtz_blob_triggers` */

DROP TABLE IF EXISTS `qrtz_blob_triggers`;

CREATE TABLE `qrtz_blob_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_blob_triggers` */

/*Table structure for table `qrtz_calendars` */

DROP TABLE IF EXISTS `qrtz_calendars`;

CREATE TABLE `qrtz_calendars` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_calendars` */

/*Table structure for table `qrtz_cron_triggers` */

DROP TABLE IF EXISTS `qrtz_cron_triggers`;

CREATE TABLE `qrtz_cron_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_cron_triggers` */

/*Table structure for table `qrtz_fired_triggers` */

DROP TABLE IF EXISTS `qrtz_fired_triggers`;

CREATE TABLE `qrtz_fired_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_fired_triggers` */

/*Table structure for table `qrtz_job_details` */

DROP TABLE IF EXISTS `qrtz_job_details`;

CREATE TABLE `qrtz_job_details` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_job_details` */

/*Table structure for table `qrtz_locks` */

DROP TABLE IF EXISTS `qrtz_locks`;

CREATE TABLE `qrtz_locks` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_locks` */

/*Table structure for table `qrtz_paused_trigger_grps` */

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;

CREATE TABLE `qrtz_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_paused_trigger_grps` */

/*Table structure for table `qrtz_scheduler_state` */

DROP TABLE IF EXISTS `qrtz_scheduler_state`;

CREATE TABLE `qrtz_scheduler_state` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_scheduler_state` */

/*Table structure for table `qrtz_simple_triggers` */

DROP TABLE IF EXISTS `qrtz_simple_triggers`;

CREATE TABLE `qrtz_simple_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_simple_triggers` */

/*Table structure for table `qrtz_simprop_triggers` */

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;

CREATE TABLE `qrtz_simprop_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_simprop_triggers` */

/*Table structure for table `qrtz_triggers` */

DROP TABLE IF EXISTS `qrtz_triggers`;

CREATE TABLE `qrtz_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_triggers` */

/*Table structure for table `schedule_job` */

DROP TABLE IF EXISTS `schedule_job`;

CREATE TABLE `schedule_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `cron_expression` varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  `status` tinyint(4) DEFAULT NULL COMMENT '任务状态  0：正常  1：暂停',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='定时任务';

/*Data for the table `schedule_job` */

insert  into `schedule_job`(`job_id`,`bean_name`,`params`,`cron_expression`,`status`,`remark`,`create_time`) values (1,'messageTask','3','0 0 0/12 * * ? ',1,'清理n个月前的所有系统消息','2022-05-09 22:32:44'),(3,'robotTask','1','0 0 0/2 * * ? ',1,'机器人自动注册并发文字帖','2022-07-21 16:01:58'),(5,'redisCacheClean','0','0 15 3 ? * *',0,'redis缓存数据清理','2023-10-21 17:06:23'),(6,'activeUserRecordTask','7','0 15 3 ? * *',1,'访客记录数据清理定时任务，参数表示清理n天前的所有访客记录','2024-05-30 11:57:54'),(7,'databaseBackupTask','0','0 0 2 * * ?',1,'数据库备份任务','2025-07-08 14:30:16');

/*Table structure for table `schedule_job_log` */

DROP TABLE IF EXISTS `schedule_job_log`;

CREATE TABLE `schedule_job_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志id',
  `job_id` bigint(20) NOT NULL COMMENT '任务id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `status` tinyint(4) NOT NULL COMMENT '任务状态    0：成功    1：失败',
  `error` varchar(2000) DEFAULT NULL COMMENT '失败信息',
  `times` int(11) NOT NULL COMMENT '耗时(单位：毫秒)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `job_id` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COMMENT='定时任务日志';

/*Data for the table `schedule_job_log` */

/*Table structure for table `sys_captcha` */

DROP TABLE IF EXISTS `sys_captcha`;

CREATE TABLE `sys_captcha` (
  `uuid` char(36) NOT NULL COMMENT 'uuid',
  `code` varchar(6) NOT NULL COMMENT '验证码',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统验证码';

/*Data for the table `sys_captcha` */

/*Table structure for table `sys_config` */

DROP TABLE IF EXISTS `sys_config`;

CREATE TABLE `sys_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `param_key` varchar(50) DEFAULT NULL COMMENT 'key',
  `param_value` varchar(5000) DEFAULT NULL COMMENT 'value',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态   0：隐藏   1：显示',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `param_key` (`param_key`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COMMENT='系统配置信息表';

/*Data for the table `sys_config` */

insert  into `sys_config`(`id`,`param_key`,`param_value`,`status`,`remark`) values (1,'CLOUD_STORAGE_CONFIG_KEY','{\"type\":2,\"qiniuDomain\":\"https://pic.linfeng.tech\",\"qiniuPrefix\":\"test\",\"qiniuAccessKey\":\"QINIU_ACCESS_KEY_PLACEHOLDER\",\"qiniuSecretKey\":\"QINIU_SECRET_KEY_PLACEHOLDER\",\"qiniuBucketName\":\"linfengtech\",\"aliyunDomain\":\"https://linfengbbs.oss-cn-shenzhen.aliyuncs.com\",\"aliyunPrefix\":\"\",\"aliyunEndPoint\":\"https://oss-cn-shenzhen.aliyuncs.com/\",\"aliyunAccessKeyId\":\"ALIYUN_ACCESS_KEY_ID_PLACEHOLDER\",\"aliyunAccessKeySecret\":\"ALIYUN_ACCESS_KEY_SECRET_PLACEHOLDER\",\"aliyunBucketName\":\"linfengbbs\"}',0,'云存储配置信息'),(3,'WxAppId','wx1d433967c92bda',1,'微信小程序appId'),(4,'wxAppSecret','WX_APP_SECRET_PLACEHOLDER',1,'微信小程序密钥'),(5,'wxPayKey','1000093596',1,'微信商户号'),(6,'wxPaySecret','WX_PAY_SECRET_PLACEHOLDER',1,'微信商户密钥'),(7,'appNotifyurl','https://wxapi.linfeng.tech/app/pay/rolBack',1,'支付回调地址'),(8,'redirectUrl','https://h5.linfeng.tech/#/pages/bill/bill?types=0',1,'h5支付之后跳转地址'),(9,'normalPost','1',1,'普通贴是否需要人工审核'),(10,'vipPost','1',1,'付费贴是否需要人工审核'),(11,'postPrice','50',1,'付费贴抽成'),(13,'sms_sign','林风论坛',1,'阿里云短信签名'),(14,'sms_templateId','SMS_201711111',1,'模板id'),(15,'sms_access_secret','SMS_ACCESS_SECRET_PLACEHOLDER',1,'短信密钥'),(16,'sms_access_key','SMS_ACCESS_KEY_PLACEHOLDER',1,'短信key'),(17,'sms_region','cn-hangzhou',1,'region地区'),(18,'sms_open','1',1,'短信是否开启'),(19,'isOpen','1',1,'视频入口开启'),(20,'chargeIsOpen','0',1,'充值开关是否开启'),(21,'exchange','0',1,'积分兑换余额的开关是否开启'),(22,'integral','100',1,'兑换一块钱需要的积分数'),(23,'img','http://pic.linfeng.tech/test/20220724/9a665bf276a44827ad8ef0b3140a7d1d.png',1,'项目logo'),(24,'noticeContent','欢迎光临林风社交论坛标准商业版',1,'公告内容'),(25,'contactWechat','11111111',1,'客服微信号'),(26,'contactWechatQr','https://pic.linfeng.tech/test/20250728/4c08d6ef9e7247739a2107503b0debd4.jpg',1,'客服微信二维码'),(27,'contactTime','每天7:30-22:30',1,'客服工作时间'),(28,'contactPhone','0571-8888888',1,'客服手机号'),(29,'surplus','4',1,'每日抽奖次数'),(30,'luckDrawStatus','1',1,'抽奖是否开启 0=未开启  1=进行中'),(31,'luckDrawRule','1.每次抽奖消耗一定数量的积分，积分从您的账户自动扣除；\n2.每天可抽奖的次数有限；\n3.可以通过积分签到获取积分；\n4.此抽奖活动最终解释权归本平台所有；\n5.用户中奖后平台会自动发放奖励积分或奖励红包至用户的账户中；',1,'抽奖活动规则'),(32,'luckDrawIntegral','2',1,'每次抽奖消耗的积分数量'),(33,'protocol','1、用户在本网站注册时，不得使用虚假身份信息。用户应当妥善保存其账户信息和密码，由于用户泄密所导致的损失需由用户自行承担。如用户发现他人冒用或盗用其账户或密码，或其账户存在其他未经合法授权使用之情形，应立即以有效方式通知本公司。用户理解并同意本公司有权根据用户的通知、请求或依据判断，采取相应的行动或措施，包括但不限于冻结账户、限制账户功能等，本公司对采取上述行动所导致的损失不承担除法律有明确规定外的责任。\n\n2、用户在使用本服务时须遵守法律法规，不得利用本服务从事违法违规行为，包括但不限于：\n\n（1）发布、传送、传播、储存危害国家安全统一、破坏社会稳定、违反公序良俗、侮辱、诽谤、淫秽、暴力以及任何违反国家法律法规的内容；\n\n（2）发布、传送、传播、储存侵害他人知识产权、商业秘密等合法权利的内容；\n\n（3）恶意虚构事实、隐瞒真相以误导、欺骗他人；\n\n（4）发布、传送、传播广告信息及垃圾信息；\n\n（5）其他法律法规禁止的行为。\n\n3、用户不得利用本服务进行任何有损本公司及其关联企业之权利、利益及商誉，或其他用户合法权利之行为。\n\n4、用户不得基于本服务从事制作、使用、传播“私服”、“外挂”等侵害本公司合法权益的行为。如有违反，本公司将依据中国现行法律法规及本公司的相关规定予以处理。\n\n5、虚拟财产转移服务外，用户不得通过任何方式直接或变相进行游戏账号、游戏币、游戏道具等虚拟财产的转移。\n\n6、用户不得从事任何利用本公司平台系统漏洞进行有损其他用户、本公司或互联网安全的行为。\n\n7、用户知悉并确认，本公司通过公告、邮件、短信、账户通知以及用户在账户中登记的即时通讯工具等方式，向用户发出关于本服务的通知、规则、提示等信息，均为有效通知。该等信息一经公布或发布，即视为已送达至用户。',1,'用户服务协议'),(34,'privacy','    我们重视用户的隐私。您在使用我们的服务时，我们可能会收集和使用您的相关信息。我们希望通过本《隐私政策》向您说明，在使用我们的服务时，我们如何收集、使用、储存和分享这些信息，以及我们为您提供的访问、更新、控制和保护这些信息的方式。\n    本《隐私政策》与您所使用的服务息息相关，希望您仔细阅读，在需要时，按照本《隐私政策》的指引，作出您认为适当的选择。本《隐私政策》中涉及的相关技术词汇，我们尽量以简明扼要的表述，并提供进一步说明的链接，以便您的理解。\n    您使用或继续使用我们的服务，即意味着同意我们按照本《隐私政策》收集、使用、储存和分享您的相关信息。\n    如对本《隐私政策》或相关事宜有任何问题，请与我们联系。',1,'个人隐私协议'),(35,'emailLogin','0',1,'是否开启邮箱登录 0关闭 1开启'),(36,'app_appid','1872812888',1,'APP端的appid'),(37,'bgImg','https://pic.linfeng.tech/test/20251104/4c4e9965c64a46d4a58fbc76ba34d497.png',1,'用户页面背景图'),(38,'vipIntegral','2',1,'会员积分奖励翻倍数'),(39,'commonRename','1',1,'普通用户每月改名次数限制'),(40,'vipRename','3',1,'会员改名每月次数限制'),(41,'vipTopicNumber','8',1,'会员创建圈子数量限制'),(42,'commonTopicNumber','2',1,'普通用户创建圈子数量限制'),(43,'addPostIntegral','5',1,'发帖积分奖励'),(44,'vipAgreeContent','为使用充值服务，用户（以下或称“您）应当阅读并遵守《网络服务协议》、《充值协议》(以下简称“本协议”) 及本平台（以下或简称“平台”）充值相关规则、指引。请您务必审慎阅读、充分理解各条款内容，特别是免除或者限制责任的条款。如您不同意本协议项下任何一条或一款内容的，应当立即终止阅读并停止充值服务。如您完成充值流程，视为对本协议的认可并承诺依本协议各项约定履行。您不应再以不了解本协议内容为由拒绝履行本协议或拒绝履行本协议项下任何一条或一款内容。完成充值流程以您完成付款为准。本服务仅向18周岁以上的完全民事行为能力人提供，如果您不符合前述要求，不论您的监护人是否同意，任何情况下您都不得使用充值服务。',1,'会员服务协议'),(45,'isTrusted','true',1,NULL),(47,'canCashOut','1',1,'是否开启提现0关闭1开启'),(48,'adIsOpen','0',1,'流量主广告开关0关闭1开启'),(49,'wxAdpid','1998382706',1,'流量主广告微信小程序的adpid'),(50,'h5Adpid','1818425366',1,'流量主广告H5的adpid'),(51,'indexStyle1','0',1,'首页-最新-帖子列表样式 0普通样式 1瀑布流样式'),(52,'indexStyle2','1',1,'首页-圈子-帖子列表样式 0普通样式 1瀑布流样式'),(53,'indexStyle3','2',1,'首页-关注-帖子列表样式 0普通样式 1瀑布流样式'),(54,'vipShow','0',1,'会员入口开关 0开启 1关闭'),(55,'showType','1',1,'帖子详情页样式0格子布局1轮播布局'),(56,'popupOpen','1',1,'弹框公告是否开启0开启1关闭'),(57,'popTitle','公告',1,'首页弹框的标题'),(59,'popContent','当前站点为林风社交论坛标准商业版。林风社交论坛微信小程序/H5/app版本是前后端分离的社交论坛问答发帖。',1,'首页弹框的内容'),(60,'popTime','3',1,'首页弹框展示频率 单位天'),(61,'addPostIntegralLimit','2',1,'每天限制发帖积分奖励次数'),(62,'rewardBtn','1',1,'帖子详情页打赏按钮 0不显示 1显示'),(63,'payPostbtn','1',1,'付费贴开关0开启1关闭'),(64,'payPostVip','0',1,'0所有人可发付费贴 1仅会员可发付费贴'),(65,'privateCirclesOpen','1',1,'私密圈子创建入口0关闭1开启'),(66,'privateCircleNeedVip','0',1,'私密圈子创建0所有人可发 1仅会员可发'),(67,'commentCheck','0',1,'评论发布审核 0直接过审1需要审核'),(68,'openEmailRegister','1',1,'邮箱注册 0关闭1开放'),(69,'circleCheck','0',1,'圈子发布审核 0直接过审 2需要审核'),(70,'WxMpAppId','wxa111c6f570231112',1,'微信公众号AppId'),(71,'WxMpSecret','WX_MP_SECRET_PLACEHOLDER',1,'微信公众号密钥'),(72,'iosClose','1',1,'IOS机型虚拟支付相关功能 0关闭1开启'),(73,'tencentSecretId','TENCENT_SECRET_ID_PLACEHOLDER',1,'腾讯云SecretId'),(74,'tencentSecretKey','TENCENT_SECRET_KEY_PLACEHOLDER',1,'腾讯云SecretKey'),(75,'tencentSmsRegion','ap-beijing',1,'腾讯云短信地域'),(76,'tencentSmsSdkAppId','1400111253',1,'腾讯云短信SdkAppId'),(77,'tencentSmsSignName','林风科技',1,'腾讯云短信签名'),(78,'tencentSmsTemplateId','2011139',1,'腾讯云短信模板ID'),(79,'chooseSms','0',1,'选择的短信厂商0阿里1腾讯'),(80,'socialBtn','0',1,'好友聊天模块0开启1关闭'),(81,'defaultHead','https://pic.linfeng.tech/test/20220825/794e4d232bef4dcdac96497f9f487b48.jpeg',1,'用户默认注册头像'),(82,'baiduAppId','99999713',1,'百度智能云APPID'),(83,'baiduApiKey','BAIDU_API_KEY_PLACEHOLDER',1,'百度智能云ApiKey'),(84,'baiduSecretKey','BAIDU_SECRET_KEY_PLACEHOLDER',1,'百度智能云SecretKey'),(85,'baiduCensorTextOpen','0',1,'百度文本审核0关闭1开启'),(86,'baiduCensorImageOpen','0',1,'百度图片审核0关闭1开启'),(87,'baiduCensorVideoOpen','0',1,'百度视频审核0关闭1开启'),(88,'yuetoint','0',1,'余额兑换积分的开关 0开启 1关闭'),(89,'yuetointratio','100',1,'一块钱能兑换的积分数'),(90,'storageMethod','1',1,'0云存储1本地存储'),(91,'localStorageUrl','http://localhost:8080/',1,'本地存储后端接口路径'),(92,'standard','这是平台规范协议',1,'平台规范协议');

/*Table structure for table `sys_log` */

DROP TABLE IF EXISTS `sys_log`;

CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) DEFAULT NULL COMMENT '请求参数',
  `time` bigint(20) NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `address` varchar(50) DEFAULT '' COMMENT '地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COMMENT='系统日志';

/*Data for the table `sys_log` */

/*Table structure for table `sys_menu` */

DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int(11) DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=182 DEFAULT CHARSET=utf8mb4 COMMENT='菜单管理';

/*Data for the table `sys_menu` */

insert  into `sys_menu`(`menu_id`,`parent_id`,`name`,`url`,`perms`,`type`,`icon`,`order_num`) values (1,0,'系统管理',NULL,NULL,0,'Tools',0),(2,1,'管理员列表','sys/user',NULL,1,'Stamp',1),(3,1,'角色管理','sys/role',NULL,1,'Avatar',2),(4,1,'菜单管理','sys/menu',NULL,1,'menu',3),(5,1,'SQL监控','https://wxapi.linfeng.tech/druid/sql.html',NULL,1,'SetUp',4),(6,1,'定时任务','job/schedule',NULL,1,'Cloudy',5),(7,6,'查看',NULL,'sys:schedule:list,sys:schedule:info',2,NULL,0),(8,6,'新增',NULL,'sys:schedule:save',2,NULL,0),(9,6,'修改',NULL,'sys:schedule:update',2,NULL,0),(10,6,'删除',NULL,'sys:schedule:delete',2,NULL,0),(11,6,'暂停',NULL,'sys:schedule:pause',2,NULL,0),(12,6,'恢复',NULL,'sys:schedule:resume',2,NULL,0),(13,6,'立即执行',NULL,'sys:schedule:run',2,NULL,0),(14,6,'日志列表',NULL,'sys:schedule:log',2,NULL,0),(15,2,'查看',NULL,'sys:user:list,sys:user:info',2,NULL,0),(16,2,'新增',NULL,'sys:user:save,sys:role:select',2,NULL,0),(17,2,'修改',NULL,'sys:user:update,sys:role:select',2,NULL,0),(18,2,'删除',NULL,'sys:user:delete',2,NULL,0),(19,3,'查看',NULL,'sys:role:list,sys:role:info',2,NULL,0),(20,3,'新增',NULL,'sys:role:save,sys:menu:list',2,NULL,0),(21,3,'修改',NULL,'sys:role:update,sys:menu:list',2,NULL,0),(22,3,'删除',NULL,'sys:role:delete',2,NULL,0),(23,4,'查看',NULL,'sys:menu:list,sys:menu:info',2,NULL,0),(24,4,'新增',NULL,'sys:menu:save,sys:menu:select',2,NULL,0),(25,4,'修改',NULL,'sys:menu:update,sys:menu:select',2,NULL,0),(26,4,'删除',NULL,'sys:menu:delete',2,NULL,0),(27,1,'参数管理','sys/config','sys:config:list,sys:config:info,sys:config:save,sys:config:update,sys:config:delete',1,'Setting',6),(29,1,'系统日志','sys/log','sys:log:list',1,'Tickets',7),(30,42,'文件上传','oss/oss','sys:oss:all',1,'Link',6),(36,0,'动态管理','','',0,'ChatLineSquare',1),(37,42,'配置中心','admin/system',NULL,1,'StarFilled',6),(38,37,'查看',NULL,'admin:system:list,admin:system:info',2,NULL,6),(39,37,'新增',NULL,'admin:system:save',2,NULL,6),(40,37,'修改',NULL,'admin:system:update',2,NULL,6),(41,37,'删除',NULL,'admin:system:delete',2,NULL,6),(42,0,'应用配置','','',0,'CopyDocument',2),(43,88,'会员管理','admin/user',NULL,1,'Avatar',6),(44,43,'查看',NULL,'admin:user:list,admin:user:info',2,NULL,6),(45,43,'新增',NULL,'admin:user:save',2,NULL,6),(46,43,'修改',NULL,'admin:user:update',2,NULL,6),(47,43,'删除',NULL,'admin:user:delete',2,NULL,6),(48,89,'分类管理','admin/category',NULL,1,'Grid',6),(49,48,'查看',NULL,'admin:category:list,admin:category:info',2,NULL,6),(50,48,'新增',NULL,'admin:category:save',2,NULL,6),(51,48,'修改',NULL,'admin:category:update',2,NULL,6),(53,89,'圈子管理','admin/topic',NULL,1,'HelpFilled',6),(54,53,'查看',NULL,'admin:topic:list,admin:topic:info',2,NULL,6),(55,53,'新增',NULL,'admin:topic:save',2,NULL,6),(56,53,'修改',NULL,'admin:topic:update',2,NULL,6),(57,53,'删除',NULL,'admin:topic:delete',2,NULL,6),(58,36,'帖子管理','admin/post',NULL,1,'DocumentChecked',6),(59,58,'查看',NULL,'admin:post:list,admin:post:info',2,NULL,6),(60,58,'新增',NULL,'admin:post:save',2,NULL,6),(61,58,'修改',NULL,'admin:post:update',2,NULL,6),(62,58,'删除',NULL,'admin:post:delete',2,NULL,6),(63,89,'话题管理','admin/discuss',NULL,1,'menu',6),(64,63,'查看',NULL,'admin:discuss:list,admin:discuss:info',2,NULL,6),(65,63,'新增',NULL,'admin:discuss:save',2,NULL,6),(66,63,'修改',NULL,'admin:discuss:update',2,NULL,6),(67,63,'删除',NULL,'admin:discuss:delete',2,NULL,6),(68,36,'评论管理','admin/comment',NULL,1,'ChatDotRound',6),(69,68,'查看',NULL,'admin:comment:list,admin:comment:info',2,NULL,6),(70,68,'新增',NULL,'admin:comment:save',2,NULL,6),(71,68,'修改',NULL,'admin:comment:update',2,NULL,6),(72,68,'删除',NULL,'admin:comment:delete',2,NULL,6),(73,89,'消息管理','admin/message',NULL,1,'ChatLineRound',6),(74,73,'查看',NULL,'admin:message:list,admin:message:info',2,NULL,6),(75,73,'新增',NULL,'admin:message:save',2,NULL,6),(76,73,'修改',NULL,'admin:message:update',2,NULL,6),(77,73,'删除',NULL,'admin:message:delete',2,NULL,6),(78,155,'轮播图管理','admin/link',NULL,1,'Edit',6),(79,78,'查看',NULL,'admin:link:list,admin:link:info',2,NULL,6),(80,78,'新增',NULL,'admin:link:save',2,NULL,6),(81,78,'修改',NULL,'admin:link:update',2,NULL,6),(82,78,'删除',NULL,'admin:link:delete',2,NULL,6),(83,89,'敏感词库','admin/sensitive',NULL,1,'CircleClose',6),(84,83,'查看',NULL,'admin:sensitive:list,admin:sensitive:info',2,NULL,6),(85,83,'新增',NULL,'admin:sensitive:save',2,NULL,6),(86,83,'修改',NULL,'admin:sensitive:update',2,NULL,6),(87,83,'删除',NULL,'admin:sensitive:delete',2,NULL,6),(88,0,'用户管理','','',0,'UserFilled',3),(89,0,'内容管理','','',0,'Postcard',4),(90,105,'充值记录','admin/userrecharge',NULL,1,'IceCream',6),(91,90,'查看',NULL,'admin:userrecharge:list,admin:userrecharge:info',2,NULL,6),(92,90,'新增',NULL,'admin:userrecharge:save',2,NULL,6),(93,90,'修改',NULL,'admin:userrecharge:update',2,NULL,6),(94,90,'删除',NULL,'admin:userrecharge:delete',2,NULL,6),(95,105,'用户账单','admin/bill',NULL,1,'Money',6),(96,95,'查看',NULL,'admin:bill:list,admin:bill:info',2,NULL,6),(97,95,'新增',NULL,'admin:bill:save',2,NULL,6),(98,95,'修改',NULL,'admin:bill:update',2,NULL,6),(99,95,'删除',NULL,'admin:bill:delete',2,NULL,6),(100,105,'充值方案','admin/recharge',NULL,1,'Operation',6),(101,100,'查看',NULL,'admin:recharge:list,admin:recharge:info',2,NULL,6),(102,100,'新增',NULL,'admin:recharge:save',2,NULL,6),(103,100,'修改',NULL,'admin:recharge:update',2,NULL,6),(104,100,'删除',NULL,'admin:recharge:delete',2,NULL,6),(105,0,'充值中心','','',0,'Coin',6),(106,116,'签到记录','admin/usersign',NULL,1,'Checked',6),(107,106,'查看',NULL,'admin:usersign:list,admin:usersign:info',2,NULL,6),(108,106,'新增',NULL,'admin:usersign:save',2,NULL,6),(109,106,'修改',NULL,'admin:usersign:update',2,NULL,6),(110,106,'删除',NULL,'admin:usersign:delete',2,NULL,6),(111,116,'签到配置','admin/signconfig',NULL,1,'CoffeeCup',6),(112,111,'查看',NULL,'admin:signconfig:list,admin:signconfig:info',2,NULL,6),(113,111,'新增',NULL,'admin:signconfig:save',2,NULL,6),(114,111,'修改',NULL,'admin:signconfig:update',2,NULL,6),(115,111,'删除',NULL,'admin:signconfig:delete',2,NULL,6),(116,0,'签到管理','','',0,'Cellphone',7),(117,155,'用户菜单设置','admin/usermenu',NULL,1,'menu',6),(118,117,'查看',NULL,'admin:usermenu:list,admin:usermenu:info',2,NULL,6),(119,117,'新增',NULL,'admin:usermenu:save',2,NULL,6),(120,117,'修改',NULL,'admin:usermenu:update',2,NULL,6),(121,117,'删除',NULL,'admin:usermenu:delete',2,NULL,6),(122,132,'抽奖记录','admin/luckdrawrecord',NULL,1,'menu',6),(123,122,'查看',NULL,'admin:luckdrawrecord:list,admin:luckdrawrecord:info',2,NULL,6),(124,122,'新增',NULL,'admin:luckdrawrecord:save',2,NULL,6),(125,122,'修改',NULL,'admin:luckdrawrecord:update',2,NULL,6),(126,122,'删除',NULL,'admin:luckdrawrecord:delete',2,NULL,6),(127,132,'奖品设置','admin/luckdraw',NULL,1,'Setting',6),(128,127,'查看',NULL,'admin:luckdraw:list,admin:luckdraw:info',2,NULL,6),(129,127,'新增',NULL,'admin:luckdraw:save',2,NULL,6),(130,127,'修改',NULL,'admin:luckdraw:update',2,NULL,6),(131,127,'删除',NULL,'admin:luckdraw:delete',2,NULL,6),(132,0,'抽奖管理','','',0,'MagicStick',8),(133,138,'用户举报','admin/report',NULL,1,'MostlyCloudy',6),(134,133,'查看',NULL,'admin:report:list,admin:report:info',2,NULL,6),(135,133,'新增',NULL,'admin:report:save',2,NULL,6),(136,133,'修改',NULL,'admin:report:update',2,NULL,6),(137,133,'删除',NULL,'admin:report:delete',2,NULL,6),(138,0,'举报管理','','',0,'Postcard',9),(139,88,'会员充值设置','admin/vipoption',NULL,1,'Pointer',6),(140,139,'查看',NULL,'admin:vipoption:list,admin:vipoption:info',2,NULL,6),(141,139,'新增',NULL,'admin:vipoption:save',2,NULL,6),(142,139,'修改',NULL,'admin:vipoption:update',2,NULL,6),(143,139,'删除',NULL,'admin:vipoption:delete',2,NULL,6),(144,48,'删除',NULL,'admin:category:delete',2,NULL,6),(145,156,'提现管理','admin/cashout',NULL,1,'TakeawayBox',6),(146,145,'查看',NULL,'admin:cashout:list,admin:cashout:info',2,NULL,6),(147,145,'新增',NULL,'admin:cashout:save',2,NULL,6),(148,145,'修改',NULL,'admin:cashout:update',2,NULL,6),(149,145,'删除',NULL,'admin:cashout:delete',2,NULL,6),(150,155,'导航栏管理','admin/navigation',NULL,1,'CollectionTag',6),(151,150,'查看',NULL,'admin:navigation:list,admin:navigation:info',2,NULL,6),(152,150,'新增',NULL,'admin:navigation:save',2,NULL,6),(153,150,'修改',NULL,'admin:navigation:update',2,NULL,6),(154,150,'删除',NULL,'admin:navigation:delete',2,NULL,6),(155,0,'用户端设置','','',0,'EditPen',5),(156,0,'提现管理','','',0,'Reading',10),(157,88,'用户经验设置','admin/userlevel',NULL,1,'Wallet',6),(158,157,'查看',NULL,'admin:userlevel:list,admin:userlevel:info',2,NULL,6),(159,157,'新增',NULL,'admin:userlevel:save',2,NULL,6),(160,157,'修改',NULL,'admin:userlevel:update',2,NULL,6),(161,157,'删除',NULL,'admin:userlevel:delete',2,NULL,6),(167,88,'会员权益管理','admin/vipbenefit',NULL,1,'Checked',6),(168,167,'查看',NULL,'admin:vipbenefit:list,admin:vipbenefit:info',2,NULL,6),(169,167,'新增',NULL,'admin:vipbenefit:save',2,NULL,6),(170,167,'修改',NULL,'admin:vipbenefit:update',2,NULL,6),(171,167,'删除',NULL,'admin:vipbenefit:delete',2,NULL,6),(172,1,'访客统计','admin/activeuser',NULL,1,'Setting',8),(173,172,'查看',NULL,'admin:activeuser:list,admin:activeuser:info',2,NULL,6),(174,172,'新增',NULL,'admin:activeuser:save',2,NULL,6),(175,172,'修改',NULL,'admin:activeuser:update',2,NULL,6),(176,172,'删除',NULL,'admin:activeuser:delete',2,NULL,6),(177,42,'APP版本更新','admin/appversion',NULL,1,'Refresh',6),(178,177,'查看',NULL,'admin:appversion:list,admin:appversion:info',2,NULL,6),(179,177,'新增',NULL,'admin:appversion:save',2,NULL,6),(180,177,'修改',NULL,'admin:appversion:update',2,NULL,6),(181,177,'删除',NULL,'admin:appversion:delete',2,NULL,6);

/*Table structure for table `sys_oss` */

DROP TABLE IF EXISTS `sys_oss`;

CREATE TABLE `sys_oss` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(200) DEFAULT NULL COMMENT 'URL地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COMMENT='文件上传';

/*Data for the table `sys_oss` */

insert  into `sys_oss`(`id`,`url`,`create_date`) values (17,'http://localhost:8080/resource/image/2025-07-28/48c44f1c-9efc-469d-807c-c058b759bc69.jpg','2025-07-28 10:30:09');

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='角色';

/*Data for the table `sys_role` */

insert  into `sys_role`(`role_id`,`role_name`,`remark`,`create_user_id`,`create_time`) values (1,'运营','运营人员',1,'2025-06-01 13:35:45');

/*Table structure for table `sys_role_menu` */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单对应关系';

/*Data for the table `sys_role_menu` */

insert  into `sys_role_menu`(`id`,`role_id`,`menu_id`) values (76,1,36),(77,1,58),(78,1,59),(79,1,60),(80,1,61),(81,1,62),(82,1,68),(83,1,69),(84,1,70),(85,1,71),(86,1,72),(87,1,42),(88,1,30),(89,1,37),(90,1,38),(91,1,39),(92,1,40),(93,1,41),(94,1,88),(95,1,43),(96,1,44),(97,1,45),(98,1,46),(99,1,47),(100,1,139),(101,1,140),(102,1,141),(103,1,142),(104,1,143),(105,1,-666666);

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `salt` varchar(20) DEFAULT NULL COMMENT '盐',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) DEFAULT NULL COMMENT '手机号',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`username`,`password`,`salt`,`email`,`mobile`,`status`,`create_user_id`,`create_time`) values (1,'admin','cdac762d0ba79875489f6a8b430fa8b5dfe0cdd81da38b80f02f33328af7fd4a','YzcmCZNvbXocrsz9dm8e','root@163.com','18100900090',1,1,'2025-06-01 13:36:24'),(2,'yunying','5ad9030f5f5e49ed06e6dfb44f0da6f9a95cf9f68276207c1277c05d31bc5fc6','hXie4Fbhak1N6pNOtIoo','yunying@163.com','15669231234',1,1,'2025-06-01 13:36:24');

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色对应关系';

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`id`,`user_id`,`role_id`) values (1,2,1);

/*Table structure for table `sys_user_token` */

DROP TABLE IF EXISTS `sys_user_token`;

CREATE TABLE `sys_user_token` (
  `user_id` bigint(20) NOT NULL,
  `token` varchar(100) NOT NULL COMMENT 'token',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户Token';

/*Data for the table `sys_user_token` */

insert  into `sys_user_token`(`user_id`,`token`,`expire_time`,`update_time`) values (1,'8bcc59b8ad37703312f61734e25d3dc9','2025-07-28 21:59:34','2025-07-28 09:59:34');

insert  into `sys_config`(`id`,`param_key`,`param_value`,`status`,`remark`) values (93,'loginType','0',1,'小程序登录策略 0仅openid登录1手机号登录');
insert  into `sys_config`(`id`,`param_key`,`param_value`,`status`,`remark`) values (94,'loginInfoPop','0',1,'小程序登录是否要求填写头像昵称0否1是');

-- 菜单SQL
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    VALUES ('1', '私聊管理', 'admin/chatmessage', NULL, '1', 'Setting', '6');

-- 按钮父菜单ID
set @parentId = @@identity;

-- 菜单对应按钮SQL
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '查看', null, 'admin:chatmessage:list,admin:chatmessage:info', '2', null, '6';
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '新增', null, 'admin:chatmessage:save', '2', null, '6';
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '修改', null, 'admin:chatmessage:update', '2', null, '6';
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '删除', null, 'admin:chatmessage:delete', '2', null, '6';


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
