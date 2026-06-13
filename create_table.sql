CREATE TABLE IF NOT EXISTS `sys_register_apply` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL COMMENT '学号/工号',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `role` TINYINT NOT NULL COMMENT '角色: 0-学校管理员, 1-学院管理员, 2-辅导员, 3-学生',
    `gender` TINYINT COMMENT '性别: 1-男, 2-女',
    `grade` INT COMMENT '年级，如 2020',
    `college_id` BIGINT COMMENT '所属学院ID',
    `college_name` VARCHAR(100) COMMENT '所属学院名称',
    `class_name` VARCHAR(100) COMMENT '班级名称',
    `id_card` VARCHAR(20) COMMENT '身份证号',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待审核, 1-已通过, 2-已驳回',
    `reject_reason` VARCHAR(255) COMMENT '驳回理由',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_username` (`username`)
) COMMENT '用户注册申请表';