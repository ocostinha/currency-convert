# Purchase Transaction Application

A production-ready Spring Boot application implementing hexagonal architecture for managing purchase
transactions with real-time currency conversion capabilities using the U.S. Department of the
Treasury's Reporting Rates of Exchange API.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technical Stack](#technical-stack)
- [Currency Conversion Strategy](#currency-conversion-strategy)
- [Features](#features)
- [API Documentation](#api-documentation)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Deployment](#deployment)
- [Monitoring & Observability](#monitoring--observability)
- [Testing](#testing)
- [Database](#database)

## Overview

This application provides a RESTful API for storing purchase transactions in USD and retrieving them
with automatic currency conversion to any supported currency. The system integrates with the
Treasury Reporting Rates of Exchange API to obtain accurate, government-published exchange rates.

### Key Capabilities

- **Transaction Management**: Store and retrieve purchase transactions with comprehensive validation
- **Currency Conversion**: Real-time conversion using official Treasury exchange rates
- **Resilience**: Circuit breaker, retry mechanisms, and rate limiting for external API calls
- **Observability**: Structured logging, metrics, and health checks via Spring Boot Actuator
- **Performance**: Caching layer with Caffeine for optimized exchange rate lookups
- **Production-Ready**: Docker support, graceful shutdown, and configurable profiles

## Architecture

The application follows **Hexagonal Architecture** (Ports and Adapters) principles, ensuring clean
separation of concerns and high testability:

```
┌─────────────────────────────────────────────────────────────┐
│                     Infrastructure Layer                     │
│  (REST Controllers, JPA Repositories, External API Clients) │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Application Layer                       │
│         (Use Cases, Business Logic, Orchestration)          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Domain Layer                          │
│              (Core Business Models, Interfaces)              │
└─────────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

- **Domain Layer** (`domain/`, `core/`): Contains core business entities (Purchase) and port
  interfaces (PurchaseBusiness, FiscalDataService, PurchaseRepository)
- **Application Layer** (`purchase/business/`, `fiscalData/service/`): Implements business logic and
  use cases
- **Infrastructure Layer** (`purchase/controller/`, `repository/`, `fiscalData/gateway/`,
  `infrastructure/`): Adapters for external systems (REST, database, external APIs)

## Technical Stack

| Component             | Technology                 | Version       |
|-----------------------|----------------------------|---------------|
| **Runtime**           | Java                       | 21            |
| **Framework**         | Spring Boot                | 3.4.1         |
| **Build Tool**        | Maven                      | 3.x           |
| **Database**          | H2 (embedded)              | Runtime       |
| **Migration**         | Flyway                     | Latest        |
| **Resilience**        | Resilience4j               | 2.1.0         |
| **Caching**           | Caffeine                   | Latest        |
| **Metrics**           | Micrometer + Prometheus    | Latest        |
| **API Documentation** | SpringDoc OpenAPI          | 2.3.0         |
| **Logging**           | Logback + Logstash Encoder | 7.4           |
| **Rate Limiting**     | Bucket4j                   | 8.10.1        |
| **Container**         | Docker (Eclipse Temurin)   | 21-jre-alpine |

## Currency Conversion Strategy

### Country-Currency Description Approach

This application uses the **Country-Currency Description** field from the Treasury API instead of
the generic currency code. This strategic decision addresses a critical challenge in international
currency conversion: **currency name ambiguity**.

#### Problem Statement

Multiple countries use currencies with identical names but different values. For example:

| Country   | Currency Name | ISO Code | Exchange Rate (Example) |
|-----------|---------------|----------|-------------------------|
| Australia | Dollar        | AUD      | 0.65                    |
| Bahamas   | Dollar        | BSD      | 1.00                    |
| Barbados  | Dollar        | BBD      | 0.50                    |
| Bermuda   | Dollar        | BMD      | 1.00                    |
| Canada    | Dollar        | CAD      | 0.74                    |

If the system used only the currency name "Dollar" for lookups, it would create ambiguity and
potentially return incorrect exchange rates, leading to financial inaccuracies.

#### Solution: Country-Currency Description

The Treasury API provides a `country_currency_desc` field that combines the country name with the
currency name (e.g., "Australia-Dollar", "Canada-Dollar", "Brazil-Real"). This approach:

1. **Eliminates Ambiguity**: Each country-currency combination is unique
2. **Ensures Accuracy**: Guarantees the correct exchange rate for the intended currency
3. **Maintains Traceability**: Clear identification of which country's currency is being used
4. **Follows Official Standards**: Aligns with U.S. Treasury's data structure

#### Implementation

```java
// Example: Converting to Australian Dollar
GET /api/v1/purchases/{id}?currency=Australia-Dollar

// Example: Converting to Canadian Dollar
GET /api/v1/purchases/{id}?currency=Canada-Dollar
```

The system queries the Treasury API using this compound identifier:

```
filter=country_currency_desc:eq:Australia-Dollar,record_date:lte:2023-01-15
```

#### Supported Currency Format

- **Format**: `{Country}-{Currency}`
- **Examples**:
    - `Brazil-Real`
    - `European Union-Euro`
    - `United Kingdom-Pound`
    - `Japan-Yen`
    - `Mexico-Peso`

#### Default Currency

The application uses `United States-Dollar` as the default currency for stored transactions. When
retrieving a purchase with `currency=United States-Dollar`, no conversion is performed (exchange
rate = 1.0).

## Features

### Core Functionality

- ✅ **Transaction Storage**: Persist purchase transactions with validation
- ✅ **Currency Conversion**: Convert purchases to any supported currency
- ✅ **Exchange Rate Lookup**: Automatic retrieval from Treasury API with 6-month lookback window
- ✅ **Data Validation**: Comprehensive input validation using Bean Validation
- ✅ **Error Handling**: Structured error responses with appropriate HTTP status codes

### Resilience & Performance

- ✅ **Circuit Breaker**: Prevents cascading failures when Treasury API is unavailable
- ✅ **Retry Mechanism**: Exponential backoff retry for transient failures
- ✅ **Caching**: 24-hour cache for exchange rates to reduce API calls
- ✅ **Rate Limiting**: Token bucket algorithm to prevent API abuse
- ✅ **Connection Pooling**: HikariCP for optimized database connections

### Observability

- ✅ **Health Checks**: Custom health indicator for Treasury API connectivity
- ✅ **Metrics**: Prometheus-compatible metrics for monitoring
- ✅ **Structured Logging**: JSON-formatted logs with correlation IDs
- ✅ **API Documentation**: Interactive Swagger UI for API exploration

## API Documentation

### Base URL

```
http://localhost:8080
```

### Endpoints

#### 1. Store Purchase Transaction

Creates a new purchase transaction in USD.

**Request:**

```http
POST /api/v1/purchases
Content-Type: application/json

{
  "description": "Coffee purchase at Starbucks",
  "transactionDate": "2023-01-15",
  "purchaseAmount": 4.50
}
```

**Response:** `201 Created`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "description": "Coffee purchase at Starbucks",
  "transactionDate": "2023-01-15",
  "purchaseAmount": 4.50
}
```

**Validation Rules:**

- `description`: Required, max 50 characters
- `transactionDate`: Required, valid date format (yyyy-MM-dd)
- `purchaseAmount`: Required, positive number, rounded to 2 decimal places

#### 2. Retrieve Purchase with Currency Conversion

Retrieves a purchase and converts it to the specified currency.

**Request:**

```http
GET /api/v1/purchases/{id}?currency=Brazil-Real
```

**Response:** `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "description": "Coffee purchase at Starbucks",
  "transactionDate": "2023-01-15",
  "purchaseAmount": 4.50,
  "targetCurrency": "Brazil-Real",
  "exchangeRate": 5.234,
  "convertedAmount": 23.55
}
```

**Query Parameters:**

- `currency`: Required, Country-Currency Description format (e.g., "Brazil-Real", "European
  Union-Euro")

**Error Responses:**

- `404 Not Found`: Purchase ID does not exist

```json
{
  "timestamp": "2023-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Purchase not found",
  "path": "/api/v1/purchases/invalid-id"
}
```

- `400 Bad Request`: Currency conversion not available

```json
{
  "timestamp": "2023-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Purchase cannot be converted to the target currency",
  "path": "/api/v1/purchases/550e8400-e29b-41d4-a716-446655440000"
}
```

### Interactive API Documentation

Access the Swagger UI for interactive API testing:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI specification available at:

```
http://localhost:8080/api-docs
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+ (or use included Maven wrapper)
- Docker (optional, for containerized deployment)

### Running Locally

#### Using Maven

```bash
# Clone the repository
git clone <repository-url>
cd felipe

# Run the application
./mvnw spring-boot:run
```

#### Using Docker

```bash
# Build the application
./mvnw clean package -DskipTests

# Build Docker image
docker build -t purchase-transaction-app .

# Run container
docker run -p 8080:8080 -p 9090:9090 purchase-transaction-app
```

#### Using Docker Compose

```bash
docker-compose up
```

### Verifying the Installation

```bash
# Check application health
curl http://localhost:9090/actuator/health

# Expected response:
# {"status":"UP","components":{"db":{"status":"UP"},"fiscalDataService":{"status":"UP"}}}
```

## Configuration

### Application Profiles

The application supports multiple profiles:

- **default**: Standard configuration
- **dev**: Development settings (detailed logging, H2 console enabled)
- **prod**: Production settings (optimized logging, security hardened)
- **test**: Test configuration (in-memory database, mocked external services)

Activate a profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Environment Variables

| Variable                         | Description                            | Default                                                           |
|----------------------------------|----------------------------------------|-------------------------------------------------------------------|
| `DB_URL`                         | Database JDBC URL                      | `jdbc:h2:file:./db/codingTest`                                    |
| `DB_USERNAME`                    | Database username                      | `felipepaltrinieri`                                               |
| `DB_PASSWORD`                    | Database password                      | `wex`                                                             |
| `SERVER_PORT`                    | Application HTTP port                  | `8080`                                                            |
| `MANAGEMENT_PORT`                | Actuator management port               | `9090`                                                            |
| `FISCAL_DATA_BASE_URL`           | Treasury API base URL                  | `https://api.fiscaldata.treasury.gov/services/api/fiscal_service` |
| `FISCAL_DATA_MAX_MONTH_AGO`      | Exchange rate lookback period (months) | `6`                                                               |
| `FISCAL_DATA_CONNECT_TIMEOUT_MS` | API connection timeout                 | `5000`                                                            |
| `FISCAL_DATA_READ_TIMEOUT_MS`    | API read timeout                       | `10000`                                                           |
| `RATE_LIMIT_CAPACITY`            | Rate limit bucket capacity             | `50`                                                              |
| `RATE_LIMIT_REFILL_TOKENS`       | Tokens refilled per period             | `50`                                                              |
| `RATE_LIMIT_REFILL_DURATION`     | Refill period (minutes)                | `1`                                                               |

### Treasury API Configuration

The application integrates with the U.S. Treasury Reporting Rates of Exchange API:

- **Endpoint**: `/v1/accounting/od/rates_of_exchange`
- **Documentation
  **: https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange
- **Rate Limit**: Configurable via application properties
- **Lookback Window**: 6 months (configurable)

## Deployment

### Production Considerations

1. **Database**: Replace H2 with a production-grade database (PostgreSQL, MySQL)
2. **Secrets Management**: Use environment variables or secret management services
3. **Logging**: Configure log aggregation (ELK Stack, CloudWatch)
4. **Monitoring**: Set up Prometheus + Grafana for metrics visualization
5. **Load Balancing**: Deploy behind a load balancer for high availability
6. **SSL/TLS**: Enable HTTPS for secure communication

### Docker Deployment

The included Dockerfile uses:

- **Base Image**: Eclipse Temurin 21 JRE Alpine (minimal footprint)
- **JVM Tuning**: G1GC with container-aware settings
- **Health Check**: Automated health monitoring
- **Graceful Shutdown**: 30-second timeout for in-flight requests

### Kubernetes Deployment

Example deployment manifest:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: purchase-transaction-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: purchase-transaction-app
  template:
    metadata:
      labels:
        app: purchase-transaction-app
    spec:
      containers:
        - name: app
          image: purchase-transaction-app:latest
          ports:
            - containerPort: 8080
            - containerPort: 9090
          env:
            - name: DB_URL
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: url
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 9090
            initialDelaySeconds: 40
            periodSeconds: 30
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 9090
            initialDelaySeconds: 20
            periodSeconds: 10
```

## Monitoring & Observability

### Health Checks

**Endpoint**: `http://localhost:9090/actuator/health`

Monitors:

- Database connectivity
- Treasury API availability
- Circuit breaker status

### Metrics

**Endpoint**: `http://localhost:9090/actuator/prometheus`

Key metrics:

- `fiscal_data_api_latency`: Treasury API response times
- `http_server_requests`: HTTP request metrics
- `resilience4j_circuitbreaker_state`: Circuit breaker state
- `cache_gets`: Cache hit/miss rates

### Logging

Structured JSON logging with correlation IDs for request tracing:

```json
{
  "timestamp": "2023-01-15T10:30:00.123Z",
  "level": "INFO",
  "correlationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "logger": "costa.paltrinieri.felipe.purchase.business.PurchaseBusinessImpl",
  "message": "Creating purchase: Coffee purchase at Starbucks",
  "thread": "http-nio-8080-exec-1"
}
```

### Custom Health Indicator

The application includes a custom health indicator for the Treasury API:

```bash
curl http://localhost:9090/actuator/health/fiscalDataService
```

## Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=PurchaseBusinessImplTest
```

### Test Structure

- **Unit Tests**: Business logic and service layer tests
- **Integration Tests**: Repository and external API integration tests
- **Controller Tests**: REST endpoint tests with MockMvc

### Manual Testing with cURL

The `curl-tests/` directory contains shell scripts for manual API testing:

```bash
cd curl-tests

# Run all tests
./run-all-tests.sh

# Individual tests
./01-create-purchase.sh
./02-get-purchase.sh
./03-get-purchase-eur.sh
```

## Database

### H2 Console

Access the H2 database console (development only):

```
http://localhost:8080/h2-console
```

**Connection Settings:**

- JDBC URL: `jdbc:h2:file:./db/codingTest`
- Username: `felipepaltrinieri`
- Password: `wex`

### Schema Migration

Database schema is managed by Flyway. Migration scripts are located in:

```
src/main/resources/db/migration/
```

### Schema Overview

**Table: `purchases`**

| Column             | Type          | Constraints |
|--------------------|---------------|-------------|
| `id`               | VARCHAR(36)   | PRIMARY KEY |
| `description`      | VARCHAR(50)   | NOT NULL    |
| `transaction_date` | DATE          | NOT NULL    |
| `purchase_amount`  | DECIMAL(19,2) | NOT NULL    |
| `created_at`       | TIMESTAMP     | NOT NULL    |
| `updated_at`       | TIMESTAMP     | NOT NULL    |

## License

This project is proprietary software developed for coding assessment purposes.

## Support

For issues, questions, or contributions, please contact the development team.

---

**Built with ❤️ using Spring Boot and Hexagonal Architecture**
