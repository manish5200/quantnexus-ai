# 🚀 QuantNexus

> _"Where quantitative precision meets enterprise-grade security."_

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%2F21-007396?logo=openjdk)](https://openjdk.org/)
[![Security](https://img.shields.io/badge/Spring%20Security-JWT-blue?logo=springsecurity)](https://spring.io/projects/spring-security)
[![Redis](https://img.shields.io/badge/Redis-Caching-red?logo=redis)](https://redis.io/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?logo=swagger)](https://swagger.io/)

**Candidate:** Manish Singh  
**Role:** Backend Developer Intern  
**Assignment:** Zorvyn FinTech Finance Data Processing & Access Control

---

## 🔎 The Story Behind the Name
**QuantNexus** is designed with architectural intent:
- **Quant (Quantitative)**: Reflects a data-driven approach where financial transactions are processed with mathematical precision.
- **Nexus (The Core)**: Represents the architecture of this system—a central hub connecting advanced security, high-performance caching, and data integrity.

---

## 📄 Project Overview
In modern financial systems, **data integrity** and **secure access** are the greatest challenges. **QuantNexus** is a robust backend engine designed to solve these by focusing on:

* 🛡️ **Security**: Strict **Role-Based Access Control (RBAC)** using Spring Security and stateless JWT.
* ⚡ **Efficiency**: Drastically reducing database load and API latency using **Redis as a high-performance caching layer** for analytics.
* 💾 **Integrity**: Full support for **Audit Trails** (who created/updated what) and **Self-Healing Temporal Ledger** to ensure financial accuracy.
* 🛠️ **Developer Experience (DX)**: Providing fully interactive API documentation using **OpenAPI/Swagger UI**.

---

## 🏗️ Core Architecture & Assumptions
This project follows the **Clean Layered Architecture** to ensure maintainability and testability.

**Assumption: The "Internal Corporate Dashboard" Model** Designed as an **internal corporate B2B tool**. The database represents a **single, unified Company Ledger** where categories reflect business operations (e.g., `REVENUE`, `INFRASTRUCTURE`, `SALARY`).

---

## 🧠 Core Enterprise Features

* 🔐 **Strict RBAC**:
    * `ADMIN` (Full Access)
    * `ANALYST` (Read + Filter)
    * `VIEWER` (Aggregated Dashboards only).
* ⏱️ **Self-Healing Temporal Ledger**: Automatic recalculation of `balanceAfter` across the chronological chain if a historical record is modified.
* 🛡️ **Defense-In-Depth**: Stateless JWT, automated brute-force lockout (5 attempts), and protection against admin self-demotion.
* 🔍 **Dynamic Filtering**: Advanced search using JPA Specifications for complex, multi-parameter queries.

---

## 📁 Project Structure (Tree-Wise)

```text
quantnexus-api
├── src/main/java/com/quantnexus
│   ├── config          # Configuration: Security, OpenAPI (Swagger), and Redis
│   ├── controller      # REST Gateways: Auth, Dashboard, Ledger, and User Management
│   ├── domain          # Data Model: JPA Entities (User, FinancialRecord) and Enums
│   ├── dto             # Data Transfer Objects: Request/Response payloads with validation
│   ├── exception       # Reliability: Global @ControllerAdvice for centralized error handling
│   ├── repository      # Persistence: Spring Data JPA Repositories and SQL Specifications
│   ├── security        # Protection: JWT filters, Token logic, and SecurityUser context
│   └── service         # Core Logic: Analytics Engine, Ledger Math, and AuthService
├── src/main/resources
│   └── application.yml # System properties and environment configuration
└── pom.xml             # Dependency Management: Spring Boot, Security, Swagger, Redis


-------
🔌 API Documentation & Usage

Interactive **Swagger UI**: Start the application and navigate to `http://localhost:8080/swagger-ui.html`.

### Core Endpoints Quick Reference

| Method | Endpoint | Allowed Roles | Description |
|--------|----------|---------------|-------------|
| `POST` | `/api/v1/auth/login` | Public | Authenticates user and returns JWT Token |
| `GET` | `/api/v1/dashboard/summary` | ADMIN, ANALYST, VIEWER | Returns aggregated company financials |
| `POST` | `/api/v1/records` | ADMIN | Logs a new transaction to the corporate ledger |
| `GET` | `/api/v1/records/history` | ADMIN, ANALYST | Retrieves paginated history with dynamic filters |
| `PUT` | `/api/v1/records/{ref}` | ADMIN | Updates an entry and triggers balance healing |
| `DELETE` | `/api/v1/records/{ref}` | ADMIN | Removes an entry from the active ledger |
| `PUT` | `/api/v1/admin/users/{userId}/role/{role}` | ADMIN | Modifies user permissions (Admin only) |
| `PATCH` | `/api/v1/admin/users/{userId}/status` | ADMIN | Toggles account locks/status |

---

## 🛠️ Technological Stack

- **Framework**: Spring Boot 3.3.x
- **Security**: Spring Security 6 & JSON Web Tokens (JWT)
- **Database**: H2 (In-memory for zero-dependency evaluation)
- **Caching**: Redis (For high-speed dashboard analytics)
- **Documentation**: SpringDoc OpenAPI 2.8.4 (Swagger)
- **Persistence**: Hibernate / Jakarta Persistence (JPA) + Specifications
- **Testing**: JUnit 5 & Mockito

---

## 🚀 Getting Started (Local Setup)

### 1. Clone & Build

```bash
git clone <your-repo-link>
cd quantnexus-api
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The server will initialize on **port 8080**. The **H2 in-memory database** will automatically spin up for zero-dependency evaluation.

---

## 💡 Interview Context (Mentor Tips)

**On Performance**: "I implemented Redis to cache dashboard analytics. Since summary totals are read-heavy but expensive to recalculate, caching ensures the dashboard feels instantaneous while protecting the database from repetitive queries."

**On Reliability**: "I used a Self-Healing Ledger approach. If an Admin edits a transaction from two weeks ago, the service automatically recalculates the 'balanceAfter' for every subsequent record chronologically to ensure data integrity."

---

*Developed by Manish Singh for the Zorvyn FinTech Backend Developer Intern Evaluation.*