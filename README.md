# MentorKE

MentorKE is a mentor–mentee matchmaking web application. It provides a server-side Java
backend (Servlets + JSP) and a PostgreSQL persistence layer for user management, sessions,
and audit logging.

Status: Completed — CRUD operations and documentation are included.

Key features
- User management with roles (admin / mentor / mentee)
- Session tracking and validation
- Audit logging
- Automatic DB and table initialization on first run

Tech stack
- Java 21
- Jakarta EE (Servlets + JSP)
- PostgreSQL 12+
- Maven 3.6+
- PostgreSQL JDBC Driver

Getting started

Prerequisites
- Java 21
- Maven 3.6+
- PostgreSQL running locally (recommended `localhost:5432`)

Build
```bash
cd /home/sharon/IdeaProjects/MentorKE
mvn clean package
```

Configuration
- Default DB settings are in [src/main/java/app/dbconnection/Connection.java](src/main/java/app/dbconnection/Connection.java).
- The app will attempt to create the application database and required tables automatically on startup, but schema changes to existing tables are applied manually.
- See [DATABASE_MIGRATION_GUIDE.md](DATABASE_MIGRATION_GUIDE.md) for the current SQL migration.

Deploy
- Deploy `target/MentorKE.war` to your servlet container (WildFly, Tomcat). Access the app at:

  http://localhost:8080/MentorKE

Documentation
- Start with IMPLEMENTATION_SUMMARY.md for architecture and setup details.
- See [DATABASE_MIGRATION_GUIDE.md](DATABASE_MIGRATION_GUIDE.md) and DEPLOYMENT_CHECKLIST.md for deeper guidance.

Suggested next steps
- Use BCrypt for password hashing.
- Add HikariCP for connection pooling.
- Add unit and integration tests (JUnit + Testcontainers).

Contributing
- Fork, make changes, and open a pull request.

Updated: May 28, 2026


