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

-- changeset egorbacheva:5
ALTER TYPE animal_kind RENAME VALUE 'dog' TO 'DOG';
ALTER TYPE animal_kind RENAME VALUE 'cat' TO 'CAT';
ALTER TYPE animal_kind RENAME VALUE 'bird' TO 'BIRD';
ALTER TYPE animal_kind RENAME VALUE 'rabbit' TO 'RABBIT';
ALTER TYPE animal_kind RENAME VALUE 'rat' TO 'RAT';
ALTER TYPE animal_kind RENAME VALUE 'other' TO 'OTHER';

-- changeset egorbacheva:6
CREATE CAST (character varying AS animal_kind) WITH INOUT AS ASSIGNMENT ;

-- changeset egorbacheva:7
ALTER TABLE pet DROP COLUMN pet_kind;
DROP CAST (character varying AS animal_kind);
DROP TYPE animal_kind;
ALTER TABLE pet ADD COLUMN pet_type varchar;
