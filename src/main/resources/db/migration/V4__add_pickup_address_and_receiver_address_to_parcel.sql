ALTER TABLE parcels
    ALTER COLUMN price TYPE DECIMAL USING (price::DECIMAL);

ALTER TABLE parcels
    ALTER COLUMN receiver_address DROP NOT NULL;