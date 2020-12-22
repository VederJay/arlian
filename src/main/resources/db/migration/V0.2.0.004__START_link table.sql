CREATE TABLE "link"
(
    id                 bigserial primary key,
    title              varchar,
    url                varchar not null,
    order_number       int not null,
    card_id            bigint not null,
    create_date_time   timestamp not null,
    update_date_time   timestamp not null
);

ALTER SEQUENCE link_id_seq INCREMENT 5;
ALTER TABLE link
    ADD FOREIGN KEY (card_id) REFERENCES "card" (id)
        ON DELETE CASCADE;