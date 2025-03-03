# 📊 Database Documentation

This document outlines the database schema for the **Byte Breeze** project, detailing the tables, their columns, relationships, and other important design considerations.

---

## 1️⃣ **Database Overview**

The Byte Breeze system uses a relational database to store and manage the data for different modules of the project. The primary entities include 

### **Key Entities:**
- **Users**: Information about customers, e-commerce sellers, and delivery riders.
- **Parcels**: Details of the parcels associated with orders.
- **Riders**: Details of the delivery riders.
- **Feedback**: Ratings and reviews given to the riders by the customers.
-  ..

---

## 2️⃣ **Database Schema**

The following table outlines the main tables and their fields.

### **Users Table**
| Column Name     | Data Type   | Description                                      |
|-----------------|-------------|--------------------------------------------------|
| user_id         | INT         | Primary Key, unique identifier for each user.   |
| name            | VARCHAR(100)| Full name of the user.                          |
| email           | VARCHAR(255)| Email address (unique).                         |
| role            | ENUM        | User role (e.g., `customer`, `seller`, `rider`). |
| created_at      | TIMESTAMP   | Date and time when the user was created.         |

### **Parcels Table**
| Column Name     | Data Type   | Description                                      |
|-----------------|-------------|--------------------------------------------------|
| parcel_id       | INT         | Primary Key, unique identifier for each parcel. |
| order_id        | INT         | Foreign Key, references `orders(order_id)`.     |
| weight          | DECIMAL     | Weight of the parcel.                           |
| dimensions      | VARCHAR(50) | Dimensions (LxWxH) of the parcel.               |

### **Riders Table**
| Column Name     | Data Type   | Description                                      |
|-----------------|-------------|--------------------------------------------------|
| rider_id        | INT         | Primary Key, unique identifier for each rider.  |
| name            | VARCHAR(100)| Full name of the rider.                         |
| phone           | VARCHAR(20) | Phone number of the rider.                      |
| status          | ENUM        | Rider status (e.g., `available`, `on delivery`). |

### **Feedback Table**
| Column Name     | Data Type   | Description                                      |
|-----------------|-------------|--------------------------------------------------|
| feedback_id     | INT         | Primary Key, unique identifier for each feedback.|
| Parcel_id        | INT         | Foreign Key, references `orders(order_id)`.     |
| rider_id        | INT         | Foreign Key, references `riders(rider_id)`.     |
| rating          | INT         | Rating given by the customer (1-5).              |
| comments        | TEXT        | Additional comments from the customer.           |

---

## 3️⃣ **Relationships Between Tables**

The following describes the relationships between the database tables:

### **Users - Parcels**
- An **User** can have multiple **Parcels** associated with it.
- A **Parcel** belongs to one **User**.
- **Relationship**: One-to-Many (1 Order -> Many Parcels).

### **Users - Feedback**
- A **User** can leave feedback for **Riders** through **Orders**.
- **Relationship**: Many-to-Many (Users -> Riders via Feedback).

---

## 4️⃣ **Normalization and Design Decisions**



## 5️⃣ **Additional Database Considerations**

- **Data Integrity**: Foreign keys and constraints ensure data integrity across the database.
- **Scalability**: The database is designed to handle large amounts of data with proper indexing and normalization to support future growth.

---

## 6️⃣ **Database Diagram**

![ER Diagram](ER_Diagram_V2.jpeg)

The Entity-Relationship (ER) diagram above visually represents the database schema and relationships between tables.

---

## 🛠️ **SQL Setup Script**
The following SQL script can be used to set up the database:

```sql
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(255) UNIQUE,
    role ENUM('customer', 'seller', 'rider'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE parcels (
    parcel_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    weight DECIMAL(10, 2),
    dimensions VARCHAR(50),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE riders (
    rider_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    phone VARCHAR(20),
    status ENUM('available', 'on delivery')
);

CREATE TABLE feedback (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    rider_id INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (rider_id) REFERENCES riders(rider_id)
);
