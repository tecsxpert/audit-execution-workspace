
# Audit Execution Workspace
# Audit Execution Backend

## Tech Stack
- Spring Boot
- PostgreSQL
- Redis
- Flyway
- Docker
- Swagger

## Features
- CRUD APIs for Audit Items
- Audit logging (create/update/delete tracking)
- Redis caching for performance
- JWT-based authentication (basic)
- Global exception handling
- DTO-based clean architecture

## How to Run

```bash
docker-compose up -d
mvn spring-boot:run

##Swagger UI:
http://localhost:8080/swagger-ui/index.html

