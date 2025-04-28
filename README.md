<div align="center">

<div align="center">
  <img src="docs/Team/UML/images/QuickDrop.jpg" alt="LifeRide Banner" width="50%">
</div>


# 📦Smart Parcel Delivery System



<!-- Row 1 -->
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=hibernate)
![GitHub Projects](https://img.shields.io/badge/GitHub%20Projects-181717?style=flat-square&logo=github)
![JUnit](https://img.shields.io/badge/JUnit-25A162?style=flat-square&logo=junit5)
![Mockito](https://img.shields.io/badge/Mockito-3DDC84?style=flat-square&logo=mockito)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker)
![Azure](https://img.shields.io/badge/Azure-0078D4?style=flat-square&logo=microsoftazure)
![OpenTelemetry](https://img.shields.io/badge/OpenTelemetry-000000?style=flat-square&logo=opentelemetry)

</div>


---
<div align="center">

[![DeliveryParcel.me - Live](https://img.shields.io/badge/DeliveryParcel.me-Live-brightgreen?style=for-the-badge&logo=google-chrome&logoColor=white)](https://deliveryparcel.me)

<br/>


[![WBS Docs](https://img.shields.io/badge/WBS_Docs-view-FF6B6B?style=for-the-badge&logo=googledocs)](https://docs.google.com/spreadsheets/d/1xkvcfqKs8xaK-G49qsPYhYuLTmGR0uW70pVxoA6MyY0/edit?gid=1578793000#gid=1578793000)
[![Diagrams](https://img.shields.io/badge/Diagrams-view-grey?labelColor=E88305&style=for-the-badge&logo=lucid&logoColor=white)](https://github.com/Learnathon-By-Geeky-Solutions/byte-breeze/tree/main/docs/Team/UML)
[![Project Board](https://img.shields.io/badge/Project_Board-view-8A2BE2?style=for-the-badge&logo=bookstack)](https://github.com/orgs/Learnathon-By-Geeky-Solutions/projects/69/views/6?filterQuery=-sprint%3A%22Sprint+7%22)
[![Wiki](https://img.shields.io/badge/Wiki-view-black?style=for-the-badge&logo=github)](https://github.com/Learnathon-By-Geeky-Solutions/byte-breeze/wiki)
[![GitHub Issues](https://img.shields.io/badge/issues-view-red?style=for-the-badge&logo=github)](https://github.com/Learnathon-By-Geeky-Solutions/byte-breeze/issues)
</div>

---


## 📊 SonarCloud Analysis

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Learnathon-By-Geeky-Solutions_byte-breeze&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Learnathon-By-Geeky-Solutions_byte-breeze)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Learnathon-By-Geeky-Solutions_byte-breeze&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Learnathon-By-Geeky-Solutions_byte-breeze)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Learnathon-By-Geeky-Solutions_byte-breeze&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Learnathon-By-Geeky-Solutions_byte-breeze)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Learnathon-By-Geeky-Solutions_byte-breeze&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Learnathon-By-Geeky-Solutions_byte-breeze)



---
## 📑 Table of Contents

<details>
<summary>🔽 Click to expand/collapse</summary>

- [Live Deployment](#-live-deployment)
- [Team Members & Mentor](#-team-members--mentor)
- [Project Context](#-project-context)
- [Key Features](#-key-features)
    - [Customer (P2P)](#-customer-p2p)
    - [E-Commerce Seller (B2C)](#-e-commerce-seller-b2c)
    - [Delivery Rider](#-delivery-rider)
    - [System Admin](#-system-admin)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure-simplified)
- [Project Resources](#-project-resources)
- [Getting Started](#getting-started)
- [Development Guidelines](#development-guidelines)
- [Resources](#resources)

</details>

---

## 🚀 Live Deloyment

### https://deliveryparcel.me/

### 🔐 Portal Access Credentials

| Portal       | URL                                                        | Email                | Password           |
|--------------|------------------------------------------------------------|----------------------|---------------------|
| **Customer** | [Login](https://deliveryparcel.me/auth/login)    |       |          |
| **Rider**    | [Login](https://deliveryparcel.me/rider/login)   |    |       |
| **Admin**    | [Login](https://deliveryparcel.me/admin/login)   | *Access Restricted*  | *Access Restricted* |
---

## 👥 Team Members & Mentor

| Role        | Name                                                         | GitHub Profile                                      |
|-------------|--------------------------------------------------------------|-----------------------------------------------------|
| Team Leader | Suvash Kumar                                                 | [@suvashsumon](https://github.com/suvashsumon)     |
| Member      | Md. Khairul Bashar                                           | [@KhairulBasharbd](https://github.com/KhairulBasharbd) |
| Member      | Md. Tofael Ahmed                                             | [@Tofaal9152](https://github.com/Tofaal9152)        |
| Mentor      | Jamilur Rahman, *Senior Software Engineer, Brain Station 23* | [@jamilxt](https://github.com/jamilxt)             |


---

<h1 align= "center">Smart Parcel Delivery System</h1>

A tech-driven, role-based parcel delivery platform designed to address the modern challenges of intra-city logistics. Built to streamline the delivery process for **customers**, **e-commerce sellers**, and **delivery riders**, with powerful admin tools and real-time tracking capabilities.

---

## 🚀 Project Context

With rapid urbanization and the e-commerce boom, the demand for reliable, efficient, and transparent parcel delivery services has skyrocketed. Traditional systems fall short—facing issues like delays, poor tracking, and ineffective communication.

**Smart Parcel Delivery System** bridges this gap through a robust, GPS-enabled, user-friendly solution that offers:
- Real-time tracking
- Role-specific dashboards
- Transparent communication
- Seamless delivery management

---
## 🔑 Key Features

### 👤 Customer (P2P)
- 📍 **User-Friendly Booking** – Set pickup/drop-off locations and parcel details
- 🧱 **Product Categorization** – Choose from predefined categories with limits
- 🔔 **Order Notifications** – Updates when a rider accepts/completes a delivery
- 📡 **Real-Time Tracking** – GPS tracking for live parcel status
- ⭐ **Feedback System** – Rate and review rider performance
- 🤝 **Secure Handover** – Only authenticated riders can receive parcels

### 🛒 E-Commerce Seller (B2C)
- 📊 **Seller Dashboard** – Track deliveries and rider assignments
- 🔔 **Order Notifications** – Instant alerts on delivery progress
- 📡 **Live Tracking** – Monitor each parcel in real time
- 📈 **Analytics & Reports** – View rider performance, success rates
- ⭐ **Feedback System** – Rate and review services
- 🤝 **Secure Handover** – Verified handover to rider

### 🛵 Delivery Rider
- 📶 **Rider Status** – Set availability status
- 🔔 **Delivery Alerts** – Nearby requests via push notifications
- 🗺️ **Route Navigation** – Optimized GPS-based routing
- 📦 **Order Management** – Accept, manage, and complete deliveries
- ⭐ **Performance Reviews** – Access feedback and ratings
- 🤝 **Secure Delivery** – Handover only to verified recipients
- 💵 **Payment** – Receive payment after each delivery

### 🛠️ System Admin
- 👥 **User & Delivery Management** – Oversee accounts and operations
- 📊 **Analytics Dashboard** – Insights into system and rider efficiency
- 🎯 **Parcel & Pricing Control** – Define pricing models and limits
- 🛡️ **Fraud Detection** – Monitor and prevent suspicious activity

---

## 🧰 Tech Stack

| Layer           | Technology                                 |
|----------------|--------------------------------------------|
| **Frontend**    | React.js, Thymeleaf                        |
| **Styling**     | HTML, Tailwind CSS                         |
| **Backend**     | Java, Spring Boot                          |
|**Databse Versioning** | Flyway                                     |
| **Authentication** | Spring Security, JWT                       |
| **Database**    | PostgreSQL (Relational), Hibernate ORM     |
| **APIs**        | RESTful APIs, Spring MVC                   |
| **Testing**     | JUnit (Unit), Postman (API), JMeter (Load) |
| **DevOps**      | AWS (Deployment), GitHub Actions (CI/CD)   |
| **Version Control** | Git, GitHub                                |
---

## 📁 Project Structure (Simplified)
```
├───config
├───controller
├───dto
│   ├───paymentapiresponse
│   ├───request
│   └───response
├───enums
├───exception
│   └───custom
├───mapper
├───model
├───repository
├───security
├───service
└───util
```


## 📄 Project Resources

| Category             | Document Name           | Link                                                                 |
|----------------------|-------------------------|----------------------------------------------------------------------|
| 📘 Documents         | Requirement Breakdown    | [View](https://docs.google.com/spreadsheets/d/1xkvcfqKs8xaK-G49qsPYhYuLTmGR0uW70pVxoA6MyY0/edit?gid=1403762707#gid=1403762707) |
| 📘 Documents         | Project Documentation    | [View](docs/Team/)                                                   |
| 🧩 UML Diagram       | UML Diagram              | [View](docs/Team/UML/)                                               |
| 🗄️ Database Schema   | Database Schema          | [View](docs/Team/database/)                                          |





## Getting Started
0. **Ensure Prerequisites:** Ensure your system have those running:

| Tool        | Required Version | Download Link                                          |
|-------------|------------------|--------------------------------------------------------|
| Java JDK    | 17+              | [Download JDK](https://adoptium.net/)                 |
| Gradle      | Wrapper included | N/A (uses `./gradlew`)                                |
| PostgreSQL  | 12+              | [Download](https://www.postgresql.org/download/)      |
| Git         | Any              | [Download](https://git-scm.com/downloads)             |

1. **Clone Project:** Clone the project to your local machine using SSH:
```
git clone git@github.com:Learnathon-By-Geeky-Solutions/byte-breeze.git
```
2. **Nevigate to Directory:** Navigate to the Project Directory:
```
cd byte-breeze
```
3. **Install Dependencies:** Use Gradle Wrapper to install dependencies and build the application:
```
./gradlew build --refresh-dependencies
```
Make sure you have executable permission on `gradlew`. If not, run:
```
chmod +x gradlew
```
4. **Set Environment Variables:** To run this project you need to set the following environment variables on your environment:
```
DB_URL=your_postgresql_database_url
DB_USERNAME=your_postgresql_database_username
DB_PASSWORD=your_postgresql_database_password
SSLCOM_STORE_ID=your_sslcommerz_store_id
SSLCOM_STORE_PASSWD=your_sslcommerz_store_password 
SSLCOM_BASE_URL=your_sslcommerz_base_url
```
5. **Run:** Run the Application by this command:
```
./gradlew bootRun
```
The app will start on the default port: `http://localhost:8080`

## Resources
- [Project Documentation](docs/)
- [Development Setup](docs/setup.md)
- [Contributing Guidelines](CONTRIBUTING.md)
