ALTER TABLE picture RENAME COLUMN image to original_image;
ALTER TABLE picture ADD COLUMN reduced_image bytea;