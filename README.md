# Customer Management System
Overview
Spring Boot app to manage customer data (First Name, Last Name, Preferred Name, Date of Birth) via RESTful APIs. Features:

Create, retrieve, update (preferred name), and delete customers.
DDD with Customer entity and CustomerDTO for APIs.
80%+ test coverage via JaCoCo.

# Technologies

Spring Boot 3.2.5
RESTful API
Spring Data JPA & H2 (in-memory)
SpringDoc OpenAPI (Swagger)
Gradle 7.6.1+
JaCoCo 0.8.12
JUnit 5, Mockito
Lombok

# Setup

Clone Repo:
git clone <repository-url>
cd bank


# Build:
./gradlew clean build


# Run:
./gradlew bootRun


API: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html
H2 Console: http://localhost:8080/h2-console (JDBC: jdbc:h2:mem:testdb, user: sa, pass: empty)


# Test & Coverage:
./gradlew test


JaCoCo report: build/reports/jacoco/test/html/index.html



# API Documentation
View endpoints at http://localhost:8080/swagger-ui.html. Examples:

Create Customer:curl -X POST http://localhost:8080/customers -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","dateOfBirth":"1990-01-01"}'


Update Preferred Name:curl -X PATCH http://localhost:8080/customers/1/preferred-name -H "Content-Type: application/json" -d '"Johnny"'



# Structure

src/main/java/com.allica.bank.customer.domain: Customer, CustomerRepo
src/main/java/com.allica.bank.customer.app: CustomerService, CustomerController, CustomerDTO
src/test/java: CustomerServiceTest, CustomerControllerIntegrationTest
build.gradle: Dependencies, JaCoCo

# Troubleshooting

H2 Console: No CUSTOMER Table: Use JDBC URL jdbc:h2:mem:testdb. Create a customer via API (POST /customers). Refresh console (click tree view) or query SHOW TABLES; or SELECT * FROM CUSTOMER;. Check logs for JPA errors.
Swagger UI 500 Error: Check logs (./gradlew bootRun), ensure CustomerController returns CustomerDTO, verify springdoc-openapi dependency.

