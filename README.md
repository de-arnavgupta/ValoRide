# ValoRide 🚗

A modern, scalable ride-sharing backend API built with Spring Boot. ValoRide provides a complete backend solution for Uber-like ride-sharing applications.

## Features ✨

### User Management
- **User Authentication** - Register, login, logout with JWT-based authentication
- **Role-based Access Control** - Separate roles for Riders, Drivers, and Admins
- **Token Management** - Access and refresh token support with logout from all devices

### Ride Management
- **Ride Requests** - Riders can request rides with pickup and drop-off locations
- **Fare Estimation** - Get fare estimates before booking
- **Ride Lifecycle** - Full ride flow: Request → Accept → Arrive → Start → Complete
- **Ride Cancellation** - Both riders and drivers can cancel rides with reason tracking
- **Ride Rating** - Riders can rate completed rides
- **Ride History** - View past rides for both riders and drivers

### Driver Management
- **Driver Registration** - Drivers can register with vehicle details
- **Location Tracking** - Real-time driver location updates
- **Availability Toggle** - Drivers can go online/offline
- **Nearby Drivers** - Find available drivers near a location
- **Admin Approval** - Driver approval workflow for new registrations

### Payment Integration
- **Stripe Integration** - Secure payment processing with Stripe
- **Webhook Support** - Handle Stripe payment events
- **Multiple Payment Methods** - Support for various payment options

### Admin Features
- **User Management** - View and manage all users
- **Driver Approval** - Approve or reject driver registrations
- **Analytics Dashboard** - Platform statistics and insights
- **Revenue Reports** - Track platform revenue over time
- **Driver Performance** - Monitor top-performing drivers

### Additional Features
- **Email Notifications** - Send notifications via email
- **Rate Limiting** - API rate limiting with Bucket4j
- **API Documentation** - Interactive Swagger UI documentation
- **Redis Caching** - Fast data caching with Redis
- **Kafka Support** - Event-driven architecture support (optional)

## Tech Stack 🛠️

| Category | Technology |
|----------|------------|
| **Framework** | Spring Boot 3.4.1 |
| **Language** | Java 21 |
| **Database** | PostgreSQL |
| **Caching** | Redis |
| **Security** | Spring Security + JWT |
| **Payment** | Stripe |
| **Documentation** | SpringDoc OpenAPI (Swagger) |
| **Messaging** | Apache Kafka (optional) |
| **Object Mapping** | MapStruct |
| **Rate Limiting** | Bucket4j |
| **Build Tool** | Maven |

## Prerequisites 📋

- **Java 21** or higher
- **Maven 3.8+**
- **PostgreSQL 14+**
- **Redis 7+**
- **Apache Kafka** (optional) - Required only for real-time event streaming features like ride status notifications and driver location broadcasts. The application works fully without Kafka.

## Installation 🚀

### 1. Clone the repository

```bash
git clone https://github.com/de-arnavgupta/ValoRide.git
cd ValoRide
```

### 2. Configure environment variables

Create a `.env` file in the project root with the following variables:

```env
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/valoride
DATABASE_USER=your_username
DATABASE_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT
JWT_SECRET=your-secure-jwt-secret-key-minimum-256-bits

# Stripe
STRIPE_API_KEY=sk_test_xxx
STRIPE_WEBHOOK_SECRET=whsec_xxx

# Email (optional)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
NOTIFICATION_EMAIL_ENABLED=false

# Kafka (optional)
KAFKA_SERVERS=localhost:9092
```

### 3. Create the database

```bash
createdb valoride
```

### 4. Build and run

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080/api`

## API Documentation 📖

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api/api-docs

### API Endpoints Overview

| Endpoint | Description |
|----------|-------------|
| `POST /api/v1/auth/register` | Register a new user |
| `POST /api/v1/auth/login` | User login |
| `POST /api/v1/auth/refresh` | Refresh access token |
| `POST /api/v1/auth/logout` | User logout |
| `POST /api/v1/rides/request` | Request a new ride |
| `POST /api/v1/rides/estimate` | Get fare estimate |
| `POST /api/v1/rides/{id}/accept` | Driver accepts ride |
| `POST /api/v1/rides/{id}/start` | Start the ride |
| `POST /api/v1/rides/{id}/complete` | Complete the ride |
| `GET /api/v1/rides/history` | Get ride history |
| `POST /api/v1/drivers/register` | Register as driver |
| `PUT /api/v1/drivers/location` | Update driver location |
| `PUT /api/v1/drivers/availability` | Toggle availability |
| `POST /api/v1/drivers/nearby` | Find nearby drivers |
| `GET /api/v1/admin/users` | List all users (Admin) |
| `POST /api/v1/admin/drivers/{id}/approve` | Approve driver (Admin) |
| `GET /api/v1/analytics/dashboard` | Dashboard stats (Admin) |
| `GET /api/v1/analytics/revenue` | Revenue report (Admin) |

## Project Structure 📁

```
src/main/java/com/arnavgpt/valoride/
├── ValoRideApplication.java    # Main application entry point
├── admin/                      # Admin management module
│   ├── controller/
│   └── service/
├── analytics/                  # Analytics and reporting
│   ├── controller/
│   ├── dto/
│   ├── repository/
│   └── service/
├── common/                     # Shared components
│   └── dto/
├── config/                     # Application configuration
├── driver/                     # Driver management module
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── exception/                  # Exception handling
├── notification/               # Notification services
├── payment/                    # Payment processing (Stripe)
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── ride/                       # Ride management module
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
└── user/                       # User management module
    ├── controller/
    ├── dto/
    ├── entity/
    ├── repository/
    └── service/
```

## Configuration ⚙️

Key configuration options in `application.properties`:

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Server port | 8080 |
| `server.servlet.context-path` | API base path | /api |
| `jwt.access-token-expiration` | Access token TTL (ms) | 900000 (15 min) |
| `jwt.refresh-token-expiration` | Refresh token TTL (ms) | 604800000 (7 days) |
| `rate-limit.requests-per-minute` | API rate limit | 100 |
| `spring.kafka.enabled` | Enable Kafka | false |

## Running Tests 🧪

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## Contributing 🤝

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License 📄

This project is open source and available under the [MIT License](LICENSE).

## Author ✍️

**Arnav Gupta** - [GitHub](https://github.com/de-arnavgupta)