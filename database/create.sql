# 创建数据库did_db
# CREATE DATABASE did_db;
# DROP DATABASE did_db;

# 使用数据库did_db
use did_db;

-- 创建did与个人的管理表
CREATE TABLE did_person(
    identity_num CHAR(18) NOT NULL ,
    did          VARCHAR(100) NOT NULL,
    name         VARCHAR(20) NOT NULL ,
    PRIMARY KEY (identity_num, did)
);

-- 创建did与组织对应的表
CREATE TABLE did_org(
    org_id      INT AUTO_INCREMENT PRIMARY KEY,
    did         VARCHAR(100) NOT NULL UNIQUE,
    org_name    VARCHAR(50)  NOT NULL
);

-- 创建问卷表
CREATE TABLE query(
    query_id    INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(50) NOT NULL ,
    type        ENUM('信息收集', '活动报名') NOT NULL ,
    deadline    DATETIME    NOT NULL
);

-- 创建组织与问卷对应的表
CREATE TABLE org_query(
    did         VARCHAR(100) NOT NULL ,
    query_id    INT          NOT NULL ,
    PRIMARY KEY (did, query_id),
    FOREIGN KEY (did) REFERENCES did_org(did),
    FOREIGN KEY (query_id) REFERENCES query(query_id)
);

-- 创建题目表
CREATE TABLE question(
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    type        ENUM('单行文本', '多行文本', '下拉选择', '日期选择', '文件上传') NOT NULL ,
    key_title       VARCHAR(50)
);

-- 创建问卷/题目对应关系表
CREATE TABLE query_question(
    query_id INT NOT NULL ,
    question_id INT NOT NULL ,
    PRIMARY KEY (query_id, question_id),
    FOREIGN KEY (query_id) REFERENCES query(query_id),
    FOREIGN KEY (question_id) REFERENCES question(question_id)
);

CREATE TABLE select_items(
    question_id INT NOT NULL ,
    item    VARCHAR(50) NOT NULL ,
    PRIMARY KEY (question_id, item),
    FOREIGN KEY (question_id) REFERENCES question(question_id)
)