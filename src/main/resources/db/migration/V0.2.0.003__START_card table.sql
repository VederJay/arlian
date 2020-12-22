CREATE TABLE "card"
(
    id                 bigserial primary key,
    title              varchar not null,
    type               int not null,
    position           int not null,
    order_number       int not null,
    page_id            bigint not null,
    create_date_time   timestamp not null,
    update_date_time   timestamp not null
);

ALTER SEQUENCE card_id_seq INCREMENT 5;
ALTER TABLE card
    ADD FOREIGN KEY (page_id) REFERENCES "page" (id)
        ON DELETE CASCADE;