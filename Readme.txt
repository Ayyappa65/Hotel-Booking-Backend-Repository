# Enhanced version of README_HotelBooking.txt with detailed explanation of the project and its features

readme_hotel_detailed = """# HotelBooking: Hotel Reservation Backend System

## Overview

**HotelBooking** is a comprehensive backend system designed to manage hotel reservations with a focus on modularity, scalability, and security.
It provides RESTful APIs for hotel management, user authentication, and room booking functionalities.
The system supports role-based access control using JWT and is built using Spring Boot and Spring Security.

This application is ideal for:
- Hotel chains managing multiple properties
- Online travel agencies
- Custom hotel booking platforms for enterprises

## Features

### 🔐 Authentication & User Management
- Secure login via JWT tokens
- Role-based access control using `ROLE_ADMIN` and `ROLE_USER`
- Authenticated user context via Spring Security
- Password encryption with BCrypt

### 🏨 Hotel Management (Admin Only)
- Add new hotels with relevant details
- Edit existing hotel records
- Delete hotels from the system
- View all registered hotels

### 📅 Booking Management (Users)
- Browse available hotels
- Book rooms with selected hotels
- View current and past bookings for the logged-in user
- Prevent double-booking and ensure valid dates

### 📊 Admin Dashboard (Future Scope)
- Metrics and analytics for hotel performance
- Revenue reports and booking trends
- Hotel availability calendar

### 🔒 Method-Level Access Control
- Restrict access to sensitive operations via `@PreAuthorize`
- Ensure users can only perform permitted actions based on roles


## 💻 Technology Stack

| Category      | Tech                              |
|---------------|-----------------------------------|
| Language      | Java 17                           |
| Framework     | Spring Boot                       |
| Security      | Spring Security + JWT             |
| ORM           | Spring Data JPA (Hibernate)       |
| Database      | MySQL / PostgreSQL                |
| Build Tool    | Maven                             |
| Dev Tools     | Spring Boot DevTools, Lombok      |


## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/Hotel-Booking-Backend-Repository.git
cd Hotel-Booking-Backend-Repository


## ⚙️ Spring Boot Actuator: Monitoring and Health

http://localhost:8080/actuator/metrics/http.server.requests,  
http://localhost:8080/actuator/health   

Both of these endpoints are part of Spring Boot Actuator, which provides production-ready features to help you monitor and manage your Spring Boot application.

🔍 1. /actuator/health
The application includes **Spring Boot Actuator** to support real-time monitoring, health checks, and performance metrics.

### 📍 Endpoint 1: `/actuator/health`

**Purpose:** To check if the application and its components are healthy.

#### ✅ What it Checks:
- Application is running
- Database connectivity
- Disk space status
- External services (SSL, Redis, etc.)

#### 🔧 Example Output:
```json
{
    "status": "UP",
    "components": {
        "db": {
            "status": "UP",
            "details": {
                "database": "MySQL",
                "validationQuery": "isValid()"
            }
        },
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 491944669184,
                "free": 193455534080,
                "threshold": 10485760,
                "path": "c:\\Users\\aduggineni\\Downloads\\Hotel Booking Backend Repository\\.",
                "exists": true
            }
        },
        "ping": {
            "status": "UP"
        },
        "ssl": {
            "status": "UP",
            "details": {
                "validChains": [],
                "invalidChains": []
            }
        }
    }
}
📈 Usage:
Load balancers or Kubernetes uses it to know if your app is healthy.
Useful for DevOps, monitoring, and auto-recovery.



🔍 2. /actuator/metrics/http.server.requests
📍 Endpoint 2: /actuator/metrics/http.server.requests
Purpose: To analyze traffic and performance metrics of all HTTP requests.
✅ What It Provides:
Total requests handled
Average/max request time
Breakdown by:
  -> HTTP method (GET, POST, etc.)
  -> URI (/api/v1/bookings)
  -> Status codes (200, 400, 500)
  -> Exceptions and error outcomes

#### 🔧 Example Output:
```json
{
  "name": "http.server.requests",
  "baseUnit": "seconds",
  "measurements": [
    { "statistic": "COUNT", "value": 38.0 },
    { "statistic": "TOTAL_TIME", "value": 1.06 },
    { "statistic": "MAX", "value": 0.103 }
  ],
  "availableTags": [
    { "tag": "exception", "values": ["RequestRejectedException", "none"] },
    { "tag": "method", "values": ["GET", "POST", "PUT", "DELETE"] },
    { "tag": "uri", "values": ["/api/v1/bookings", "/api/v1/rooms", "/actuator/health"] },
    { "tag": "status", "values": ["200", "400", "404"] },
    { "tag": "outcome", "values": ["SUCCESS", "CLIENT_ERROR"] }
  ]
}
