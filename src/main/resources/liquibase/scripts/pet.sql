-- liquibase formatted sql

-- changeset egorbacheva:2
CREATE TABLE pet
(
    id            SERIAL,
    name          VARCHAR,
    kind          VARCHAR,
    year_of_birth INT
)
