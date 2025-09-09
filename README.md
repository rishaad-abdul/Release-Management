# Release Management & Deployment Tracker

A comprehensive Spring Boot application for managing software releases, tracking deployments, and handling rollbacks across different environments.

## 🔹 Features

### User Management (Core Java + OOP)
- **Roles**: Admin, Developer, QA
- **CRUD Operations**: Create, Read, Update, Delete users
- **Exception Handling**: Invalid inputs, duplicate users
- **In-memory collections** with database persistence

### Release Tracking (Spring Boot + REST APIs)
- Create releases with version numbers, descriptions, and owners
- **Environment Progression**: Dev → QA → UAT → Prod
- **Promotion APIs**: Move releases between environments
- **Rollback Support**: Revert releases to previous environments

### Deployment Logs (SQL + Collections)
- Store deployment history in database with releaseId, environment, timestamp
- Retrieve deployment history for specific releases
- Track success/failure status and deployment notes
- Filter by environment, date range, and success status

### Web UI (HTML/CSS/JavaScript)
- User-friendly interface for managing users and releases
- View deployment history with filtering options
- Responsive design with modern styling

### Unit Tests (JUnit + Mockito)
- Comprehensive test coverage for services and controllers
- Mock database layers in tests
- Exception handling validation

## 🛠 Technology Stack

- **Backend**: Spring Boot 2.7.18, Java 11
- **Database**: H2 (in-memory for development)
- **ORM**: Spring Data JPA, Hibernate
- **Testing**: JUnit 5, Mockito
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Build Tool**: Maven
- **API**: RESTful services with JSON

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/releasetracker/
│   │   ├── controller/          # REST Controllers
│   │   ├── service/            # Business Logic
│   │   ├── repository/         # Data Access Layer
│   │   ├── model/              # Entity Classes
│   │   └── exception/          # Custom Exceptions
│   └── resources/
│       ├── static/             # Web UI Files
│       └── application.properties
└── test/
    ├── java/com/releasetracker/
    │   ├── service/            # Service Tests
    │   ├── controller/         # Controller Tests
    │   └── model/              # Model Tests
    └── resources/
        └── application-test.properties
```

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Installation & Running

1. **Clone or extract the project**
   ```bash
   cd TestProj
   ```

2. **Build the application**
   ```bash
   mvn clean compile
   ```

3. **Run tests**
   ```bash
   mvn test
   ```

4. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Web UI: http://localhost:8080
   - H2 Database Console: http://localhost:8080/h2-console
   - API Base URL: http://localhost:8080/api

## 📋 API Endpoints

### User Management
- `GET /api/users` - Get all users
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/role/{role}` - Get users by role
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Release Management
- `GET /api/releases` - Get all releases
- `POST /api/releases` - Create release
- `GET /api/releases/{id}` - Get release by ID
- `GET /api/releases/version/{version}` - Get release by version
- `GET /api/releases/environment/{env}` - Get releases by environment
- `POST /api/releases/{id}/promote?promotedById={userId}` - Promote release
- `POST /api/releases/{id}/rollback?rolledBackById={userId}` - Rollback release
- `PUT /api/releases/{id}` - Update release
- `DELETE /api/releases/{id}` - Delete release

### Deployment Logs
- `GET /api/deployment-logs` - Get all deployment logs
- `POST /api/deployment-logs` - Create deployment log
- `POST /api/deployment-logs/log` - Log deployment with parameters
- `GET /api/deployment-logs/release/{releaseId}` - Get logs by release
- `GET /api/deployment-logs/environment/{env}` - Get logs by environment
- `GET /api/deployment-logs/user/{userId}` - Get logs by user
- `GET /api/deployment-logs/failed` - Get failed deployments
- `GET /api/deployment-logs/successful` - Get successful deployments

## 🎯 Usage Examples

### 1. Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john@example.com", 
    "fullName": "John Doe",
    "role": "DEVELOPER"
  }'
```

### 2. Create a Release
```bash
curl -X POST http://localhost:8080/api/releases \
  -H "Content-Type: application/json" \
  -d '{
    "versionNumber": "1.0.0",
    "description": "Initial release",
    "owner": {"id": 1}
  }'
```

### 3. Promote a Release
```bash
curl -X POST "http://localhost:8080/api/releases/1/promote?promotedById=1"
```

## 🧪 Running Tests

### Run all tests
```bash
mvn test
```

### Run specific test class
```bash
mvn test -Dtest=UserServiceTest
```

### Run tests with coverage
```bash
mvn test jacoco:report
```

## 🏗 Key Design Patterns & Concepts

### Object-Oriented Programming
- **Encapsulation**: Private fields with public getters/setters
- **Inheritance**: Service layer hierarchy
- **Polymorphism**: Exception handling with different exception types
- **Abstraction**: Repository interfaces

### Exception Handling
- Custom exceptions for business logic violations
- Global exception handling in controllers
- Proper HTTP status codes for different error scenarios

### Collections Usage
- ArrayList for dynamic user/release lists
- HashMap-like behavior through JPA repositories
- Stream API for filtering and processing

### Database Integration
- JPA/Hibernate for object-relational mapping
- Repository pattern for data access
- Transaction management
- Database relationships (One-to-Many, Many-to-One)

## 🔒 Security Considerations

- Input validation with Bean Validation annotations
- SQL injection prevention through JPA
- No hardcoded credentials
- Proper error handling without information leakage

## 🌟 Future Enhancements

- User authentication and authorization
- Email notifications for deployments
- Integration with CI/CD pipelines
- Audit trails for all operations
- Dashboard with metrics and charts
- Docker containerization
- Production database configuration (PostgreSQL/MySQL)

## 📝 License

This project is created for educational purposes to demonstrate Spring Boot, Java OOP concepts, testing, and web development skills.