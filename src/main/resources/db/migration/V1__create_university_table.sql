CREATE TABLE IF NOT EXISTS university (
                                          id BIGSERIAL PRIMARY KEY,

                                          name VARCHAR(255) NOT NULL,
    country VARCHAR(100) NOT NULL,
    min_gpa DOUBLE PRECISION,

    state VARCHAR(100),
    city VARCHAR(100),
    application_deadline VARCHAR(100),
    website_url VARCHAR(500),

    accepts_international BOOLEAN,
    accepts_fall BOOLEAN,
    accepts_spring BOOLEAN,

    application_portal VARCHAR(255),
    transfer_requirements TEXT,

    CONSTRAINT university_name_country_unique UNIQUE (name, country)
    );

CREATE INDEX IF NOT EXISTS idx_university_country
    ON university (country);

CREATE INDEX IF NOT EXISTS idx_university_min_gpa
    ON university (min_gpa);

CREATE INDEX IF NOT EXISTS idx_university_accepts_international
    ON university (accepts_international);

CREATE INDEX IF NOT EXISTS idx_university_accepts_fall
    ON university (accepts_fall);

CREATE INDEX IF NOT EXISTS idx_university_accepts_spring
    ON university (accepts_spring);