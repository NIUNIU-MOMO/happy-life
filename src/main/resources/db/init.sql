CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS t_user (
    id BIGSERIAL PRIMARY KEY,
    openid VARCHAR(64) NOT NULL UNIQUE,
    nickname VARCHAR(64),
    avatar_url VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE t_user IS '用户表';

CREATE TABLE IF NOT EXISTS t_post (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    description TEXT,
    images JSONB DEFAULT '[]',
    video_url VARCHAR(255),
    location_name VARCHAR(128),
    location_point GEOGRAPHY(POINT, 4326),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    post_date DATE,
    tags JSONB DEFAULT '[]',
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);
COMMENT ON TABLE t_post IS '帖子表';

CREATE INDEX IF NOT EXISTS idx_post_user ON t_post(user_id);
CREATE INDEX IF NOT EXISTS idx_post_location ON t_post USING GIST(location_point);
CREATE INDEX IF NOT EXISTS idx_post_date ON t_post(post_date DESC);

CREATE TABLE IF NOT EXISTS t_favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, post_id)
);
COMMENT ON TABLE t_favorite IS '收藏表';
