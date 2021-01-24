CREATE TABLE "picture_group"
(
    id                 bigserial primary key,
    create_date_time   timestamp NOT NULL,
    update_date_time   timestamp NOT NULL
);
ALTER SEQUENCE picture_group_id_seq INCREMENT 5;

CREATE TABLE "user_picture_group_link"
(
    id                 bigserial primary key,
    create_date_time   timestamp NOT NULL,
    update_date_time   timestamp NOT NULL,
    user_id            bigint    NOT NULL,
    picture_group_id   bigint    NOT NULL,
    role               char      NOT NULL
);
ALTER SEQUENCE user_picture_group_link_id_seq INCREMENT 5;
ALTER TABLE user_picture_group_link
    ADD FOREIGN KEY (user_id) REFERENCES "user" (id)
        ON DELETE CASCADE;
ALTER TABLE user_picture_group_link
    ADD FOREIGN KEY (picture_group_id) REFERENCES "picture_group" (id)
        ON DELETE CASCADE;


-- add a picture group for every user that has pictures and update the sequence
INSERT INTO picture_group (id, create_date_time, update_date_time)
SELECT distinct user_id, now(), now() FROM picture;
SELECT setval('picture_group_id_seq', (SELECT max(id) FROM picture_group));

-- add a user picture group link for every user that has pictures and update the sequence
INSERT INTO user_picture_group_link (id, create_date_time, update_date_time, user_id, picture_group_id, role)
SELECT id, now(), now(), id, id, 'O' FROM picture_group;
SELECT setval('picture_group_id_seq', (SELECT max(id) FROM user_picture_group_link));


ALTER TABLE "picture" ADD COLUMN "picture_group_id" bigint NULL;
UPDATE picture SET picture_group_id = user_id;
ALTER TABLE "picture" ALTER COLUMN "picture_group_id" SET NOT NULL;
ALTER TABLE picture
    ADD FOREIGN KEY (picture_group_id) REFERENCES "picture_group" (id)
        ON DELETE CASCADE;
ALTER TABLE picture DROP CONSTRAINT picture_user_id_fkey;
ALTER TABLE "picture" DROP COLUMN "user_id";
