
ALTER TABLE sys_log ADD `address` varchar(50) DEFAULT '' COMMENT '地址';


insert  into `sys_config`(`id`,`param_key`,`param_value`,`status`,`remark`) values (80,'socialBtn','0',1,'好友聊天模块0开启1关闭');
insert  into `schedule_job`(`job_id`,`bean_name`,`params`,`cron_expression`,`status`,`remark`,`create_time`) values (6,'activeUserRecordTask','7','0 15 3 ? * *',1,'访客记录数据清理定时任务，参数表示清理n天前的所有访客记录','2024-05-30 11:57:54');

-- 菜单SQL
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    VALUES ('1', '访客统计', 'admin/activeuser', NULL, '1', 'Setting', '6');

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

-- 按钮父菜单ID
SET @parentId = @@identity;

-- 菜单对应按钮SQL
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '查看', NULL, 'admin:activeuser:list,admin:activeuser:info', '2', NULL, '6';
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '新增', NULL, 'admin:activeuser:save', '2', NULL, '6';
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '修改', NULL, 'admin:activeuser:update', '2', NULL, '6';
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '删除', NULL, 'admin:activeuser:delete', '2', NULL, '6';
	
	
	