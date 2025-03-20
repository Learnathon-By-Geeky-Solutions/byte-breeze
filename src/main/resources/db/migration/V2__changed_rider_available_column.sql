ALTER TABLE riders
    ADD is_available BOOLEAN;

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_on_rider FOREIGN KEY (user_id) REFERENCES riders (id);

ALTER TABLE riders
DROP
COLUMN want_to_delivery;