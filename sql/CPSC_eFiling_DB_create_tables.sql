-- CPSC eFiling MySQL Database & Tables
-- 直接执行本文件即可创建数据库和全部业务表
-- Database: CPSC_eFiling_DB
-- Charset: utf8mb4
-- Engine: InnoDB

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `CPSC_eFiling_DB`
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE `CPSC_eFiling_DB`;

-- =========================================================
-- 1. 制造商档案表
-- 对应 Excel：02_制造商信息
-- 对应 JSON：coreProduct.manufacturer + directives.manufacturer
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_manufacturer` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `certifier_id` VARCHAR(100) NOT NULL COMMENT 'CPSC Certifier ID / 企业账户ID',

    `gln` VARCHAR(50) NULL COMMENT 'Global Location Number，可选',
    `alternate_id` VARCHAR(50) NOT NULL COMMENT '制造商编号，对应 manufacturer.alternateId',
    `sbm_id` VARCHAR(50) NULL COMMENT 'Small Batch Manufacturer ID，可选',

    `name` VARCHAR(100) NOT NULL COMMENT '制造商名称',
    `address_line1` VARCHAR(100) NOT NULL COMMENT '地址1',
    `address_line2` VARCHAR(100) NULL COMMENT '地址2',
    `apt_number` VARCHAR(10) NULL COMMENT '房间/套间号',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `state_province` VARCHAR(100) NULL COMMENT '州/省',
    `country` VARCHAR(50) NOT NULL COMMENT '国家，建议填写英文，如 Japan',
    `postal_code` VARCHAR(10) NOT NULL COMMENT '邮编',
    `phone` VARCHAR(20) NOT NULL COMMENT '电话',
    `email` VARCHAR(50) NOT NULL COMMENT '邮箱',

    `is_new` CHAR(1) NOT NULL DEFAULT 'Y' COMMENT 'directives.manufacturer.isNew：Y=新建，N=已存在',

    `active_flag` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：1=启用，0=停用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY `uk_CPSC_eFiling_mfg_alt_id` (`certifier_id`, `alternate_id`),
    KEY `idx_CPSC_eFiling_mfg_gln` (`gln`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling 制造商档案表';


-- =========================================================
-- 2. POC 联系人档案表
-- 对应 Excel：04_联系人POC
-- 对应 JSON：coreProduct.poc + directives.poc
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_poc` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `certifier_id` VARCHAR(100) NOT NULL COMMENT 'CPSC Certifier ID / 企业账户ID',

    `poc_code` VARCHAR(50) NOT NULL COMMENT '内部POC编号，用于Excel匹配',
    `type` VARCHAR(30) NOT NULL DEFAULT 'Importer' COMMENT 'POC类型：Importer / Manufacturer / Laboratory / Broker / Other',

    `gln` VARCHAR(50) NULL COMMENT 'Global Location Number，可选',
    `alternate_id` VARCHAR(50) NULL COMMENT 'POC alternateId',
    `name` VARCHAR(100) NULL COMMENT 'POC名称',
    `address_line1` VARCHAR(100) NULL COMMENT '地址1，type=Other时建议填写',
    `address_line2` VARCHAR(100) NULL COMMENT '地址2',
    `apt_number` VARCHAR(10) NULL COMMENT '房间/套间号',
    `city` VARCHAR(50) NULL COMMENT '城市',
    `state_province` VARCHAR(100) NULL COMMENT '州/省',
    `country` VARCHAR(50) NULL COMMENT '国家',
    `postal_code` VARCHAR(10) NULL COMMENT '邮编',
    `phone` VARCHAR(20) NULL COMMENT '电话',
    `email` VARCHAR(50) NULL COMMENT '邮箱',

    `is_new` CHAR(1) NOT NULL DEFAULT 'N' COMMENT 'directives.poc.isNew：Y=新建，N=已存在',

    `active_flag` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：1=启用，0=停用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY `uk_CPSC_eFiling_poc_code` (`certifier_id`, `poc_code`),
    KEY `idx_CPSC_eFiling_poc_alt_id` (`alternate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling POC联系人档案表';


-- =========================================================
-- 3. 实验室档案表
-- 对应 Excel：03_实验室与测试报告中的实验室基础信息
-- 对应 JSON：coreProduct.labs[] 中的实验室资料 + directives.labs[]
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_lab` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `certifier_id` VARCHAR(100) NOT NULL COMMENT 'CPSC Certifier ID / 企业账户ID',

    `type` VARCHAR(10) NOT NULL DEFAULT 'LAB' COMMENT '实验室类型：ITL 或 LAB',
    `cpsc_id` VARCHAR(20) NULL COMMENT 'CPSC ID，type=ITL时使用',
    `gln` VARCHAR(50) NULL COMMENT 'Global Location Number，可选',
    `alternate_id` VARCHAR(50) NOT NULL COMMENT '实验室编号，对应 labs[].alternateId',

    `name` VARCHAR(100) NOT NULL COMMENT '实验室名称',
    `address_line1` VARCHAR(100) NOT NULL COMMENT '地址1',
    `address_line2` VARCHAR(100) NULL COMMENT '地址2',
    `apt_number` VARCHAR(10) NULL COMMENT '房间/套间号',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `state_province` VARCHAR(100) NULL COMMENT '州/省',
    `country` VARCHAR(50) NOT NULL COMMENT '国家，建议填写英文',
    `postal_code` VARCHAR(10) NOT NULL COMMENT '邮编',
    `phone` VARCHAR(20) NOT NULL COMMENT '电话',
    `email` VARCHAR(50) NOT NULL COMMENT '邮箱',

    `is_new` CHAR(1) NOT NULL DEFAULT 'N' COMMENT 'directives.labs[].isNew：Y=新建，N=已存在',

    `active_flag` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：1=启用，0=停用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY `uk_CPSC_eFiling_lab_alt_id` (`certifier_id`, `alternate_id`),
    KEY `idx_CPSC_eFiling_lab_cpsc_id` (`cpsc_id`),
    KEY `idx_CPSC_eFiling_lab_gln` (`gln`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling 实验室档案表';


-- =========================================================
-- 4. 产品证书主表
-- 对应 Excel：01_产品基本信息
-- 对应 JSON：coreProduct 主体字段 + directives产品层字段
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_product_certificate` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `certifier_id` VARCHAR(100) NOT NULL COMMENT 'CPSC Certifier ID / 企业账户ID',
    `collection_id` VARCHAR(100) NOT NULL COMMENT 'CPSC Product Collection ID',

    `version_id` VARCHAR(19) NOT NULL DEFAULT 'V1' COMMENT '证书版本号，对应 coreProduct.versionId',
    `primary_product_id` VARCHAR(19) NOT NULL COMMENT '产品唯一ID，对应 coreProduct.primaryProductId',
    `primary_product_id_type` VARCHAR(30) NOT NULL DEFAULT 'Model #' COMMENT '产品ID类型：GTIN / UPC / SKU / Model # / Serial # / Registered # / Alternate ID',
    `certificate_type` VARCHAR(10) NOT NULL DEFAULT 'GCC' COMMENT '证书类型：GCC 或 CPC',

    `name` VARCHAR(250) NOT NULL COMMENT '产品名称',
    `trade_brand_name` VARCHAR(50) NULL COMMENT '品牌名称',
    `description` VARCHAR(250) NULL COMMENT '产品描述',
    `color` VARCHAR(50) NULL COMMENT '产品颜色',
    `style` VARCHAR(50) NULL COMMENT '产品款式',

    `manufacture_date` VARCHAR(10) NOT NULL COMMENT '生产年月，API格式 MM/YYYY，如 06/2026',
    `production_start_date` VARCHAR(10) NULL COMMENT '生产开始日期，API格式 MM/DD/YYYY',
    `production_end_date` VARCHAR(10) NULL COMMENT '生产结束日期，API格式 MM/DD/YYYY',
    `last_test_date` VARCHAR(10) NOT NULL COMMENT '最近测试日期，API格式 MM/DD/YYYY',

    `lot_number` VARCHAR(20) NULL COMMENT '批次号',
    `lot_number_assigned_by` VARCHAR(20) NULL COMMENT '批号分配方：Manufacturer 或 Seller；填写lot_number时建议必填',

    `manufacturer_id` BIGINT NOT NULL COMMENT '关联制造商ID',
    `poc_id` BIGINT NULL COMMENT '关联POC联系人ID',

    `product_update` CHAR(1) NOT NULL DEFAULT 'N' COMMENT 'directives.productUpdate：Y=更新已有产品，N=新增产品',
    `version_id_to_update` VARCHAR(19) NULL COMMENT 'directives.versionIdToUpdate，更新已有证书时使用',

    `data_status` VARCHAR(30) NOT NULL DEFAULT 'DRAFT' COMMENT '数据状态：DRAFT/READY/IMPORTED/ERROR/ARCHIVED',
    `remark` VARCHAR(500) NULL COMMENT '内部备注',

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY `uk_CPSC_eFiling_product_version` (`certifier_id`, `collection_id`, `primary_product_id`, `version_id`),
    KEY `idx_CPSC_eFiling_primary_product_id` (`primary_product_id`),
    KEY `idx_CPSC_eFiling_product_mfg_id` (`manufacturer_id`),
    KEY `idx_CPSC_eFiling_product_poc_id` (`poc_id`),

    CONSTRAINT `fk_CPSC_eFiling_product_manufacturer`
        FOREIGN KEY (`manufacturer_id`)
        REFERENCES `CPSC_eFiling_manufacturer` (`id`),

    CONSTRAINT `fk_CPSC_eFiling_product_poc`
        FOREIGN KEY (`poc_id`)
        REFERENCES `CPSC_eFiling_poc` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling 产品证书主表';


-- =========================================================
-- 5. 产品其他标识表
-- 对应 JSON：coreProduct.identifiers[]
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_product_identifier` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `product_certificate_id` BIGINT NOT NULL COMMENT '产品证书ID',

    `ident_type` VARCHAR(30) NOT NULL COMMENT '标识类型：GTIN/UPC/SKU/Model #/Serial #/Registered #/Alternate ID',
    `identifier` VARCHAR(50) NOT NULL COMMENT '标识值',

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    UNIQUE KEY `uk_CPSC_eFiling_product_ident` (`product_certificate_id`, `ident_type`, `identifier`),

    CONSTRAINT `fk_CPSC_eFiling_identifier_product`
        FOREIGN KEY (`product_certificate_id`)
        REFERENCES `CPSC_eFiling_product_certificate` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling 产品其他标识表';


-- =========================================================
-- 6. 产品-实验室/测试报告表
-- 对应 JSON：coreProduct.labs[] 中与具体产品相关的测试报告信息
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_product_lab` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `product_certificate_id` BIGINT NOT NULL COMMENT '产品证书ID',
    `lab_id` BIGINT NOT NULL COMMENT '实验室ID',

    `test_report_id` VARCHAR(400) NULL COMMENT '测试报告编号，对应 labs[].testReportId',
    `test_url` VARCHAR(400) NULL COMMENT '测试报告URL',
    `test_report_access_key` VARCHAR(400) NULL COMMENT '测试报告访问密码',

    `is_component` TINYINT NOT NULL DEFAULT 0 COMMENT '是否部件测试：0=否，1=是',
    `component_description` VARCHAR(250) NULL COMMENT '部件说明',

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    KEY `idx_CPSC_eFiling_product_lab_product` (`product_certificate_id`),
    KEY `idx_CPSC_eFiling_product_lab_lab` (`lab_id`),

    CONSTRAINT `fk_CPSC_eFiling_product_lab_product`
        FOREIGN KEY (`product_certificate_id`)
        REFERENCES `CPSC_eFiling_product_certificate` (`id`)
        ON DELETE CASCADE,

    CONSTRAINT `fk_CPSC_eFiling_product_lab_lab`
        FOREIGN KEY (`lab_id`)
        REFERENCES `CPSC_eFiling_lab` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling 产品实验室及测试报告表';


-- =========================================================
-- 7. 产品实验室法规代码表
-- 对应 JSON：coreProduct.labs[].citationCodes[]
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_product_lab_citation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `product_lab_id` BIGINT NOT NULL COMMENT '产品-实验室测试记录ID',
    `citation_code` VARCHAR(50) NOT NULL COMMENT '检测法规代码，如 1201、1215',

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    UNIQUE KEY `uk_CPSC_eFiling_product_lab_citation` (`product_lab_id`, `citation_code`),

    CONSTRAINT `fk_CPSC_eFiling_citation_product_lab`
        FOREIGN KEY (`product_lab_id`)
        REFERENCES `CPSC_eFiling_product_lab` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling 产品检测法规代码表';


-- =========================================================
-- 8. 产品法规豁免表
-- 对应 Excel：05_法规豁免
-- 对应 JSON：coreProduct.exemptions[]
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_product_exemption` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `product_certificate_id` BIGINT NOT NULL COMMENT '产品证书ID',
    `exemption_code` VARCHAR(100) NOT NULL COMMENT '法规豁免条款，如 1610.1(d)(1)',

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    UNIQUE KEY `uk_CPSC_eFiling_product_exemption` (`product_certificate_id`, `exemption_code`),

    CONSTRAINT `fk_CPSC_eFiling_exemption_product`
        FOREIGN KEY (`product_certificate_id`)
        REFERENCES `CPSC_eFiling_product_certificate` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling 产品法规豁免表';


-- =========================================================
-- 9. API导入批次表
-- 保存 /import 返回的 importId，以及请求/响应/日志JSON
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_import_batch` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `certifier_id` VARCHAR(100) NOT NULL COMMENT 'CPSC Certifier ID',
    `collection_id` VARCHAR(100) NOT NULL COMMENT 'CPSC Collection ID',
    `do_certify` TINYINT NOT NULL DEFAULT 0 COMMENT '是否导入时直接认证：0=否，1=是',

    `import_id` VARCHAR(100) NULL COMMENT 'CPSC返回的Import ID',
    `status_code` VARCHAR(20) NULL COMMENT 'API返回statusCode',
    `status_message` VARCHAR(500) NULL COMMENT 'API返回statusMessage',
    `import_status` VARCHAR(100) NULL COMMENT 'API返回importStatus',
    `percent_complete` INT NULL COMMENT '导入进度百分比',

    `request_json` LONGTEXT NULL COMMENT '提交给CPSC的JSON',
    `response_json` LONGTEXT NULL COMMENT 'CPSC /import 原始响应',
    `log_json` LONGTEXT NULL COMMENT 'CPSC /getImportLog 原始响应',

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    KEY `idx_CPSC_eFiling_import_id` (`import_id`),
    KEY `idx_CPSC_eFiling_import_status` (`import_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling API导入批次表';


-- =========================================================
-- 10. API导入批次明细表
-- 记录一个批次里导入了哪些产品
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_import_batch_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `import_batch_id` BIGINT NOT NULL COMMENT '导入批次ID',
    `product_certificate_id` BIGINT NOT NULL COMMENT '产品证书ID',

    `primary_product_id` VARCHAR(19) NOT NULL COMMENT '冗余存储产品ID，便于查询',
    `version_id` VARCHAR(19) NOT NULL COMMENT '冗余存储版本号',

    `imported_ind` CHAR(1) NULL COMMENT 'getImportLog validations.importedInd',
    `error_detected_ind` CHAR(1) NULL COMMENT 'getImportLog validations.errorDetectedInd',
    `error_count` INT NOT NULL DEFAULT 0 COMMENT '错误数量',

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    UNIQUE KEY `uk_CPSC_eFiling_batch_product` (`import_batch_id`, `product_certificate_id`),

    CONSTRAINT `fk_CPSC_eFiling_batch_item_batch`
        FOREIGN KEY (`import_batch_id`)
        REFERENCES `CPSC_eFiling_import_batch` (`id`)
        ON DELETE CASCADE,

    CONSTRAINT `fk_CPSC_eFiling_batch_item_product`
        FOREIGN KEY (`product_certificate_id`)
        REFERENCES `CPSC_eFiling_product_certificate` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling 导入批次明细表';


-- =========================================================
-- 11. API导入错误明细表
-- 对应 getImportLog 返回的 validations.errors[]
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_import_error` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `import_batch_item_id` BIGINT NOT NULL COMMENT '导入批次明细ID',

    `error_code` VARCHAR(20) NOT NULL COMMENT '错误码',
    `error_field` VARCHAR(200) NULL COMMENT '错误字段',
    `error_message` VARCHAR(1000) NULL COMMENT '错误信息',
    `error_message_cn` VARCHAR(1000) NULL COMMENT '中文处理建议',

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    KEY `idx_CPSC_eFiling_error_code` (`error_code`),
    KEY `idx_CPSC_eFiling_error_field` (`error_field`),

    CONSTRAINT `fk_CPSC_eFiling_import_error_item`
        FOREIGN KEY (`import_batch_item_id`)
        REFERENCES `CPSC_eFiling_import_batch_item` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling 导入错误明细表';


-- =========================================================
-- 12. 错误码字典表，可用于中文提示
-- 可按 CPSC 文档 Error Code Dictionary 继续补全
-- =========================================================
CREATE TABLE IF NOT EXISTS `CPSC_eFiling_error_dictionary` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    `error_code` VARCHAR(20) NOT NULL COMMENT '错误码',
    `error_field` VARCHAR(200) NULL COMMENT '错误字段',
    `error_message` VARCHAR(1000) NOT NULL COMMENT '英文错误信息',
    `error_message_cn` VARCHAR(1000) NULL COMMENT '中文解释/处理建议',
    `category` VARCHAR(100) NULL COMMENT '错误类别：certificate/trade_party/testing',

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY `uk_CPSC_eFiling_error_code_field` (`error_code`, `error_field`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CPSC eFiling 错误码字典表';


-- =========================================================
-- 13. 初始化部分常见错误码
-- =========================================================
INSERT IGNORE INTO `CPSC_eFiling_error_dictionary`
(`error_code`, `error_field`, `error_message`, `error_message_cn`, `category`)
VALUES
('1001', 'name', 'The Product Name is invalid.', '产品名称无效，请检查是否为空、过长或包含非法字符。', 'certificate'),
('1003', 'primaryProductIdType', 'The Product ID Type is invalid.', '产品ID类型无效，请使用 GTIN、UPC、SKU、Model #、Serial #、Registered #、Alternate ID。', 'certificate'),
('1004', 'primaryProductId', 'The Product ID is invalid.', '产品唯一ID无效，请检查长度、重复或非法字符。', 'certificate'),
('1018', 'manufactureDate', 'The Manufacture Date is invalid.', '生产日期格式无效，请使用 MM/YYYY，例如 06/2026。', 'certificate'),
('1024', 'lastTestDate', 'The Last Test Date is invalid.', '最近测试日期格式无效，请使用 MM/DD/YYYY，例如 06/19/2026。', 'certificate'),
('1025', 'versionId', 'The Certificate Version is invalid.', '证书版本号无效，请检查版本号格式或是否重复。', 'certificate'),
('2002', '<TRADE_PARTY_TYPE>.name', 'The Name for the <TRADE_PARTY_TYPE> is invalid.', '贸易相关方名称无效，请检查制造商、实验室或POC名称。', 'trade_party'),
('2003', '<TRADE_PARTY_TYPE>.alternateId', 'The Alternate ID for the <TRADE_PARTY_TYPE> is invalid.', '贸易相关方编号无效，请检查 alternateId。', 'trade_party'),
('2005', '<TRADE_PARTY_TYPE>.alternateId', 'The Alternate ID for the <TRADE_PARTY_TYPE> is already being used by an existing <TRADE_PARTY_TYPE>.', '该编号已被已有贸易相关方使用，请不要重复创建，或将 isNew 改为 N。', 'trade_party'),
('2024', '<TRADE_PARTY_TYPE>.alternateId', 'The Alternate ID for the <TRADE_PARTY_TYPE> was not found.', '系统找不到该贸易相关方编号；如为新对象请设置 isNew=Y，如已存在请核对编号。', 'trade_party'),
('2026', 'N/A', 'No Labs or Exclusions were specified.', '产品没有填写实验室检测信息，也没有填写法规豁免信息，请至少填写其中一种。', 'testing'),
('3002', 'labs[#].testURL', 'Lab <#> has an invalid Test URL.', '测试报告URL无效，请检查链接格式。', 'testing'),
('3003', 'labs[#].testReportId', 'Lab <#> has an invalid Test Report ID.', '测试报告编号无效，请检查是否为空、过长或格式错误。', 'testing'),
('3006', 'labs[#].citationCodes', 'Lab <#> could not find citation <#>.', '检测法规代码无法识别，请核对 citationCodes。', 'testing'),
('3007', 'labs[#].citationCodes', 'Lab <#> does not have a citation specified.', '实验室记录缺少检测法规代码，请至少填写一个 citationCode。', 'testing'),
('3008', 'exemptions', 'Testing exclusion code not recognized.', '法规豁免条款无法识别，请核对 exemptions。', 'testing');


SET FOREIGN_KEY_CHECKS = 1;

-- 执行完成后可用以下语句检查表是否创建成功：
-- USE CPSC_eFiling_DB;
-- SHOW TABLES;
