### User
| id  | name  | email  | phone_no  | password  | role | created_at  | updated_at  |
|-----|-------|--------|-----------|-----------|------|-------------|-------------|

### Parcel
| id | description | weight | pickup_location_lattitude | pickup_location_longitude | reciever_id | sender_id | status | created_at | updated_at |
|----|-------------|--------|---------------------------|---------------------------|-------------|-----------|--------|------------|------------|

### Receiver
| id | name | phone_no | emali | house | street | area | lattitude | longitude | receiver_otp | created_at | updated_at |
|----|------|----------|-------|-------|--------|------|-----------|-----------|--------------|------------|------------|

### percel_deliveryman
| id | deliveryman_id | percel_id | pickup_otp | created_at | updated_at |
|----|----------------|-----------|------------|------------|------------|
