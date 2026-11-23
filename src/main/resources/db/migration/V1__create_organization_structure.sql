-- PostgreSQL
-- IDs are supplied by the application (UUID); no database UUID extension required.

CREATE TABLE companies (
    id              UUID PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE,
    legal_name      VARCHAR(200) NOT NULL,
    trade_name      VARCHAR(200),
    tax_id          VARCHAR(30),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMPTZ
);

CREATE TABLE branches (
    id              UUID PRIMARY KEY,
    company_id      UUID NOT NULL REFERENCES companies(id),
    code            VARCHAR(50) NOT NULL,
    name            VARCHAR(200) NOT NULL,
    tax_id          VARCHAR(30),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT uq_branches_company_code UNIQUE (company_id, code),
    CONSTRAINT uq_branches_company_id_id UNIQUE (company_id, id)
);

CREATE TABLE departments (
    id              UUID PRIMARY KEY,
    company_id      UUID NOT NULL REFERENCES companies(id),
    branch_id       UUID,
    code            VARCHAR(50) NOT NULL,
    name            VARCHAR(200) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT uq_departments_company_code UNIQUE (company_id, code),
    CONSTRAINT uq_departments_company_id_id UNIQUE (company_id, id),
    CONSTRAINT fk_departments_branch_company
        FOREIGN KEY (company_id, branch_id)
        REFERENCES branches(company_id, id)
);

CREATE INDEX ix_branches_company_status ON branches(company_id, status);
CREATE INDEX ix_departments_company_branch ON departments(company_id, branch_id);
