-- =========================
-- staffs: add active flag (soft delete)
-- =========================
ALTER TABLE staffs ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

-- =========================
-- shifts
-- =========================
CREATE TABLE shifts (
    id BIGSERIAL PRIMARY KEY,

    staff_id BIGINT NOT NULL,

    work_date DATE NOT NULL,
    is_off BOOLEAN NOT NULL DEFAULT FALSE,
    start_time TIME,
    end_time TIME,
    task VARCHAR(200),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_shift_staff
        FOREIGN KEY (staff_id)
        REFERENCES staffs (id)
        ON DELETE CASCADE,

    CONSTRAINT uq_shift_staff_date
        UNIQUE (staff_id, work_date)
);
