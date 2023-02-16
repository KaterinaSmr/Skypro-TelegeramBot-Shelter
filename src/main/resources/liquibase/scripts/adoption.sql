-- liquibase formatted sql

-- changeset egorbacheva:1
CREATE TABLE adoption
(
    id                  SERIAL PRIMARY KEY ,
    person_id           INT REFERENCES person(id),
    pet_id              INT REFERENCES pet(id),
    adoption_start_date DATE,
    probation_end_date  DATE,
    probation_finished  BOOL,
    adoption_confirmed  BOOL
)

-- changeset egorbacheva:2
ALTER TABLE adoption ADD UNIQUE (pet_id);
ALTER TABLE adoption ADD UNIQUE (person_id);

-- changeset egorbacheva:3
CREATE TYPE adoption_status AS ENUM
    ('ON_PROBATION', 'PROBATION_EXTENDED', 'PROBATION_SUCCESSFUL',
'PROBATION_FAILED', 'ADOPTION_CONFIRMED', 'ADOPTION_REFUSED');
ALTER TABLE adoption DROP COLUMN probation_finished;
ALTER TABLE adoption DROP COLUMN adoption_confirmed;
ALTER TABLE adoption ADD COLUMN adoption_status adoption_status;

--changeset egorbacheva:4
ALTER TYPE adoption_status ADD VALUE 'NOT STARTED' BEFORE 'ON_PROBATION';
ALTER TABLE adoption RENAME COLUMN adoption_start_date TO probation_start_date;

--changeset egorbacheva:5
ALTER TYPE adoption_status RENAME VALUE 'NOT STARTED' TO 'NOT_STARTED'

--changeset egorbacheva:6
CREATE CAST (character varying AS adoption_status) WITH INOUT AS ASSIGNMENT ;

--changeset egorbacheva:7
ALTER TABLE adoption DROP COLUMN adoption_status;
ALTER TABLE adoption ADD COLUMN adoption_status varchar;
DROP CAST (character varying AS adoption_status);
DROP TYPE adoption_status;