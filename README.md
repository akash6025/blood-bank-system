# 🩸 Blood Bank Management System

## Overview
A Spring Boot + Thymeleaf web app to manage donors, blood stock, and blood requests.

## Tech Stack
- Spring Boot (Web, Thymeleaf, Data JPA, Validation)
- PostgreSQL
- Java 17, Maven

## Setup
1. Create PostgreSQL database:
   - Database: `bloodbank`
   - User/Pass: create and grant privileges
2. Configure `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/bloodbank
   spring.datasource.username=YOUR_DB_USERNAME
   spring.datasource.password=YOUR_DB_PASSWORD
   ```
3. Build and run:
   ```bash
   mvn spring-boot:run
   ```
4. Open http://localhost:8080

## Features
- Donor registration and listing
- Create/track blood requests; approve/reject
- Manage blood stock per blood group

## Notes
- Schema is auto-managed via `spring.jpa.hibernate.ddl-auto=update` for local development.
- Update as needed for production readiness (migrations, auth, roles, validations, tests).
