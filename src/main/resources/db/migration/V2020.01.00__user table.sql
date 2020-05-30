CREATE TABLE "user"
(
    id                 bigserial primary key,
    given_name         varchar   not null,
    full_name          varchar   not null,
    email_address      varchar   not null,
    picture_url        varchar   null,
    "create_date_time" timestamp NOT NULL,
    "update_date_time" timestamp NOT NULL
);

ALTER SEQUENCE user_id_seq INCREMENT 5;