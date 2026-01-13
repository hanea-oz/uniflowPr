# University Management System (UNIFLOW)

A comprehensive web application for managing university operations including students, teachers, modules, timetables, and student evaluation.

## 🎓 Features

### Admin Features
- **User Management**: Create, update, and delete students and teachers
- **Group Management**: Organize students into groups by program and level
- **Module Management**: Define modules with responsible teachers
- **Module Assignment**: Assign modules to specific groups
- **Room Management**: Manage classroom inventory
- **Timetable Management**: Create weekly recurring timetables
  - Define timeslots (day, start time, end time)
  - Create sessions with conflict detection
  - Automatic validation (no double-booking of rooms, teachers, or groups)
- **Complete CRUD Operations**: Full control over all entities

### Teacher Features
- **Dashboard**: View assigned modules and upcoming sessions
- **Module View**: Access all modules where they are responsible
- **Timetable View**: Personal weekly schedule
- **Grade Management**: 
  - Select module
  - View enrolled students
  - Create/update grades (Lab, Exam, Project, Participation, Final)
  - Provide feedback

### Student Features
- **Dashboard**: Personal profile and group information
- **Timetable View**: Weekly class schedule based on group
- **Grade View**: View all grades and feedback from teachers

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.1
- **Web Framework**: Spring MVC + Thymeleaf
- **Data Access**: Spring Data JPA
- **Database**: PostgreSQL (Railway)
- **Security**: Spring Security with role-based access control
- **Build Tool**: Maven
- **UI Framework**: Bootstrap 5 + Bootstrap Icons

## 📊 Database Schema

The application uses a pre-existing PostgreSQL database with the following key tables:

- `users` - Authentication and role management
- `students` - Student profiles (extends users)
- `teachers` - Teacher profiles (extends users)
- `groups` - Student groupings
- `modules` - Course modules with responsible teachers
- `module_groups` - Module-to-group assignments
- `rooms` - Classroom inventory
- `timeslots` - Weekly recurring time slots
- `sessions` - Timetable entries (module + teacher + group + room + timeslot)
- `enrollments` - Student-module enrollments
- `grades` - Student evaluations

**Important**: The database schema is pre-created. The application uses `spring.jpa.hibernate.ddl-auto=none` to prevent schema generation.

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database (Railway or local)

### Configuration

1. **Clone the repository** (or use your existing project)

2. **Configure database connection**

   Edit `src/main/resources/application.properties`:

   ```properties
   # For Railway deployment
   spring.datasource.url=jdbc:postgresql://postgres.railway.internal:5432/railway
   spring.datasource.username=postgres
   spring.datasource.password=yMyLJbpsZnERBgzjyCGorMlUlyKDZDEJ
   ```

   For local development, edit `src/main/resources/application-local.properties`:

   ```properties
   # For local development using Railway public URL
   spring.datasource.url=jdbc:postgresql://hopper.proxy.rlwy.net:48322/railway
   spring.datasource.username=postgres
   spring.datasource.password=yMyLJbpsZnERBgzjyCGorMlUlyKDZDEJ
   ```

### Running Locally

1. **Build the project**:
   ```bash
   mvn clean package
   ```

2. **Run with local profile**:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

   Or run the JAR:
   ```bash
   java -jar target/university-management-1.0.0.jar --spring.profiles.active=local
   ```

3. **Access the application**:
   - Open browser: http://localhost:8080
   - Login page will be displayed

### Demo Accounts

The application comes with pre-seeded demo data:

| Role | Email | Password |
|------|-------|----------|
| **Admin** | admin@uniflow.com | admin123 |
| **Teacher** | teacher@uniflow.com | teacher123 |
| **Student** | student@uniflow.com | student123 |

## 🔐 Security

- **Form-based authentication**: Custom login page at `/login`
- **Role-based access control**: 
  - `ADMIN`: Full CRUD access to all resources
  - `TEACHER`: View own modules/sessions, manage grades for own modules
  - `STUDENT`: View own profile, timetable, and grades
- **Password encryption**: BCrypt encoding
- **Session management**: Automatic logout, session invalidation

## 📁 Project Structure

```
src/main/java/com/uniflow/
├── config/              # Security & initialization configuration
│   ├── SecurityConfig.java
│   └── DataInitializer.java
├── controller/          # MVC Controllers
│   ├── HomeController.java
│   ├── AdminController.java
│   ├── AdminStudentController.java
│   ├── AdminTeacherController.java
│   ├── AdminGroupController.java
│   ├── AdminModuleController.java
│   ├── AdminRoomController.java
│   ├── AdminTimeslotController.java
│   ├── AdminSessionController.java
│   ├── TeacherController.java
│   ├── TeacherGradeController.java
│   └── StudentController.java
├── model/               # Domain entities
│   ├── enums/
│   │   ├── RoleEnum.java
│   │   └── SessionTypeEnum.java
│   ├── User.java
│   ├── Student.java
│   ├── Teacher.java
│   ├── Group.java
│   ├── Module.java
│   ├── Room.java
│   ├── Timeslot.java
│   ├── Session.java
│   ├── Enrollment.java
│   └── Grade.java
├── repository/          # Spring Data JPA repositories
├── service/             # Business logic layer
└── UniversityManagementApplication.java

src/main/resources/
├── templates/           # Thymeleaf views
│   ├── layout.html
│   ├── login.html
│   ├── admin/
│   ├── teacher/
│   └── student/
├── application.properties
└── application-local.properties
```

## 🎯 Key Features Explained

### Weekly Recurring Timetable

Unlike date-based timetables, this system uses **weekly recurring schedules**:

- **Timeslots** define recurring time windows: `(day_of_week, start_time, end_time)`
- **Sessions** reference timeslots and repeat every week
- **Conflict Detection**: Database constraints prevent:
  - Same room booked twice at the same timeslot
  - Teacher teaching two sessions at the same timeslot
  - Group attending two sessions at the same timeslot

### Enrollment Model

- **Simplified**: Only stores `(student_id, module_id)`
- **Group derivation**: Student's group is obtained from `students.group_id`
- **Consistency**: Avoids storing redundant group information in enrollments

### Grade Management

- **One-to-one** with enrollment
- **Multiple components**: Lab, Exam, Project, Participation, Final grade
- **Teacher tracking**: Records which teacher gave the grade
- **Validation**: Grades must be between 0 and 20

## 🐛 Troubleshooting

### Database Connection Issues

1. **Check Railway database status**: Ensure Railway PostgreSQL is running
2. **Verify credentials**: Confirm DATABASE_URL, username, and password
3. **Network connectivity**: Test connection using psql:
   ```bash
   psql -h hopper.proxy.rlwy.net -U postgres -p 48322 -d railway
   ```

### Schema Mismatch

If you see JPA errors about missing columns/tables:

1. **Verify schema exists**: The database must have all tables pre-created
2. **Check ddl-auto setting**: Must be `none` (no auto schema generation)
3. **Review entity mappings**: Ensure JPA annotations match database schema

### Login Issues

1. **Check DataInitializer**: Ensure it ran successfully (check logs)
2. **Verify users exist**: Query database: `SELECT * FROM users;`
3. **Clear browser cache**: Remove cookies and try again

## 📝 Development Notes

### Adding New Features

1. **Entity changes**: DO NOT modify entities without corresponding schema changes
2. **Schema updates**: All schema changes must be done manually in PostgreSQL
3. **Testing**: Always test with real database, not H2/in-memory

### Best Practices

- **Transaction management**: Use `@Transactional` for multi-step operations
- **Error handling**: Catch constraint violations and show user-friendly messages
- **Lazy loading**: Be careful with lazy-loaded associations in views
- **Security**: Always verify user permissions in controllers

## 🚢 Deployment to Railway

1. **Prepare for deployment**:
   ```bash
   mvn clean package -DskipTests
   ```

2. **Create Railway project** (if not exists):
   - Go to https://railway.app
   - Create new project
   - Add PostgreSQL service
   - Note the internal DATABASE_URL

3. **Deploy application**:
   - Push to GitHub repository
   - Connect Railway to GitHub repo
   - Configure environment variables
   - Railway will auto-deploy on push

4. **Environment Variables** (Railway):
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://postgres.railway.internal:5432/railway
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=<your-password>
   SPRING_JPA_HIBERNATE_DDL_AUTO=none
   ```

## 📄 License

This project is created for educational purposes.

## 👥 Support

For issues or questions:
- Review logs in `application.log` or console output
- Check database connectivity
- Verify schema matches entity definitions

---

**Built with ❤️ using Spring Boot & Thymeleaf**
