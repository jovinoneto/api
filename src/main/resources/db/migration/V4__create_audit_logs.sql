CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY,
    actor_user_id   UUID REFERENCES users(id) ON DELETE SET NULL,
    company_id      UUID REFERENCES companies(id),
    action          VARCHAR(150) NOT NULL,
    entity_type     VARCHAR(100) NOT NULL,
    entity_id       VARCHAR(150),
    outcome         VARCHAR(20) NOT NULL DEFAULT 'SUCCESS'
                    CHECK (outcome IN ('SUCCESS', 'FAILURE', 'DENIED')),
    ip_address      INET,
    user_agent      TEXT,
    before_data     JSONB,
    after_data      JSONB,
    metadata        JSONB,
    occurred_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX ix_audit_logs_actor_date ON audit_logs(actor_user_id, occurred_at DESC);
CREATE INDEX ix_audit_logs_company_date ON audit_logs(company_id, occurred_at DESC);
CREATE INDEX ix_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX ix_audit_logs_action_date ON audit_logs(action, occurred_at DESC);
