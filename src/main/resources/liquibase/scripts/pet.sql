-- liquibase formatted sql

-- changeset egorbacheva:2
CREATE TABLE pet
(
    id            SERIAL,
    name          VARCHAR,
    kind          VARCHAR,
    year_of_birth INT
)
-- changeset egorbacheva:3
ALTER TABLE pet add primary key (id)

--changeset egorbacheva:4
CREATE TYPE animal_kind AS ENUM
    ('dog', 'cat', 'bird', 'rabbit', 'rat', 'other');
ALTER TABLE pet DROP COLUMN kind;
ALTER TABLE pet ADD COLUMN pet_kind animal_kind;
