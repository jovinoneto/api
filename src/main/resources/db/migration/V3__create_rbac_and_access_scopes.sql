CREATE TABLE roles (
    id              UUID PRIMARY KEY,
    -- NULL means a system-wide role; otherwise role belongs to one company.
    company_id      UUID REFERENCES companies(id),
    code            VARCHAR(80) NOT NULL,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    is_system       BOOLEAN NOT NULL DEFAULT FALSE,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_system_role_scope CHECK (NOT is_system OR company_id IS NULL)
);

CREATE UNIQUE INDEX uq_roles_global_code
    ON roles(code) WHERE company_id IS NULL;
CREATE UNIQUE INDEX uq_roles_company_code
    ON roles(company_id, code) WHERE company_id IS NOT NULL;
CREATE INDEX ix_roles_company_status ON roles(company_id, status);

CREATE TABLE permissions (
    id              UUID PRIMARY KEY,
    code            VARCHAR(150) NOT NULL UNIQUE,
    resource        VARCHAR(100) NOT NULL,
    action          VARCHAR(50) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_permissions_resource_action UNIQUE(resource, action)
);

CREATE TABLE user_roles (
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_by     UUID REFERENCES users(id) ON DELETE SET NULL,
    assigned_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);
CREATE INDEX ix_user_roles_role ON user_roles(role_id);

CREATE TABLE role_permissions (
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id   UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    granted_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Reserved for future ABAC conditions. Not interpreted by the initial
    -- authorization implementation; NULL means ordinary RBAC grant.
    condition_json  JSONB,
    PRIMARY KEY (role_id, permission_id)
);
CREATE INDEX ix_role_permissions_permission ON role_permissions(permission_id);

CREATE TABLE user_permissions (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission_id   UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    effect          VARCHAR(10) NOT NULL CHECK (effect IN ('ALLOW', 'DENY')),
    granted_by      UUID REFERENCES users(id) ON DELETE SET NULL,
    reason          TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMPTZ,
    -- Reserved for future ABAC conditions; do not evaluate until policy
    -- evaluation is explicitly implemented.
    condition_json  JSONB,
    CONSTRAINT uq_user_permission UNIQUE(user_id, permission_id),
    CONSTRAINT ck_user_permission_expiry
        CHECK (expires_at IS NULL OR expires_at > created_at)
);
CREATE INDEX ix_user_permissions_user_effect ON user_permissions(user_id, effect);

-- A user may have multiple scopes. Exactly one target column must be set,
-- except OWN, which represents records owned/created by the user.
CREATE TABLE user_scopes (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    scope_type      VARCHAR(20) NOT NULL
                    CHECK (scope_type IN ('GLOBAL', 'COMPANY', 'BRANCH', 'DEPARTMENT', 'OWN')),
    company_id      UUID REFERENCES companies(id),
    branch_id       UUID,
    department_id   UUID,
    created_by      UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMPTZ,
    CONSTRAINT ck_user_scope_target CHECK (
        (scope_type = 'GLOBAL' AND company_id IS NULL AND branch_id IS NULL AND department_id IS NULL)
        OR (scope_type = 'COMPANY' AND company_id IS NOT NULL AND branch_id IS NULL AND department_id IS NULL)
        OR (scope_type = 'BRANCH' AND company_id IS NOT NULL AND branch_id IS NOT NULL AND department_id IS NULL)
        OR (scope_type = 'DEPARTMENT' AND company_id IS NOT NULL AND department_id IS NOT NULL AND branch_id IS NULL)
        OR (scope_type = 'OWN' AND company_id IS NULL AND branch_id IS NULL AND department_id IS NULL)
    ),
    CONSTRAINT fk_user_scope_branch_company
        FOREIGN KEY (company_id, branch_id)
        REFERENCES branches(company_id, id),
    CONSTRAINT fk_user_scope_department_company
        FOREIGN KEY (company_id, department_id)
        REFERENCES departments(company_id, id),
    CONSTRAINT ck_user_scope_expiry
        CHECK (expires_at IS NULL OR expires_at > created_at)
);

CREATE INDEX ix_user_scopes_user ON user_scopes(user_id);
CREATE INDEX ix_user_scopes_company ON user_scopes(company_id);
CREATE INDEX ix_user_scopes_branch ON user_scopes(branch_id);
CREATE INDEX ix_user_scopes_department ON user_scopes(department_id);

-- Optional subject attributes make the schema ABAC-ready without introducing
-- an ABAC policy engine now. Applications may populate these as needed.
CREATE TABLE user_attributes (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    attribute_key   VARCHAR(100) NOT NULL,
    attribute_value JSONB NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_attribute UNIQUE(user_id, attribute_key)
);
CREATE INDEX ix_user_attributes_key ON user_attributes(attribute_key);
