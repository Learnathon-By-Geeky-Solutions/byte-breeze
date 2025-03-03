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
| id  | user_id | nid_no | nid_pic | driving_licence | driving_licence_pic | license_expiry_date | birth_date | status   | eligibility | vehicle_type | rider_avg_rating | rider_balance | profile_completed | created_at       | updated_at       |
|-----|---------|--------|---------|-----------------|---------------------|---------------------|------------|----------|-------------|--------------|-------------------|---------------|------------------|------------------|------------------|


### parcels
| id  | category_id | description          | weight | size | pickup_location (geometry) | receiver_name | receiver_phone | receiver_email | receiver_address | receiver_location (geometry) | receiver_otp | sender_id | rider_id | rider_otp | assigned_at | status | pickup_time | delivery_time     | price  | distance | delivered_at       | created_at       | updated_at       |
|-----|-------------|----------------------|--------|------|----------------------------|---------------|----------------|----------------|------------------|------------------------------|--------------|-----------|----------|-----------|-------------|--------|-------------|------------------|--------|----------|------------------|------------------|------------------|


*Enum for status : `assigned`, `picked_up`, `in_transit`, `delivered`, `postponed`*

### payments
This table stores vendor's payment related to parcel booking.

| id  | user_id | bank_id   | transaction_id | currency | amount | status     | parcel_id | created_at       | updated_at       |
|-----|---------|----------|----------------|----------|--------|------------|-----------|------------------|------------------|

*Enum for status:* `pending`, `successful`, `failed`.

### withdraws
This table stores withdraw details related to rider's withdrawn.

| id  | rider_id | method   | transaction_id | amount | status  | requested_at    | proceed_at      | bank_id | accountholdername | account_no | created_at       | updated_at       |
|-----|----------|---------|----------------|--------|---------|-----------------|-----------------|---------|------------------|------------|------------------|------------------|

*Enum for status:* `pending`, `approved`, `rejected`.

### bank
This table stores information about system supported withdraw/payment methods.

| id | bank_name |
|----|-----------|

### reviews
This table stores information about review given by vendor to rider on specific delivery.

| id  | rider_id | vendor_id | percel_id | feedback | star | reviewed_at       | created_at       | updated_at       |
|-----|----------|-----------|-----------|----------|------|-------------------|------------------|------------------|

*Composite index:* `(rider_id, vendor_id)`

### pricing
| id  | min_weight_gm | max_weight_gm | min_size_sqm | max_size_sqm | price    | created_at       | updated_at       |
|-----|---------------|---------------|--------------|--------------|----------|------------------|------------------|

*Ensure non-overlapping ranges for `min_weight_gm` and `max_weight_gm`.*


### productcategory
| id | category | crated_at | updated_at |
|----|----------|-----------|------------|

