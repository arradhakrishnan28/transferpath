CREATE TABLE IF NOT EXISTS program (
                                       id BIGSERIAL PRIMARY KEY,

                                       university_id BIGINT NOT NULL,

                                       major_name VARCHAR(255) NOT NULL,
    degree_level VARCHAR(100),
    estimated_annual_cost DOUBLE PRECISION,
    minimum_recommended_gpa DOUBLE PRECISION,
    competitiveness_level VARCHAR(100),
    program_url VARCHAR(500),

    CONSTRAINT fk_program_university
    FOREIGN KEY (university_id)
    REFERENCES university (id)
    ON DELETE CASCADE,

    CONSTRAINT program_university_major_unique
    UNIQUE (university_id, major_name, degree_level)
    );

CREATE INDEX IF NOT EXISTS idx_program_university_id
    ON program (university_id);

CREATE INDEX IF NOT EXISTS idx_program_major_name
    ON program (major_name);

CREATE INDEX IF NOT EXISTS idx_program_minimum_recommended_gpa
    ON program (minimum_recommended_gpa);

CREATE INDEX IF NOT EXISTS idx_program_estimated_annual_cost
    ON program (estimated_annual_cost);

CREATE INDEX IF NOT EXISTS idx_program_competitiveness_level
    ON program (competitiveness_level);