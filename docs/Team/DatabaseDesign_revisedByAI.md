### users
| id  | name  | email  | phone_no  | password_hash | is_active | email_verified | role | profile_pic | created_at       | updated_at       |
|-----|-------|--------|-----------|---------------|-----------|----------------|------|-------------|------------------|------------------|

### roles
| id  | role         | description       | type         | created_at       | updated_at       |
|-----|--------------|-------------------|--------------|------------------|------------------|

### permissions
| id  | title        | description       | created_at       | updated_at       |
|-----|--------------|-------------------|------------------|------------------|

### role_permission
| id  | role_id | permission_id | created_at       | updated_at       |
|-----|---------|---------------|------------------|------------------|

*Composite unique constraint:* `(role_id, permission_id)`

### user_role
| id  | user_id | role_id | created_at       | updated_at       |
|-----|---------|---------|------------------|------------------|

*Composite unique constraint:* `(user_id, role_id)`

### riderdetails
| id  | user_id | nid_no | nid_pic | driving_licence | driving_licence_pic | license_expiry_date | birth_date | status   | eligibility | vehicle_type | profile_completed | created_at       | updated_at       |
|-----|---------|--------|---------|-----------------|---------------------|---------------------|------------|----------|-------------|--------------|-------------------|------------------|------------------|

### parcels
| id  | description          | weight | size | pickup_location (geometry) | receiver_name | receiver_phone | receiver_email | receiver_address | receiver_location (geometry) | receiver_otp | sender_id | rider_id | rider_otp | status      | price  | distance | delivered_at       | created_at       | updated_at       |
|-----|-----------------------|--------|------|----------------------------|---------------|----------------|----------------|------------------|------------------------------|--------------|-----------|----------|-----------|-------------|--------|----------|--------------------|------------------|------------------|

### payments
| id  | user_id | method   | transaction_id | currency | amount | status     | parcel_id | created_at       | updated_at       |
|-----|---------|----------|----------------|----------|--------|------------|-----------|------------------|------------------|

*Enum for status:* `pending`, `successful`, `failed`.

### withdraws
| id  | rider_id | method   | transaction_id | amount | status     | created_at       | updated_at       |
|-----|----------|----------|----------------|--------|------------|------------------|------------------|

*Enum for status:* `pending`, `approved`, `rejected`.

### reviews
| id  | rider_id | vendor_id | feedback | star | reviewed_at       | created_at       | updated_at       |
|-----|----------|-----------|----------|------|-------------------|------------------|------------------|

*Composite index:* `(rider_id, vendor_id)`

### pricing
| id  | min_weight_gm | max_weight_gm | min_size_sqm | max_size_sqm | price    | created_at       | updated_at       |
|-----|---------------|---------------|--------------|--------------|----------|------------------|------------------|

*Ensure non-overlapping ranges for `min_weight_gm` and `max_weight_gm`.*

