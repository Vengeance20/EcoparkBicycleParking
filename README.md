# Ecopark Bicycle Parking System

Welcome to the **Ecopark Bicycle Parking System**, developed by **Group 12**. This project is a smart bicycle rental and reservation management system with a fully separated architecture consisting of a Spring Boot REST API backend and a static web frontend.

---

# Technologies Used

## Backend

* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 3.5.11
* **Security:** Spring Security & Stateless JSON Web Token (JJWT 0.12.3)
* **ORM & Data Access:** Spring Data JPA / Hibernate
* **Database:** MySQL Server
* **Build Tool:** Apache Maven

## Frontend

* **Platform:** HTML5, Vanilla JavaScript (ES6)
* **CSS Framework:** TailwindCSS (CDN)
* **Map Integration:** Leaflet.js (GPS-based station search)
* **Charts & Analytics:** Chart.js (Admin Dashboard)

---

# Prerequisites

Before running the project, ensure the following software is installed on your machine:

1. Java Development Kit (JDK) 21 or higher
2. Apache Maven 3.8+
3. MySQL Server 8.0+
4. Modern Web Browser (Chrome, Edge, Brave, etc.)
5. Recommended IDEs:

   * IntelliJ IDEA
   * Eclipse

Verify your installations:

```bash
java -version
mvn -version
```

---

# Installation and Setup

## Step 1: Set Up the Database Using XAMPP

### Install XAMPP

Download and install XAMPP from:

https://www.apachefriends.org/

### Start the Database Server

1. Open **XAMPP Control Panel**.
2. Start the **MySQL** service.
3. Ensure the status indicator turns green.

### Create the Database

1. Open phpMyAdmin:

```text
http://localhost/phpmyadmin
```

2. Click **New** in the left sidebar.
3. Create a database named:

```text
ecopark_bike_1
```

4. Select:

```text
utf8mb4_unicode_ci
```

as the collation.

Alternatively, execute:

```sql
CREATE DATABASE ecopark_bike_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

5. Add dummy SQL queries from the file
```text
Dummy.sql
```

for testing (All users have the same password 123456 for logging in)

### Configure Spring Boot

Open:

```text
src/main/resources/application.properties
```

and update the database configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecopark_bike_1?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update 
spring.jpa.show-sql=true
```

> By default, XAMPP uses the username `root` with an empty password.

### Verify Database Connection

After starting the Spring Boot application, Hibernate will automatically create the required tables inside the `ecopark_bike_1` database.

You can verify the tables by opening:

```text
http://localhost/phpmyadmin
```

and selecting the `ecopark_bike_1` database.


---

# Running the Backend (Spring Boot)

## Option 1: Using Maven

Open a terminal in the project root directory (where `pom.xml` is located).

### Build the project

```bash
mvn clean install
```

### Start the Spring Boot application

```bash
mvn spring-boot:run
```

---

## Option 2: Using IntelliJ IDEA

1. Open IntelliJ IDEA.
2. Select **Open** and choose the backend project folder.
3. Wait for Maven to download all dependencies.
4. Locate:

```text
EcoparkBicycleParkingApplication.java
```

5. Click the **Run** button.


---

# Running the Frontend

Open this in your browser:

```text
http://localhost:8080/RegisterLoginPage.html
```

This serves as the main entry page for user registration and login.

---

# Troubleshooting

## Database Connection Error

Verify:

* MySQL Server is running.
* Database `ecopark_bike_db` exists.
* Username and password are correct.


---

## Maven Dependency Issues

Clear and rebuild dependencies:

```bash
mvn clean
mvn dependency:purge-local-repository
mvn install
```

---

## Java Version Mismatch

Verify the active Java version:

```bash
java -version
```

Ensure Java 21 is being used.

---

# Team

**Group 12**

[Bui Cong Minh](https://github.com/Vengeance20)

Special thanks:

[Tran Ngoc Minh](https://github.com/WiuHz)

[Pham Quang Minh](https://github.com/Hiiamming)

[Bui Nam Khanh](https://github.com/Kain1k)

[Dang Duc Thinh](https://github.com/ThinhDang22)

Ph.D Bui Thi Mai Anh for guidance from the beginning of the course

