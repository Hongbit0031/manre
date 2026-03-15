ALTER TABLE `lf_post` ADD INDEX `idx_is_private` (`is_private`);
ALTER TABLE `lf_post` ADD INDEX `idx_type` (`type`);
ALTER TABLE `lf_topic` ADD INDEX `idx_lt_uid` (`uid`);
ALTER TABLE `lf_topic` ADD INDEX `idx_lt_is_privacy` (`is_privacy`);
ALTER TABLE `lf_topic` ADD INDEX `idx_lt_cate_id` (`cate_id`);
ALTER TABLE `lf_topic` ADD INDEX `idx_lt_status` (`status`);
ALTER TABLE `lf_topic` ADD INDEX `idx_lt_rest` (`rest`);

ALTER TABLE lf_user ADD  `bg_img` varchar(255) DEFAULT NULL COMMENT '背景图';
