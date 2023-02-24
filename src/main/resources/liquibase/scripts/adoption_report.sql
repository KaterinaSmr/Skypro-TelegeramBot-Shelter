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

-- changeset egrobacheva:3
ALTER TABLE adoption_report
DROP COLUMN text_report;

-- changeset egrobacheva:4
ALTER TABLE adoption_report RENAME TO adoption_report_dog;
CREATE TABLE adoption_report_cat
(
    id          SERIAL PRIMARY KEY ,
    adoption_id INT REFERENCES adoption_cat(id),
    file_path   VARCHAR,
    media_type  VARCHAR,
    report_date DATE
)