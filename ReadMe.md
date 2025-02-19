# Fabrick Banking Service

## Overview
This project is a Spring Boot REST service that integrates with a third-party banking API (Fabrick) to provide basic banking operations. The service exposes endpoints to retrieve account balance, list transactions for a specific date range, and perform money transfers. In addition to calling remote APIs via RestTemplate, it persists money transfer transactions locally to an H2 database for audit and traceability.

## Architecture and Technologies
* Framework: Spring Boot
* HTTP Client: RestTemplate for external API communication
* Persistence: Spring Data JPA with an H2 in-memory database (with separate configuration profiles for testing)
* API Documentation: Swagger/OpenAPI annotations included in the controller
* Validation: Jakarta Bean Validation for request payloads
* Exception Handling: A GlobalExceptionHandler centrally manages all exceptions
* Testing: Both unit tests (using Mockito with JUnit 5) and integration tests are provided

## Project Structure

### 1. Service and Controller Layers

#### BankingService & BankingServiceImpl
* The BankingService interface defines the core banking operations: retrieving balance, fetching transactions, and creating money transfers.
* BankingServiceImpl contains the business logic, building REST calls to the external API and then validating the responses. It also persists a record of successful money transfers using the TransactionRepository.

#### BankingController
* Exposes REST endpoints ("/balance", "/transactions", "/transfer") to allow clients to interact with the banking operations.
* Uses Swagger annotations for easy API documentation.
* Performs input validation (e.g., date range checks for retrieving transactions) and settings (such as time zone management) based on request headers.

### 2. Data Transfer and Persistence

#### DTOs (Data Transfer Objects)
* BalanceDTO and TransactionDTO represent the API responses for balance and transaction details while ensuring a consistent JSON mapping.

#### MoneyTransferRequest and MoneyTransferResponse
* Define the contract for initiating a money transfer (with request validations) and handling the response from the external API.

#### TransactionEntity & TransactionRepository
* The TransactionEntity models a money transfer transaction record stored in the local database.
* The TransactionRepository (extending JpaRepository) provides methods (e.g., for fetching transactions by date ranges) to query the database.

### 3. Configuration and External Integration

#### FabrickConfig
* Reads external API configuration properties (base URL, API key, authorization schema, account ID, and time zone) from application properties.

#### RestTemplateConfig
* Provides a preconfigured RestTemplate bean used by the service layer to make external REST API calls.

#### Application Configuration Files
* application.yml and application-test.yml define environment-specific settings including datasources, JPA setup, logging levels, and Fabrick API credentials.

### 4. Exception Handling

#### GlobalExceptionHandler
* Implements controller advice to capture and manage exceptions uniformly across the API.
* Handles specific cases like validation errors, malformed JSON requests, and custom BankingServiceException errors that are thrown when external API calls fail or return unexpected results.

#### BankingServiceException
* A custom runtime exception used in the service layer to propagate error details when remote operations fail or return errors.

### 5. Testing

#### Unit Tests
* BankingServiceImplTest & BankingControllerTest cover the business logic in the service layer and the REST endpoints in the controller.
* They use mocking to simulate external API calls and verify the correct behavior of the service.

#### Integration Tests
* TransactionRepositoryIntegrationTest verifies the persistence layer and ensures that the TransactionRepository correctly saves and retrieves records from the database.

## Usage and Running the Application

### Prerequisites
* Java 11 (or above)
* Maven (or your preferred build tool)

### Building the Project
* Run `mvn clean install` to compile and run tests.

### Running the Application
* Use `mvn spring-boot:run` to start the application on port 8080.
* The H2 console is enabled and available at "/h2-console" (configured in application.yml).

### API Documentation
* Swagger/OpenAPI annotations allow for automatic documentation generation.
* Visit the Swagger UI endpoint (if configured) to see available endpoints and try out the API calls.

## Conclusion
This project demonstrates a layered architecture integrating external APIs with robust error handling, validation, and persistence. The structure and testing approach ensure that the service is reliable and maintainable. The use of good practices such as centralized exception management, configuration abstraction, and comprehensive testing makes this solution a strong candidate for production readiness in a professional banking context.
