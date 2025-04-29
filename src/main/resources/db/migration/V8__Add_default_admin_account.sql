INSERT INTO users (id, full_name, email, password, created_at, updated_at)
VALUES (
           '2208a8b6-2554-4e6d-83a6-c1d8985e0fe5',
           'Super Admin',
           'suvashkumarsumon@hotmail.com',
           '{bcrypt}$2a$10$9nILaChKq/itVXkBplzKs.YxjL2h00ExuGa.qdejy622GKj6AXifG', -- @Sumon@01717
           NOW(),
           NOW()
       )
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role)
VALUES (
           '2208a8b6-2554-4e6d-83a6-c1d8985e0fe5',
           'ROLE_ADMIN'
       );