# 🏨 HotelBooking: Comprehensive Hotel Reservation Backend System

**HotelBooking** is a modular, secure, and scalable hotel management backend application built with **Spring Boot**. It facilitates user authentication, role-based access, and hotel listing operations for hotel administrators and guests.

This backend system is structured with layered architecture principles and supports JWT-based security and RESTful APIs. It's built to integrate easily with front-end clients and third-party systems.

---

## 📁 Repository Structure

src/main/java/com/ayyappa/hotelbooking/
├── controller/ # REST API endpoints (Hotel, Auth, etc.)
├── dto/ # Data Transfer Objects for request/response
├── enums/ # Enum definitions (e.g., roles)
├── exception/ # Custom exception classes and handlers
├── model/ # JPA entities (User, Hotel, Booking, etc.)
├── payload/ # Response wrappers like MessageResponse
├── repository/ # Spring Data JPA repositories
├── security/ # Spring Security, JWT configs and filters
├── service/ # Business logic implementation
└── HotelBookingApplication.java # Application entry point


### Key Files

- `HotelBookingApplication.java`: Main Spring Boot application launcher.
- `src/main/resources/application.yml`: Configuration file for database, JWT, logging, etc.
- `pom.xml`: Maven project definition with dependencies and plugins.

---

## 🚀 Usage Instructions

### ✅ Prerequisites

- Java 17+
- Maven 3.6+
- MySQL database
- (Optional) Docker & Kafka for future scalability

### 🔧 Installation & Setup

#### 1. Clone the repository:
git clone https://github.com/Ayyappa65/Hotel-Booking-Backend-Repository.git
cd Hotel-Booking-Backend-Repository

Configure:

properties.yml
spring.datasource.url=jdbc:postgresql://localhost:5432/hotelbooking
spring.datasource.username=username
spring.datasource.password=password

Build the project:
mvn clean install

Run the application:
mvn spring-boot:run


## 🛠 Troubleshooting

### 🔌 Database Connection Issue:
**Error:**
**Fix:**
- Ensure your database is running and reachable.
- Double-check `application.properties` for DB URL, username, and password.
- Verify that the DB port is not blocked by a firewall.

---

### 🔐 Access Denied (403 Forbidden)

**Error Message:**

**Cause:**
Spring Security is enforcing **method-level security**, and the authenticated user doesn't have the required role to access the endpoint or method.

**Common Scenarios:**
- You're calling a method secured with `@PreAuthorize("hasRole('ADMIN')")` but the user only has `USER`.
- You forgot to send a valid JWT token with the `Authorization` header.
- The token doesn't include the required role.
<!-- @PreAuthorize("hasRole('ROLE_ADMIN')")
public void createHotel(Hotel hotel) {
    ...
} -->

**Fix:**
- Make sure your token is valid and included in the request header:
  ```http
  Authorization: Bearer <your-jwt-token>
Invalid or expired JWT token
