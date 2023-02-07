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