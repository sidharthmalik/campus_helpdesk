# Campus Complaint & Helpdesk Management System

A web application where students register, log in, submit complaints about campus facilities and track their status, while administrators review every complaint, set its priority, update its status and view summary statistics.

Built with **Java, Spring Boot, Thymeleaf, JDBC and MySQL** as a college mini project (MCA, Chandigarh University).

---

## Features

**Student**
- Register and log in with a securely hashed password
- Submit a complaint by choosing a category and describing the problem
- Receive a unique complaint ID (for example `CMP-0001`)
- View only your own complaints with priority, status and date

**Administrator**
- Separate admin login
- View all complaints with the student's name and department
- Update the priority (Low / Medium / High) and status (Pending / In Progress / Resolved) of any complaint
- Dashboard counters: total, pending, in progress and resolved
- Category-wise complaint counts

**Complaint categories:** Classroom, Laboratory, Hostel, Library, Infrastructure, Canteen, Transport, Other

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 (Spring MVC) |
| Views | Thymeleaf + Bootstrap 5.3 |
| Data access | Spring JDBC (`JdbcTemplate`) + MySQL Connector/J |
| Database | MySQL |
| Password hashing | BCrypt (`spring-security-crypto`) |
| Build tool | Maven |
| Server | Embedded Apache Tomcat |

---

## Architecture

A three-tier client-server design:

```
Browser (HTML, CSS, Bootstrap, Thymeleaf pages)
        |   HTTP request / response
Spring Boot on Tomcat (Controllers, AuthInterceptor, HelpdeskRepository)
        |   JDBC (parameterized SQL)
MySQL (students, admin, complaints)
```

Request flow for submitting a complaint: the browser sends a POST request, the `AuthInterceptor` checks the session, the controller validates the input, the repository inserts the row through JDBC, and the student is redirected to the dashboard with the new complaint ID.

---

## Database Schema

**students**

| Field | Type | Constraint |
|---|---|---|
| student_id | INT | Primary key, auto increment |
| name | VARCHAR(100) | Not null |
| email | VARCHAR(100) | Not null, unique |
| password_hash | VARCHAR(255) | Not null |
| department | VARCHAR(100) | |

**admin**

| Field | Type | Constraint |
|---|---|---|
| admin_id | INT | Primary key, auto increment |
| username | VARCHAR(100) | Not null, unique |
| password_hash | VARCHAR(255) | Not null |

**complaints**

| Field | Type | Constraint |
|---|---|---|
| complaint_id | INT | Primary key, auto increment |
| student_id | INT | Foreign key to students, on delete cascade |
| category | VARCHAR(100) | Not null |
| description | TEXT | Not null |
| priority | VARCHAR(20) | Default `Medium` |
| status | VARCHAR(30) | Default `Pending` |
| created_at | TIMESTAMP | Default current timestamp |

One student can submit many complaints; each complaint belongs to one student.

---

## Routes

| Route | Method | Purpose | Access |
|---|---|---|---|
| `/` | GET | Home page | Public |
| `/register` | GET, POST | Student registration | Public |
| `/login` | GET, POST | Student login | Public |
| `/logout` | GET | End the session | Any |
| `/dashboard` | GET | Student's own complaints | Student |
| `/complaint/new` | GET, POST | Submit a complaint | Student |
| `/admin/login` | GET, POST | Admin login | Public |
| `/admin` | GET | All complaints and statistics | Admin |
| `/admin/update/{id}` | POST | Update priority and status | Admin |

---

## Project Structure

```
campus_helpdesk/
├── pom.xml
├── database.sql
├── README.md
└── src/main/
    ├── java/com/campus/helpdesk/
    │   ├── HelpdeskApplication.java
    │   ├── AuthController.java
    │   ├── StudentController.java
    │   ├── AdminController.java
    │   ├── AuthInterceptor.java
    │   ├── WebConfig.java
    │   ├── GlobalModel.java
    │   ├── AdminSeeder.java
    │   ├── HelpdeskRepository.java
    │   ├── Models.java
    │   └── Options.java
    └── resources/
        ├── application.properties      (git-ignored, create it yourself)
        ├── static/css/style.css
        └── templates/                 (8 Thymeleaf pages)
```

---

## Getting Started

### Prerequisites
- JDK 17 or newer
- Apache Maven 3.x
- MySQL server (MySQL Workbench is optional)

### 1. Clone the repository
```bash
git clone https://github.com/sidharthmalik/campus_helpdesk.git
cd campus_helpdesk
```

### 2. Create the database
```bash
mysql -u root -p < database.sql
```

### 3. Configure the connection
Create `src/main/resources/application.properties` with your own MySQL login:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_helpdesk?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

server.port=8080
spring.thymeleaf.cache=false
spring.mvc.ignore-default-model-on-redirect=true

# Default admin, created automatically on first run
app.admin.username=admin
app.admin.password=admin123
```

This file is listed in `.gitignore`, so your password is never committed.

### 4. Run the server
```bash
mvn spring-boot:run
```
When you see `Started HelpdeskApplication`, open **http://localhost:8080**.

### Default admin

| Username | Password |
|---|---|
| `admin` | `admin123` |

The admin account is created automatically on the first run. Change `app.admin.username` and `app.admin.password` **before** the first run, and never keep the default in a real deployment.

---

## Usage Walkthrough

1. Click **Register**, create a student account and log in.
2. Click **+ New Complaint**, choose a category, describe the problem and submit. Note the ID (`CMP-0001`).
3. Log out, open `/admin/login` and sign in as admin.
4. Set the priority to High and the status to In Progress, then save.
5. Log back in as the student to see the updated status.

---

## Security Notes

| Threat | Measure |
|---|---|
| Password theft | BCrypt salted hashes |
| SQL injection | Parameterized queries only |
| Cross-site scripting | Thymeleaf escapes output by default |
| Unauthorized access | Session check in an interceptor; separate student and admin sessions |
| Session fixation | New session created at login, old one invalidated |
| Idle sessions | 30-minute timeout |
| Invalid input | Server-side validation against fixed option lists |

---

## Course Outcome Mapping

| CO | Implementation |
|---|---|
| CO1 | Three-tier architecture and the HTTP request-response cycle |
| CO2 | Spring MVC controllers and routes for student and admin operations |
| CO3 | MySQL schema and JDBC create, read and update operations |
| CO4 | `HttpSession` and interceptor-based authentication |
| CO5 | Framework comparison (Spring Boot, Flask, Django, Express) and scalability discussion |

---

## Limitations and Future Scope

**Current limitations**
- No CSRF tokens, because full Spring Security is not used
- Logout is a GET request
- No login attempt limits and no HTTPS in the development setup
- No screen for creating more administrators, and no search, filter or pagination

**Possible enhancements**
- Spring Security with CSRF protection and role-based access
- Email or SMS notifications on status changes
- Photo attachments for complaints
- Search, filtering, pagination and CSV or PDF export
- Department-wise routing and staff assignment
- Dashboard charts, Docker deployment and HTTPS

---

## Author

**Sidharth Malik**, MCA (Data Science), Chandigarh University
GitHub: [@sidharthmalik](https://github.com/sidharthmalik)
