# 💼 Expense Reimbursement System

A **microservices-based web application** that enables employees to submit expense claims digitally and allows managers to review, approve, or reject them with a structured workflow.

---

## 🚀 Overview

This system replaces manual expense handling (emails, receipts, spreadsheets) with a **secure, transparent, and automated platform**.

### 🔑 Key Features
- Role-based access (**Employee / Manager**)
- Submit expense claims with details
- Manager approval/rejection workflow
- Real-time claim status tracking
- Secure authentication using JWT
- Microservices architecture for scalability
- Centralized audit and validation logic

---

## 🏗️ Architecture

The application follows a **Microservices Architecture**:

- **Auth Service** → Handles login, registration, JWT generation  
- **Employee Service** → Manages employee data and hierarchy  
- **Expense Service** → Handles expense submission & approval workflow  
- **Eureka Server** → Service discovery  
- **Frontend (React)** → User interface  

---

## 🔄 Workflow

### 1. Authentication
- User registers (Employee/Manager)
- Logs in → receives **JWT Token**

### 2. Employee Flow
- Submits expense claim (amount, description)
- Claim stored with status **PENDING**

### 3. Manager Flow
- Views assigned employee claims
- Approves or rejects claims
- Status updated to **APPROVED / REJECTED**

### 4. Security & Validation
- JWT token used for all API requests
- Role-based authorization enforced
- Manager can only act on their employees’ claims

---

## 🛠️ Tech Stack

### 🔹 Backend
- Java, Spring Boot
- Spring Security (Authentication & Authorization)
- JWT (JSON Web Token)
- Spring Data JPA, Hibernate
- REST APIs
- Microservices Architecture

### 🔹 Frontend
- React.js
- HTML5, CSS3, JavaScript
- Axios (API calls)

### 🔹 Database
- PostgreSQL

### 🔹 DevOps & Tools
- Git & GitHub
- Maven
- Postman (API testing)
- SonarQube (Code Quality)

### 🔹 Architecture Tools
- Eureka Server (Service Discovery)
- API Gateway *(if implemented)*

---

## 📂 Project Structure
expense-reimbursement-system/
├── backend/
│   ├── auth-service/                # Handles JWT, Login, and Permissions
│   │   ├── src/main/java/com/ers/auth/
│   │   │   ├── controller/          # REST Endpoints
│   │   │   ├── service/             # Business Logic
│   │   │   ├── repository/          # Database access
│   │   │   └── security/            # JWT & Spring Security configs
│   │   └── pom.xml
│   ├── employee-service/            # Manages Employee profiles & hierarchy
│   │   ├── src/main/java/com/ers/employee/
│   │   └── pom.xml
│   ├── expense-service/             # Core logic for claims, receipts, & status
│   │   ├── src/main/java/com/ers/expense/
│   │   │   ├── model/               # Expense Entities
│   │   │   └── dto/                 # Data Transfer Objects
│   │   └── pom.xml
│   └── eureka-server/               # Service Discovery Registry
│       ├── src/main/resources/
│       │   └── application.yml      # Configured for Eureka Server
│       └── pom.xml
├── frontend/
│   └── expense-portal/              # Vite + React/Vue TypeScript App
│       ├── public/                  # Static assets (logos, favicon)
│       ├── src/
│       │   ├── assets/              # Styles, images
│       │   ├── components/          # Reusable UI components (Buttons, Tables)
│       │   ├── hooks/               # Custom React hooks
│       │   ├── pages/               # Dashboard, Login, ClaimSubmission
│       │   ├── services/            # API call logic (Axios/Fetch)
│       │   ├── store/               # State management (Redux/Zustand)
│       │   ├── types/               # TypeScript interfaces
│       │   ├── App.tsx
│       │   └── main.tsx
│       ├── .env                     # Environment variables (API URLs)
│       ├── eslint.config.js
│       ├── index.html
│       ├── package.json
│       ├── tsconfig.json
│       └── vite.config.ts
├── .gitignore
└── README.md

```
