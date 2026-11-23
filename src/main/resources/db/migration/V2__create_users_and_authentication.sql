CREATE TABLE users (
    id                      UUID PRIMARY KEY,
    full_name               VARCHAR(200) NOT NULL,
    email                   VARCHAR(320) NOT NULL,
    password_hash           VARCHAR(255) NOT NULL,
    phone                   VARCHAR(30),
    avatar_url              TEXT,
    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE', 'BLOCKED')),
    email_verified_at       TIMESTAMPTZ,
    last_login_at           TIMESTAMPTZ,
    password_changed_at     TIMESTAMPTZ,
    must_change_password    BOOLEAN NOT NULL DEFAULT FALSE,
    failed_login_attempts   INTEGER NOT NULL DEFAULT 0
                            CHECK (failed_login_attempts >= 0),
    locked_until            TIMESTAMPTZ,
    expires_at              TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at              TIMESTAMPTZ
);

-- Case-insensitive uniqueness for login email.
CREATE UNIQUE INDEX uq_users_email_lower ON users (LOWER(email));
CREATE INDEX ix_users_status ON users(status);
CREATE INDEX ix_users_deleted_at ON users(deleted_at);

CREATE TABLE user_sessions (
    id                  UUID PRIMARY KEY,
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash          VARCHAR(255) NOT NULL UNIQUE,
    ip_address          INET,
    user_agent          TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_seen_at        TIMESTAMPTZ,
    expires_at          TIMESTAMPTZ NOT NULL,
    revoked_at          TIMESTAMPTZ
);

CREATE INDEX ix_user_sessions_user_active
    ON user_sessions(user_id, expires_at)
    WHERE revoked_at IS NULL;
