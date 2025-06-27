# User posting Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern, RESTful web service built with Spring Boot for managing users posting operations, including user management and post publishing functionality.

## 🚀 Features

- **User Management**: Complete CRUD operations for user accounts
- **Post Management**: Create, read, update, and delete news posts
- **RESTful API**: Clean, well-documented REST endpoints
- **Data Validation**: Comprehensive input validation with custom error messages
- **Interactive Documentation**: Swagger/OpenAPI 3.0 integration
- **Database Integration**: JPA/Hibernate with H2 database support
- **Test Coverage**: Comprehensive unit and integration tests
- **Error Handling**: Robust exception handling with meaningful responses

## 🛠️ Technologies Used

### Core Framework
- **Spring Boot 3.5.3** - Main application framework
- **Spring Web MVC** - REST controller implementation
- **Spring Data JPA** - Data persistence layer
- **Spring Boot Validation** - Input validation

### Database
- **H2 Database** - In-memory database for development and testing
- **Hibernate** - ORM for database operations

### Development Tools
- **Lombok** - Reduces boilerplate code
- **SpringDoc OpenAPI** - API documentation and Swagger UI
- **JaCoCo** - Code coverage analysis

### Testing
- **Spring Boot Test** - Integration testing framework
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions

## 📋 Prerequisites

Before running this application, make sure you have:

- **Java 17** or higher installed
- **Maven 3.6+** installed
- **Git** for version control (optional)

## 🚀 Getting Started

### 1. Clone the Repository

```shell script
git clone https://github.com/Eferigho/1971technology-Assignment-Code.git
cd postingApp
```


### 2. Build the Project

```shell script
mvn clean compile
```


### 3. Run Tests

```shell script
mvn test
```


### 4. Start the Application

```shell script
mvn spring-boot:run
```


The application will start on `http://localhost:8080`

## 📖 API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **H2 Console**: http://localhost:8080/h2-console (if enabled)

## 🔗 API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/users` | Create a new user |
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `PUT` | `/api/users/{id}` | Update user |
| `DELETE` | `/api/users/{id}` | Delete user |

### Post Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/users/{userId}/posts` | Create a new post for user |
| `GET` | `/api/posts` | Get all posts |
| `GET` | `/api/posts/{id}` | Get post by ID |
| `GET` | `/api/users/{userId}/posts` | Get all posts by user |
| `PUT` | `/api/posts/{id}` | Update post |
| `DELETE` | `/api/posts/{id}` | Delete post |

## 📝 Request/Response Examples

### Create User

**Request:**
```shell script
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com"
  }'
```


**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```


### Create Post

**Request:**
```shell script
curl -X POST http://localhost:8080/api/users/1/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Breaking News",
    "content": "This is the content of the breaking news article."
  }'
```


**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "title": "Breaking News",
  "content": "This is the content of the breaking news article."
}
```


## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/agency/
│   │       ├── PostingApplication.java           # Main application class
│   │       ├── controller/                    # REST controllers
│   │       │   ├── UserController.java
│   │       │   └── PostController.java
│   │       ├── service/                       # Business logic layer
│   │       │   ├── UserService.java
│   │       │   ├── UserServiceImpl.java
│   │       │   ├── PostService.java
│   │       │   └── PostServiceImpl.java
│   │       ├── repository/                    # Data access layer
│   │       │   ├── UserRepository.java
│   │       │   └── PostRepository.java
│   │       └── data/
│   │           ├── entity/                    # JPA entities
│   │           │   ├── User.java
│   │           │   └── Post.java
│   │           └── dto/                       # Data transfer objects
│   │               ├── UserRequestDto.java
│   │               ├── UserResponseDto.java
│   │               ├── PostRequestDto.java
│   │               └── PostResponseDto.java
│   └── resources/
│       ├── application.properties             # Application configuration
│       └── application-test.properties        # Test configuration
└── test/
    └── java/
        └── com/agency/
            ├── service/                       # Service layer tests
            │   ├── UserServiceImplUnitTest.java
            │   ├── UserServiceImplIntegrationTest.java
            │   ├── PostServiceImplUnitTest.java
            │   └── PostServiceImplIntegrationTest.java
            └── PostingApplicationTests.java      # Application context test
```


## ⚙️ Configuration

### Database Configuration

The application uses H2 in-memory database by default. To configure:

```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:newsdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console (for development)
spring.h2.console.enabled=true
```


### Validation Configuration

The application includes comprehensive validation:

- **Email Validation**: Ensures proper email format
- **Required Fields**: Validates mandatory fields
- **Custom Messages**: Provides user-friendly error messages

## 🧪 Testing

The project includes comprehensive testing:

### Run All Tests
```shell script
mvn test
```


### Run Tests with Coverage
```shell script
mvn clean test jacoco:report
```


### View Coverage Report
Open `target/site/jacoco/index.html` in your browser.

### Test Categories

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test complete workflows with database
- **Application Tests**: Test application context loading

## 📊 Code Coverage

The project maintains high code coverage standards:

- **Minimum Coverage**: 80% line coverage
- **Service Layer**: 90%+ coverage target
- **Controller Layer**: 85%+ coverage target

## 🔧 Development

### Adding New Features

1. **Create Entity**: Add JPA entity in `data/entity/`
2. **Create DTOs**: Add request/response DTOs in `data/dto/`
3. **Create Repository**: Add repository interface in `repository/`
4. **Create Service**: Add service interface and implementation in `service/`
5. **Create Controller**: Add REST controller in `controller/`
6. **Add Tests**: Create unit and integration tests

### Code Style

The project follows standard Java conventions:

- Use Lombok annotations to reduce boilerplate
- Follow RESTful API design principles
- Implement proper exception handling
- Write comprehensive tests

## 🚀 Deployment

### Build for Production

```shell script
mvn clean package -DskipTests
```


### Run JAR File

```shell script
java -jar target/news-0.0.1-SNAPSHOT.jar
```


### Docker Deployment (Optional)

Create a `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/news-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```


Build and run:

```shell script
docker build -t news-agency .
docker run -p 8080:8080 news-agency
```


## 🐛 Troubleshooting

### Common Issues

1. **Port Already in Use**
```shell script
# Change port in application.properties
   server.port=8081
```


2. **Database Connection Issues**
    - Verify H2 configuration in `application.properties`
    - Check H2 console at `/h2-console`

3. **Lombok Not Working**
    - Enable annotation processing in your IDE
    - Verify Lombok plugin is installed

### Debug Mode

Run with debug logging:

```shell script
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.agency=DEBUG"
```


## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

If you have any questions or issues, please:

1. Check the [troubleshooting section](#-troubleshooting)
2. Review the [API documentation](#-api-documentation)
3. Open an issue on GitHub
4. Contact the development team

---

**Happy AI Coding! 🎉**