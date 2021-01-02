CREATE TABLE "picture"
(
    id                 bigserial primary key,
    user_id            bigint not null,
    create_date_time   timestamp not null,
    update_date_time   timestamp not null,
    image              bytea
);

ALTER SEQUENCE picture_id_seq INCREMENT 5;
ALTER TABLE picture
    ADD FOREIGN KEY (user_id) REFERENCES "user" (id)
        ON DELETE CASCADE;