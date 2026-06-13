DROP DATABASE IF EXISTS `winter_clothing_subsidy`;
CREATE DATABASE `winter_clothing_subsidy` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `winter_clothing_subsidy`;

-- 1. 用户表 (包含学校、学院、辅导员、学生)
CREATE TABLE `sys_user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL COMMENT '学号/工号',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(学生默认为身份证后六位)',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `role` TINYINT NOT NULL COMMENT '角色: 0-学校管理员, 1-学院管理员, 2-辅导员, 3-学生',
    `gender` TINYINT COMMENT '性别: 1-男, 2-女 (学生选衣服限制)',
    `grade` INT COMMENT '年级，如 2020',
    `college_id` BIGINT COMMENT '所属学院ID',
    `college_name` VARCHAR(100) COMMENT '所属学院名称',
    `class_name` VARCHAR(100) COMMENT '班级名称',
    `id_card` VARCHAR(20) COMMENT '身份证号',
    `wechat_openid` VARCHAR(100) COMMENT '绑定的微信OpenID',
    `is_difficulty` TINYINT(1) DEFAULT 0 COMMENT '是否在困难认定库',
    `difficulty_level` VARCHAR(20) COMMENT '困难等级',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_username` (`username`)
) COMMENT '系统用户表';

-- 2. 白名单表 (控制智慧学工登录)
CREATE TABLE `sys_whitelist` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL COMMENT '允许登录的工号',
    `operator_id` BIGINT COMMENT '操作人ID(如9447的ID)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '智慧学工登录白名单';

-- 3. 批次表
CREATE TABLE `subsidy_batch` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `batch_name` VARCHAR(100) NOT NULL COMMENT '批次名称，如: 2020学年寒衣补助',
    `difficulty_year` VARCHAR(20) NOT NULL COMMENT '困难认定学年',
    `difficulty_level` VARCHAR(50) NOT NULL COMMENT '困难等级要求',
    `freshman_grade` INT NOT NULL COMMENT '新生年级(如2020)，该年级申请理由非必填',
    `apply_start_time` DATETIME NOT NULL COMMENT '申请开始时间',
    `apply_end_time` DATETIME NOT NULL COMMENT '申请截止时间',
    `select_start_time` DATETIME NOT NULL COMMENT '选款开始时间',
    `select_end_time` DATETIME NOT NULL COMMENT '选款截止时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-关闭, 1-开启',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '补助批次表';

-- 4. 衣服款式表
CREATE TABLE `clothing_style` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `batch_id` BIGINT NOT NULL COMMENT '所属批次ID',
    `name` VARCHAR(100) NOT NULL COMMENT '款式名称(如: 男01款)',
    `gender_limit` TINYINT NOT NULL COMMENT '性别限制: 1-男, 2-女',
    `image_url` VARCHAR(255) COMMENT '款式图片(MinIO URL)',
    `size_table_url` VARCHAR(255) COMMENT '尺码推荐表(MinIO URL)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '衣服款式表';

-- 5. 衣服SKU(尺码)表
CREATE TABLE `clothing_sku` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `style_id` BIGINT NOT NULL COMMENT '款式ID',
    `size_name` VARCHAR(20) NOT NULL COMMENT '尺码(如: XL)',
    `product_code` VARCHAR(50) NOT NULL COMMENT '商品编码(如: 男01款XL码)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '衣服SKU表';

-- 6. 申请与审核记录表
CREATE TABLE `subsidy_apply` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `batch_id` BIGINT NOT NULL COMMENT '批次ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `apply_reason` TEXT COMMENT '申请理由(老生必填)',
    
    `counselor_status` TINYINT DEFAULT 0 COMMENT '辅导员审核: 0-待审核, 1-通过, 2-不通过',
    `counselor_reason` VARCHAR(255) COMMENT '辅导员拒绝理由',
    `counselor_submit` TINYINT(1) DEFAULT 0 COMMENT '辅导员是否已提交至学院: 0-否, 1-是',
    
    `college_status` TINYINT DEFAULT 0 COMMENT '学院审核: 0-待审核, 1-通过, 2-不通过',
    `college_reason` VARCHAR(255) COMMENT '学院拒绝理由',
    `college_submit` TINYINT(1) DEFAULT 0 COMMENT '学院是否已提交至学校: 0-否, 1-是',
    
    `school_status` TINYINT DEFAULT 0 COMMENT '学校审核: 0-待审核, 1-通过, 2-不通过',
    `school_reason` VARCHAR(255) COMMENT '学校拒绝理由',
    
    `final_status` TINYINT DEFAULT 0 COMMENT '最终状态: 0-审核中, 1-审核通过, 2-审核拒绝',
    
    `clothing_sku_id` BIGINT COMMENT '最终选择的衣服SKU ID',
    `select_time` DATETIME COMMENT '选款时间',
    
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_batch_student` (`batch_id`, `student_id`)
) COMMENT '申请与审核记录表';

-- 7. 注册申请表
CREATE TABLE `sys_register_apply` (
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

-- =======================================================
-- MOCK 数据初始化
-- =======================================================

-- 用户数据
INSERT INTO `sys_user` (`username`, `password`, `name`, `role`, `gender`, `grade`, `college_id`, `college_name`, `class_name`, `id_card`, `is_difficulty`, `difficulty_level`) VALUES 
('9447', 'sch123456', '白名单管理员', 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL),
('school', 'sch123456', '学校资助中心', 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL),
('college', 'sch123456', '计科学院管理员', 1, NULL, NULL, 1, '计算机学院', NULL, NULL, 0, NULL),
('teacher', 'sch123456', '张辅导员', 2, NULL, NULL, 1, '计算机学院', '计科2001班', NULL, 0, NULL),
('student', 'sch123456', '李新生(男)', 3, 1, 2020, 1, '计算机学院', '计科2001班', '110105200201011234', 1, '特别困难'),
('school01', '123456', '学校资助中心', 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL),
('college_jsj', '123456', '计科学院管理员', 1, NULL, NULL, 1, '计算机学院', NULL, NULL, 0, NULL),
('counselor01', '123456', '张辅导员', 2, NULL, NULL, 1, '计算机学院', '计科2001班', NULL, 0, NULL),
('2020001', '101234', '李新生(男)', 3, 1, 2020, 1, '计算机学院', '计科2001班', '110105200201011234', 1, '特别困难'),
('2020002', '101235', '王新生(女)', 3, 2, 2020, 1, '计算机学院', '计科2001班', '110105200201011235', 1, '一般困难'),
('2019001', '011236', '赵老生(男)', 3, 1, 2019, 1, '计算机学院', '计科1901班', '110105200101011236', 1, '特别困难'),
('2019002', '011237', '刘老生(女-非困难)', 3, 2, 2019, 1, '计算机学院', '计科1901班', '110105200101011237', 0, NULL);

-- 白名单
INSERT INTO `sys_whitelist` (`username`, `operator_id`) VALUES 
('school01', 1),
('school', 1),
('college', 1),
('teacher', 1),
('student', 1);

-- 批次
INSERT INTO `subsidy_batch` (`batch_name`, `difficulty_year`, `difficulty_level`, `freshman_grade`, `apply_start_time`, `apply_end_time`, `select_start_time`, `select_end_time`) VALUES 
('2020学年寒衣补助批次', '2020-2021', '特别困难,一般困难', 2020, '2020-11-01 00:00:00', '2026-11-15 23:59:59', '2020-11-20 00:00:00', '2026-11-30 23:59:59');

-- 款式
INSERT INTO `clothing_style` (`id`, `batch_id`, `name`, `gender_limit`, `image_url`, `size_table_url`) VALUES 
(1, 1, '男01款羽绒服', 1, 'http://minio:9000/clothes/man01.jpg', 'http://minio:9000/clothes/man01_size.jpg'),
(2, 1, '女01款羽绒服', 2, 'http://minio:9000/clothes/woman01.jpg', 'http://minio:9000/clothes/woman01_size.jpg');

-- SKU
INSERT INTO `clothing_sku` (`style_id`, `size_name`, `product_code`) VALUES 
(1, 'L', '男01款L码'),
(1, 'XL', '男01款XL码'),
(2, 'M', '女01款M码'),
(2, 'L', '女01款L码');

-- 申请记录 (MOCK: 李新生已经申请并被辅导员审核通过，提交到了学院待审；赵老生刚提交申请待辅导员审核)
INSERT INTO `subsidy_apply` (`batch_id`, `student_id`, `apply_reason`, `counselor_status`, `counselor_submit`, `college_status`, `college_submit`, `school_status`, `final_status`) VALUES 
(1, 5, NULL, 1, 1, 0, 0, 0, 0),
(1, 7, '家庭变故，急需冬衣', 0, 0, 0, 0, 0, 0);