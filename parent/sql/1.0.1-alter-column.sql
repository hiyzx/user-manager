ALTER TABLE `user` ADD COLUMN `last_login_time` datetime COMMENT '最后登陆时间' AFTER headImg;

ALTER TABLE `user` CHANGE COLUMN `headImg` `head_img` varchar(512);