-- 1. Add the column (still nullable)
ALTER TABLE payment
    ADD COLUMN share_type VARCHAR(255);

-- 2. Existing payments were all split in equal parts
UPDATE payment
SET share_type = 'EQUALS';

-- 3. Now that every row has a value, make it mandatory
ALTER TABLE payment
    ALTER COLUMN share_type SET NOT NULL;

ALTER TABLE payment
    ADD CONSTRAINT payment_share_type_check CHECK (share_type IN ('EQUALS', 'AMOUNTS'));
