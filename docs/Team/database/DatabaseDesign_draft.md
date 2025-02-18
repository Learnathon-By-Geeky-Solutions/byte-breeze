### users
| id  | name  | email  | phone_no  | password  | role | profile_pic | created_at  | updated_at  |
|-----|-------|--------|-----------|-----------|------|-------------|-------------|-------------|

### roles
| id | role | created_at | updated_at |
|----|------|------------|------------|

### permissions
| id | title | craeted_at | updated_at |
|----|-------|------------|------------|

### role_parmission
| id | role_id | permission_id | created_at | updated_at |
|----|---------|---------------|------------|------------|

### user_role
| id | user_id | role_id | created_at | updated_at |
|----|---------|---------|------------|------------|

### riderdetails
| id | user_id | nid_no | nid_pic | driving_licence | driving_licence_pic | birth_date | status | eligibility | vehicle_type | created_at | updated_at |
|----|---------|--------|---------|-----------------|---------------------|------------|--------|-------------|--------------|------------|------------|

### parcels

| id  | description          | weight | pickup_latitude        | pickup_longitude        | receiver_name | receiver_phone | receiver_email | receiver_house | receiver_street | receiver_area | receiver_latitude | receiver_longitude | receiver_otp | sender_id | rider_id | rider_otp | status      | created_at         | updated_at         |
|-----|-----------------------|--------|------------------------|-------------------------|---------------|----------------|----------------|----------------|------------------|----------------|--------------------|--------------------|--------------|-----------|----------|-----------|-------------|--------------------|--------------------|

### payments
| id | user_id | method | transaction_id | amount | status | parcel_id | created_at | updated_at |
|----|---------|--------|----------------|--------|--------|-----------|------------|------------|

### withdraws
| id | rider_id | method | amount | status | created_at | updated_at |
|----|----------|--------|--------|--------|------------|------------|

### reviews
| id | rider_id | vendor_id | feedback | star | created_at | updated_at |
|----|----------|-----------|----------|------|------------|------------|

### pricing
| id  | min_weight_gm | max_weight_gm | min_size_sqm | max_size_sqm | price    | created_at         | updated_at         |
|-----|---------------|---------------|--------------|--------------|----------|--------------------|--------------------|



