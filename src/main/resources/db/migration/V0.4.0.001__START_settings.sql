CREATE TABLE "settings"
(
    id                 bigserial primary key,
    create_date_time   timestamp NOT NULL,
    update_date_time   timestamp NOT NULL,
    user_id            bigint    NOT NULL
);

ALTER SEQUENCE settings_id_seq INCREMENT 5;
ALTER TABLE settings
    ADD FOREIGN KEY (user_id) REFERENCES "user" (id)
        ON DELETE CASCADE;