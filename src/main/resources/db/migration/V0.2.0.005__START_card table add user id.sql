ALTER TABLE card
    ADD COLUMN user_id bigserial;

ALTER TABLE card
    ADD FOREIGN KEY (user_id) REFERENCES "user" (id)
        ON DELETE CASCADE;