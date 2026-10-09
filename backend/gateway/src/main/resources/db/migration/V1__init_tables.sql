CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    preferred_language VARCHAR(20) NOT NULL DEFAULT 'ARABIC',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_role
        CHECK (role IN ('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')),
    CONSTRAINT chk_users_preferred_language
        CHECK (preferred_language IN ('ARABIC', 'ENGLISH', 'RUSSIAN', 'FRENCH'))
);

CREATE TABLE address (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    label VARCHAR(50) NOT NULL DEFAULT 'Home',
    street_line VARCHAR(150) NOT NULL,
    city VARCHAR(30) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    default_shipping BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_address_city
        CHECK (city in ('ALEXANDRIA', 'EL_BEHEIRA', 'CAIRO', 'TANTA')),
    CONSTRAINT fk_address_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_address_user_id ON address(user_id);

-- at most one default address per user
CREATE UNIQUE INDEX uq_address_one_default ON address(user_id) WHERE default_shipping;