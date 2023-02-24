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

--changeset egorbacheva:3
ALTER TABLE person ADD UNIQUE (chat_id);

--changeset egorbacheva:4
ALTER TABLE person RENAME TO person_dog;
CREATE TABLE person_cat(
                       id        SERIAL PRIMARY KEY ,
                       chat_id   BIGINT UNIQUE ,
                       first_name  VARCHAR,
                       last_name VARCHAR,
                       phone   VARCHAR,
                       email   VARCHAR
)




