-- liquibase formatted sql

-- changeset egorbacheva:1
CREATE TABLE adoption_report
(
    id          SERIAL PRIMARY KEY ,
    adoption_id INT REFERENCES adoption(id),
    file_path   VARCHAR,
    media_type  VARCHAR,
    text_repot  TEXT,
    report_date DATE
)

-- changeset egorbacheva:2
ALTER TABLE adoption_report
RENAME COLUMN text_repot TO text_report;