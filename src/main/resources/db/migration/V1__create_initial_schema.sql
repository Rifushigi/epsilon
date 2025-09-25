CREATE TABLE users
(
    id         BINARY(16)   NOT NULL,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(255) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email)
);

CREATE TABLE short_urls
(
    id            BINARY(16)    NOT NULL,
    original_url  VARCHAR(2048) NOT NULL,
    short_code    VARCHAR(255)  NOT NULL,
    click_count   BIGINT        NOT NULL DEFAULT 0,
    ttl_seconds   BIGINT        NOT NULL,
    expires_at    DATETIME(6)   NOT NULL,
    is_custom     BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at    DATETIME(6)   NOT NULL,
    updated_at    DATETIME(6),
    user_id       BINARY(16),
    PRIMARY KEY (id),
    UNIQUE KEY uk_short_code (short_code),
    INDEX idx_user_id (user_id),
    INDEX idx_expires_at (expires_at),
    CONSTRAINT fk_shorturl_on_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE clicks
(
    id           BINARY(16)   NOT NULL,
    short_url_id BINARY(16)   NOT NULL,
    ip_address   VARCHAR(45),
    user_agent   TEXT,
    referer      VARCHAR(2048),
    clicked_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_short_url_id (short_url_id),
    INDEX idx_clicked_at (clicked_at),
    CONSTRAINT fk_click_on_shorturl FOREIGN KEY (short_url_id) REFERENCES short_urls (id)
);