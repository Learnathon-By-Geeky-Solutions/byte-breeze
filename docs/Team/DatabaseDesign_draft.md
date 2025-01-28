### user
| id  | name  | email  | phone_no  | password  | role | profile_pic | created_at  | updated_at  |
|-----|-------|--------|-----------|-----------|------|-------------|-------------|-------------|

### role
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

### rider_details
| id | user_id | nid_no | nid_pic | driving_licence | driving_licence_pic | birth_date | status | eligibility | vehicle_type | created_at | updated_at |
|----|---------|--------|---------|-----------------|---------------------|------------|--------|-------------|--------------|------------|------------|

### parcel
| id | description | weight | pickup_location_lattitude | pickup_location_longitude | reciever_id | sender_id | rider_id | rider_otp | status | created_at | updated_at |
|----|-------------|--------|---------------------------|---------------------------|-------------|-----------|----------|-----------|--------|------------|------------|

### receiver
| id | name | phone_no | emali | house | street | area | lattitude | longitude | receiver_otp | created_at | updated_at |
|----|------|----------|-------|-------|--------|------|-----------|-----------|--------------|------------|------------|
