CREATE DATABASE IF NOT EXISTS daoyou DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE daoyou;

CREATE TABLE IF NOT EXISTS `user` (
 uuid VARCHAR(64) PRIMARY KEY,
 username VARCHAR(255) UNIQUE NOT NULL,
 password VARCHAR(128) NOT NULL,
 avatar VARCHAR(255),
 introduction VARCHAR(255),
 background VARCHAR(255),
 cultivation DECIMAL(12,2) DEFAULT 0.00,
 is_deleted TINYINT DEFAULT 0,
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 KEY idx_cultivation(cultivation DESC),
 KEY idx_username(username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `cultivation_logs` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 user_uuid VARCHAR(64),
 delta_requested DECIMAL(10,2),
 delta_actual DECIMAL(10,2),
 source VARCHAR(32),
 ref_id VARCHAR(64),
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 KEY idx_user_time(user_uuid, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `buildings` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(64) NOT NULL,
 region VARCHAR(64),
 description VARCHAR(255),
 cover_url VARCHAR(255),
 sort_order INT DEFAULT 0,
 status TINYINT DEFAULT 1,
 is_deleted TINYINT DEFAULT 0,
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 update_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `articles` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 uuid VARCHAR(64) UNIQUE NOT NULL,
 author_uuid VARCHAR(64) NOT NULL,
 building_id BIGINT NOT NULL,
 title VARCHAR(128) NOT NULL,
 body MEDIUMTEXT NOT NULL,
 img_urls JSON,
 type VARCHAR(16) DEFAULT 'normal',
 agree_count INT DEFAULT 0,
 collect_count INT DEFAULT 0,
 chat_count INT DEFAULT 0,
 score INT DEFAULT 0,
 relic_status VARCHAR(16) NULL,
 relic_root_content MEDIUMTEXT NULL,
 relic_generation INT DEFAULT 0,
 relic_pool_round INT DEFAULT 0,
 relic_last_merged_user_uuid VARCHAR(64) NULL,
 relic_last_merged_at DATETIME NULL,
 relic_last_destroyed_at DATETIME NULL,
 is_deleted TINYINT DEFAULT 0,
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 KEY idx_building_score(building_id, score DESC),
 KEY idx_author_building(author_uuid, building_id, score DESC),
 KEY idx_type_status(type, relic_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `relic_pool` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 article_id BIGINT NOT NULL,
 generation INT NOT NULL,
 pool_round INT NOT NULL,
 user_uuid VARCHAR(64) NOT NULL,
 content VARCHAR(500) NOT NULL,
 like_count INT DEFAULT 0,
 status VARCHAR(16) DEFAULT 'pending',
 merge_deadline DATETIME NOT NULL,
 merged_at DATETIME NULL,
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 UNIQUE(article_id, generation, pool_round, user_uuid),
 KEY idx_deadline(status, merge_deadline)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `relic_segments` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 article_id BIGINT NOT NULL,
 generation INT NOT NULL,
 seq INT NOT NULL,
 pool_round INT NOT NULL,
 user_uuid VARCHAR(64) NOT NULL,
 content VARCHAR(500) NOT NULL,
 merged_at DATETIME DEFAULT CURRENT_TIMESTAMP,
 UNIQUE(article_id, generation, seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `relic_pool_likes` (
 pool_id BIGINT NOT NULL,
 user_uuid VARCHAR(64) NOT NULL,
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY(pool_id, user_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `relic_participants` (
 article_id BIGINT,
 generation INT,
 user_uuid VARCHAR(64),
 first_at DATETIME DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY(article_id, generation, user_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `relic_destroy_votes` (
 article_id BIGINT,
 generation INT,
 user_uuid VARCHAR(64),
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY(article_id, generation, user_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `reactions` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 article_id BIGINT,
 user_uuid VARCHAR(64),
 type VARCHAR(16),
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 UNIQUE(article_id, user_uuid, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `chats` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 article_id BIGINT,
 user_uuid VARCHAR(64),
 content VARCHAR(200),
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 KEY idx_article(article_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `friend_requests` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 from_uuid VARCHAR(64),
 to_uuid VARCHAR(64),
 message VARCHAR(128),
 status VARCHAR(16) DEFAULT 'pending',
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 UNIQUE(from_uuid, to_uuid),
 KEY idx_to_status(to_uuid, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `friends` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 user_uuid VARCHAR(64),
 friend_uuid VARCHAR(64),
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 UNIQUE(user_uuid, friend_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `notifications` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 user_uuid VARCHAR(64),
 type VARCHAR(32),
 title VARCHAR(64),
 content VARCHAR(255),
 payload JSON,
 read_at DATETIME NULL,
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 KEY idx_user_read(user_uuid, read_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `stats_daily` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 stat_date DATE,
 metric VARCHAR(32),
 value BIGINT,
 UNIQUE(stat_date, metric)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `stats_total` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 metric VARCHAR(32) UNIQUE,
 value BIGINT,
 update_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `admin_operation_log` (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 admin_uuid VARCHAR(64),
 operation VARCHAR(64),
 target_type VARCHAR(32),
 target_id VARCHAR(64),
 detail JSON,
 create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO buildings (name, region, description, sort_order, status)
SELECT seed.name, seed.region, seed.description, seed.sort_order,1
FROM (
 SELECT '西湖断桥' AS name, '杭州' AS region, '烟柳画桥，风帘翠幕。' AS description,10 AS sort_order
 UNION ALL SELECT '长安酒肆', '长安', '一壶浊酒，半城风月。',20
 UNION ALL SELECT '蜀山剑阁', '蜀山', '剑鸣云海，问道青冥。',30
 UNION ALL SELECT '江南书院', '江南', '墨香入卷，儒风长存。',40
 UNION ALL SELECT '渡口', '江畔', '舟横野渡，缘起一程。',50
 UNION ALL SELECT '市集', '长安', '人间烟火，百态相逢。',60
 UNION ALL SELECT '山门', '昆仑', '叩问仙途，自此登临。',70
 UNION ALL SELECT '竹林', '蜀山', '竹影摇风，静听剑意。',80
) AS seed
WHERE NOT EXISTS (
 SELECT 1 FROM buildings b WHERE b.name = seed.name AND b.is_deleted =0
);
