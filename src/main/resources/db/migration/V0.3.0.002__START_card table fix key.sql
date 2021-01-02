ALTER TABLE card
    ALTER COLUMN user_id TYPE bigint;

DROP SEQUENCE card_user_id_seq CASCADE;