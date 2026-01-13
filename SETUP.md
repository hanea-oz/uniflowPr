# Development Environment Setup

## Environment Variables for Development

Create a `.env` file in the project root or set these environment variables:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/university_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Application Configuration
DDL_AUTO=update
LOG_LEVEL=DEBUG
SHOW_SQL=true
FORMAT_SQL=true
THYMELEAF_CACHE=false
PORT=8080
```

## Local Development Configuration

Copy `application-example.properties` to `application-local.properties` and configure:

```properties
# Local Database
spring.datasource.url=jdbc:postgresql://localhost:5432/university_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

## Production Environment

Set these environment variables in your production environment:

```bash
DATABASE_URL=jdbc:postgresql://your-production-host:5432/your-db
DB_USERNAME=your-production-username
DB_PASSWORD=your-production-password
DDL_AUTO=none
LOG_LEVEL=INFO
SHOW_SQL=false
PORT=8080
```