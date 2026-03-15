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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='APP版本信息表';


-- 菜单SQL
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    VALUES ('1', 'APP版本更新', 'admin/appversion', NULL, '1', 'Setting', '6');

-- 按钮父菜单ID
SET @parentId = @@identity;

-- 菜单对应按钮SQL
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '查看', NULL, 'admin:appversion:list,admin:appversion:info', '2', NULL, '6';
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '新增', NULL, 'admin:appversion:save', '2', NULL, '6';
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '修改', NULL, 'admin:appversion:update', '2', NULL, '6';
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
    SELECT @parentId, '删除', NULL, 'admin:appversion:delete', '2', NULL, '6';
	
	
ALTER TABLE `sys_config` MODIFY COLUMN `param_value` VARCHAR(5000) DEFAULT NULL COMMENT 'value';


insert  into `sys_config`(`id`,`param_key`,`param_value`,`status`,`remark`) values (92,'standard','这是平台规范协议',1,'平台规范协议');