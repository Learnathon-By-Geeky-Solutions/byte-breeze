INSERT INTO users (id, full_name, email, password, created_at, updated_at)
VALUES (
           '00000000-0000-0000-0000-000000000001',
           'Super Admin',
           'suvashkumarsumon@hotmail.com',
           '$2a$06$XzYOC1q4yJ3aZeVaKEZO/uh9EuxQIc.Ui7gdnu8vW4wzX8qxPK5O2', -- @Sumon@01717
           NOW(),
           NOW()
       )
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role)
VALUES (
           '00000000-0000-0000-0000-000000000001',
           'ROLE_ADMIN'
       );