# 创建数据库did_db
CREATE DATABASE did_db_v1;
# DROP DATABASE did_db_v1;

# 使用数据库did_db
use did_db_v1;

# 校内人员
CREATE TABLE insider(
    person_id    INT AUTO_INCREMENT PRIMARY KEY,
    identity_num CHAR(18) NOT NULL UNIQUE,              # 身份证号
    did          VARCHAR(100) NOT NULL UNIQUE,          # 根DID
    name         VARCHAR(50) NOT NULL,                  # 姓名
    INDEX idx_did (did)
) ENGINE=InnoDB;

# 校外人员
CREATE TABLE outsider(
    person_id    INT AUTO_INCREMENT PRIMARY KEY,
    identity_num CHAR(18) NOT NULL UNIQUE,              # 身份证号
    did          VARCHAR(100) NOT NULL UNIQUE,          # 根DID
    name         VARCHAR(50) NOT NULL,                  # 姓名
    INDEX idx_did (did)
) ENGINE=InnoDB;

# 机构
CREATE TABLE organization(
    org_id      INT AUTO_INCREMENT PRIMARY KEY,
    org_name    VARCHAR(100) NOT NULL,                  # 机构名称
    did         VARCHAR(100) NOT NULL,                  # 掌管机构的人的DID
    org_licence VARCHAR(100) NOT NULL,                   # 机构执照（一个oss的链接）
    logo        VARCHAR(100) NOT NULL,                   # 机构logo（一个oss的链接）
    INDEX idx_org_did (did)
) ENGINE=InnoDB;

CREATE TABLE query(
    query_id    INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(50) NOT NULL,                           # 问卷名称
    vc_name     VARCHAR(50) NOT NULL,                           # 凭证名称
    org_did     VARCHAR(100) NOT NULL,                          # 涉及到的机构DID
    deadline    DATETIME NOT NULL,                              # 问卷截止时间
    expire_time DATETIME NOT NULL,                              # 凭证截止时间
    logo        VARCHAR(100) NOT NULL,                          # 机构logo（一个oss的链接）
    account     INT          NULL,
    type        VARCHAR(10) NOT NULL,                           # 记录究竟是面向insider还是outsider
    FOREIGN KEY (org_did) REFERENCES organization(did),
    INDEX idx_org_did (org_did)
) ENGINE=InnoDB;

CREATE TABLE question(
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    type        ENUM('单行文本','多行文本','下拉选择','日期选择','文件上传') NOT NULL,      # 类型
    title       VARCHAR(50) NOT NULL,                                                   # 问题描述
    title_map   VARCHAR(50) ,                                                           # 将汉字映射为英文字母作为键值
    query_id    INT NOT NULL,                                                           # 记录问题所属的问卷id
    required    BOOLEAN NOT NULL,                                                       # 是否必须字段
    FOREIGN KEY (query_id) REFERENCES query(query_id) ON DELETE CASCADE,
    INDEX idx_query_id (query_id)
) ENGINE=InnoDB;

CREATE TABLE item(
    item_id     INT AUTO_INCREMENT PRIMARY KEY,
    content     VARCHAR(50) NOT NULL,                                                   # 选项内容
    question_id INT NOT NULL,                                                           # 选项所属于的问题
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB;

CREATE TABLE blog(
    blog_id     INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(50)  NOT NULL,      # 文本标题
    type        VARCHAR(10)  NOT NULL,      # 用于记录是面向校内还是面向校外
    content     VARCHAR(500) NOT NULL,      # 文本内容
    tag         VARCHAR(20),                # 文本标签
    image1      VARCHAR(100),                # 图片1 oss链接
    image2      VARCHAR(100),
    image3      VARCHAR(100),
    image4      VARCHAR(100),
    image5      VARCHAR(100),
    image6      VARCHAR(100),
    image7      VARCHAR(100),
    image8      VARCHAR(100),
    image9      VARCHAR(100),
    query_id    INT NOT NULL,               # 记录博客需要引用的问卷
    FOREIGN KEY(query_id) REFERENCES query(query_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE commit(
    commit_id   INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NULL,
    did         VARCHAR(100) NOT NULL ,
    type        VARCHAR(20) NULL ,
    phone_num   VARCHAR(50) NULL ,
    create_time DATETIME NOT NULL,
    query_id    INT NULL,
    account INT NULL,
    FOREIGN KEY (query_id) REFERENCES query(query_id) ON DELETE CASCADE
) ENGINE=InnoDB;