-- =========================
-- customers
-- =========================
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(30),
    memo TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- staffs
-- =========================
CREATE TABLE staffs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- menus
-- =========================
CREATE TABLE menus (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    duration_minutes INT NOT NULL,
    price INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- reservations
-- =========================
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,

    customer_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,

    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,

    memo TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reservation_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reservation_staff
        FOREIGN KEY (staff_id)
        REFERENCES staffs (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_reservation_menu
        FOREIGN KEY (menu_id)
        REFERENCES menus (id)
        ON DELETE RESTRICT
);