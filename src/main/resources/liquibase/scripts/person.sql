-- liquibase formatted sql

-- changeset egorbacheva:1
CREATE TABLE person(
    id        SERIAL,
    chat_id   BIGINT,
    first_name  VARCHAR,
    last_name VARCHAR,
    phone   VARCHAR,
    email   VARCHAR
)

-- changeset egorbacheva:2
ALTER TABLE person add primary key (id)



