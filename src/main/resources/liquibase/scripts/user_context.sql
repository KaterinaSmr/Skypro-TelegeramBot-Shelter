-- liquibase formatted sql

-- changeset egorbacheva:1
CREATE TABLE user_context(
    id          SERIAL,
    chat_id     BIGINT UNIQUE,
    last_command    VARCHAR
)

-- changeset egorbacheva:2
ALTER TABLE user_context ADD PRIMARY KEY (chat_id);
ALTER TABLE user_context DROP COLUMN id;