# Expense Tracker API

A backend REST API for managing personal expenses and budgets, built using Java and Spring Boot.

The project provides secure user authentication, expense management, budget tracking, searching/filtering, pagination, CSV/PDF export, API documentation, automated testing, and Docker-based deployment.

## 🚀 Tech Stack

* Java 17
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* Hibernate
* MySQL
* Maven
* REST APIs
* Bean Validation
* JUnit
* Mockito
* SpringDoc OpenAPI / Swagger
* Docker
* Docker Compose
* Git

## 📌 Features

### Authentication & Security

* User registration
* User login
* JWT-based authentication
* Access token and refresh token
* Secured APIs using Spring Security
* Password validation and authentication

### Expense Management

* Create expense
* Get expense by ID
* Get expenses
* Update expense
* Delete expense
* Search and filter expenses
* Pagination support
* Input validation

### Budget Management

* Create budget
* View budget
* Track expense usage against budget
* Budget status

### Export

* Export expenses as CSV
* Export expenses as PDF

### Exception Handling

* Global exception handling
* Custom exceptions
* Validation error handling
* Meaningful API error responses

### API Documentation

The APIs are documented using Swagger / OpenAPI.

When the application is running locally:

```text
http://localhost:8081/expense/v1/swagger-ui/index.html
```

## 🏗️ Project Architecture

The application follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Main Layers

**Controller Layer**

Handles HTTP requests and responses.

**Service Layer**

Contains business logic and application rules.

**Repository Layer**

Handles database operations using Spring Data JPA.

**Entity Layer**

Represents database tables using JPA entities.

**DTO Layer**

Used for transferring request and response data between the client and application.

**Exception Layer**

Handles application and validation errors consistently.

## 🗄️ Database

The application uses MySQL as the primary database.

Main database-related technologies:

* MySQL
* Spring Data JPA
* Hibernate

The application uses JPA/Hibernate for object-relational mapping.

## 🔐 Authentication Flow

```text
Client
   ↓
Login API
   ↓
Spring Security Authentication
   ↓
JWT Access Token
   ↓
Client sends token with API request
   ↓
JWT validation
   ↓
Protected API
```

Protected APIs require a valid JWT token.

## 🧪 Testing

The project includes different levels of automated testing.

### Unit Testing

* JUnit
* Mockito
* Service layer testing
* Helper/utility testing

### Controller Testing

* `@WebMvcTest`
* `@MockitoBean`
* MockMvc

### Repository Testing

* `@DataJpaTest`
* H2 test database

### Integration Testing

* `@SpringBootTest`
* H2 test database
* Spring Security integration

## 🐳 Docker

The application can be run using Docker and Docker Compose.

The Docker setup contains:

```text
Docker Compose
     │
     ├── Spring Boot Application
     │
     └── MySQL Database
```

The Spring Boot application communicates with MySQL using the Docker Compose service name.

### Run with Docker Compose

Make sure Docker Desktop is installed and running.

Create a `.env` file in the project root:

```text
MYSQL_ROOT_PASSWORD=your-password
```

Then run:

```bash
docker compose up -d
```

Check running containers:

```bash
docker ps
```

Check application logs:

```bash
docker logs expense-tracker-api
```

The application runs on:

```text
http://localhost:8081
```

Swagger:

```text
http://localhost:8081/expense/v1/swagger-ui/index.html
```

### Stop the application

```bash
docker compose down
```

> Note: Do not commit the `.env` file to GitHub because it contains database credentials.

## 📂 Project Structure

```text
expense-tracker-api
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── ...
│   │   └── resources
│   │       └── application.yml
│   │
│   └── test
│       └── java
│           └── ...
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── .gitignore
└── README.md
```

## ⚙️ Running Without Docker

### Prerequisites

* Java 17
* Maven
* MySQL

Configure the database connection in `application.yml`.

Then run:

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

The application starts on:

```text
http://localhost:8081
```

## 📡 Example API Flow

A typical user flow is:

```text
1. Register User
       ↓
2. Login
       ↓
3. Receive JWT Access Token
       ↓
4. Authorize using JWT
       ↓
5. Create Expense
       ↓
6. Retrieve Expenses
       ↓
7. Search / Filter / Paginate
       ↓
8. Check Budget Status
       ↓
9. Export Expenses
```

## 🎯 Project Objective

The main objective of this project is to build a production-style backend application while practicing:

* REST API development
* Spring Boot
* Spring Security
* JWT authentication
* Database persistence
* Exception handling
* Validation
* Automated testing
* API documentation
* Docker containerization

## 👩‍💻 Author

**Suganya M**

Java Backend Developer

GitHub: `itzsugan01-k`

LinkedIn: `linkedin.com/in/suganyam24`
