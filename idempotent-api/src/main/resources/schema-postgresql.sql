CREATE TABLE idempotency_keys (
    id              BIGSERIAL   PRIMARY KEY,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    idempotency_key TEXT        NOT NULL
        CHECK (char_length(idempotency_key) <= 100),
    last_run_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    locked_at       TIMESTAMPTZ DEFAULT now(),

    -- parameters of the incoming request
    request_method  TEXT        NOT NULL
        CHECK (char_length(request_method) <= 10),
    request_params  TEXT       NOT NULL,
    request_path    TEXT        NOT NULL
        CHECK (char_length(request_path) <= 100),

    -- for finished requests, stored status code and body
    response_code   INT         NULL,
    response_body   TEXT       NULL,

    recovery_point  TEXT        NOT NULL
        CHECK (char_length(recovery_point) <= 50),
    user_id         BIGINT      NOT NULL
);

CREATE UNIQUE INDEX idempotency_keys_user_id_idempotency_key
    ON idempotency_keys (user_id, idempotency_key);


--
-- A relation to hold records for every user of our app.
--
CREATE TABLE users (
    id                 BIGSERIAL       PRIMARY KEY,
    email              TEXT            NOT NULL UNIQUE
        CHECK (char_length(email) <= 255),

    -- Mambu account record
    mambu_customer_id TEXT            NOT NULL UNIQUE
        CHECK (char_length(mambu_customer_id) <= 50)
);

--
-- Now that we have a users table, add a foreign key
-- constraint to idempotency_keys which we created above.
--
ALTER TABLE idempotency_keys
    ADD CONSTRAINT idempotency_keys_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT;

--
-- A relation that hold audit records that can help us piece
-- together exactly what happened in a request if necessary
-- after the fact. It can also, for example, be used to
-- drive internal security programs tasked with looking for
-- suspicious activity.
--
CREATE TABLE audit_records (
    id                 BIGSERIAL       PRIMARY KEY,

    -- action taken, for example "created"
    event             TEXT            NOT NULL
        CHECK (char_length(event) <= 50),

    created_at         TIMESTAMPTZ     NOT NULL DEFAULT now(),
    data               TEXT           NOT NULL,
    origin_ip          TEXT            NOT NULL,

    -- resource ID and type, for example "account" ID 123
    resource_id        BIGINT          NOT NULL,
    resource_type      TEXT            NOT NULL
        CHECK (char_length(resource_type) <= 50),

    user_id            BIGINT          NOT NULL
        REFERENCES users ON DELETE RESTRICT
);

--
-- A relation representing a single account by a user.
-- Notably, it holds the ID of a successful created account on
-- Mambu after we have one.
--
CREATE TABLE accounts (
    id                 BIGSERIAL       PRIMARY KEY,
    created_at         TIMESTAMPTZ     NOT NULL DEFAULT now(),

    -- Store a reference to the idempotency key so that we can recover an
    -- already-created account. Note that idempotency keys are not stored
    -- permanently, so make sure to SET NULL when a referenced key is being
    -- reaped.
    idempotency_key_id BIGINT
        REFERENCES idempotency_keys ON DELETE SET NULL,

    -- custom data
    account_holder_id  TEXT NOT NULL,
    product_type_id    TEXT NOT NULL,

    -- ID of Mambu deposit account; NULL until we have one
    deposit_account_id   TEXT            UNIQUE
        CHECK (char_length(deposit_account_id) <= 50),

    -- ID of Coelsa cbu account; NULL until we have one
    --cbu_account_id               TEXT            UNIQUE
    --    CHECK (char_length(deposit_account_id) <= 50),

    user_id            BIGINT          NOT NULL
        REFERENCES users ON DELETE RESTRICT,
    CONSTRAINT accounts_user_id_idempotency_key_unique UNIQUE (user_id, idempotency_key_id)
);

CREATE INDEX accounts_idempotency_key_id
    ON accounts (idempotency_key_id)
    WHERE idempotency_key_id IS NOT NULL;

--
-- A relation that holds our transactionally-staged jobs
-- (see "Background jobs and job staging" above).
--
CREATE TABLE staged_jobs (
    id                 BIGSERIAL       PRIMARY KEY,
    job_name           TEXT            NOT NULL,
    job_args           TEXT           NOT NULL
);