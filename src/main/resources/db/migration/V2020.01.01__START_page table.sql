CREATE TABLE "page"
(
    id                 bigserial primary key,
    name               varchar   not null,
    is_default         boolean not null,
    user_id            bigint not null,
    create_date_time   timestamp not null,
    update_date_time   timestamp not null
);

ALTER SEQUENCE page_id_seq INCREMENT 5;
ALTER TABLE page
    ADD FOREIGN KEY (user_id) REFERENCES "user" (id)
        ON DELETE CASCADE;