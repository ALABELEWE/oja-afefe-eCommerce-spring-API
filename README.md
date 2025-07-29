# 🛒 Oja-Afefe eCommerce API

A modern and scalable RESTful API for an eCommerce platform, built with Java Spring Boot. Oja-Afefe provides endpoints for managing users, products, categories, carts, and orders with secure JWT-based authentication and full OpenAPI/Swagger documentation.

---

## 🚀 Features

- 🔐 **JWT-based Authentication** (Login, Register, Role-based access)
- 🗂️ **Category & Product Management**
    - Pagination and sorting support
    - Filter/search by keyword
- 🛒 **Cart & Order Management**
- 🌐 **Built-in Swagger/OpenAPI Documentation**
- 📂 Supports both **PostgreSQL** and **MySQL**
- 📬 CRUD operations via `POST`, `GET`, `PUT`, `DELETE` on all major resources

---

## Project Structure

This project follows a standard Spring Boot application structure, organized into logical packages for clear separation of concerns.

### Explanation of Key Directories:

* **`.mvn/wrapper`**: Contains Maven Wrapper files, allowing you to build the project without a global Maven installation.
* **`src/main/java`**: Primary source code directory for the application.
    * **`com.ecommerce.project`**: The base package for the application.
        * **`config`**: Configuration classes for the application, including Swagger/OpenAPI setup and application constants.
        * **`controller`**: REST controllers that handle incoming HTTP requests and define API endpoints.
        * **`exception`**: Custom exception classes and global exception handlers.
        * **`model`**: JPA entities representing the database tables and the core business objects.
        * **`payload`**: Data Transfer Objects (DTOs) used for request and response bodies in the API.
        * **`repository`**: Spring Data JPA repositories for database interaction.
        * **`security`**: Classes related to application security, including JWT authentication, request/response DTOs for security, and Spring Security configurations.
        * **`service`**: Business logic layer, containing service interfaces and their implementations.
        * **`util`**: Utility classes for common helper functions.
        * **`SbEcomApplication.java`**: The main Spring Boot application entry point.
* **`src/main/resources`**: Contains application configuration files and static resources.
    * **`application.properties`**: Main configuration file for Spring Boot, including database connection details and other settings.
* **`src/test/java`**: Contains unit and integration tests for the application.
* **`.gitignore`**: Specifies intentionally untracked files that Git should ignore.
* **`HELP.md`**: A markdown file potentially containing additional help or instructions.
* **`mvnw`, `mvnw.cmd`**: Maven Wrapper scripts for Linux/macOS and Windows, respectively.
* **`pom.xml`**: The Project Object Model file for Maven, defining project dependencies, build configurations, and other metadata.


---
## ⚙️ Tech Stack

- **Java 17+**
- **Spring Boot**
- **Spring Security (JWT)**
- **Spring Data JPA**
- **PostgreSQL / MySQL**
- **Swagger/OpenAPI**
- **Lombok**

---

## 🧪 API Endpoints

> Full documentation available at `http://localhost:8080/swagger-ui/index.html#/`

| Method   | Endpoint                             | Description                        |
|----------|--------------------------------------|------------------------------------|
| `POST`   | `/api/admin/categories`              | Creates a new category.            |
| `GET`    | `/api/public/categories`             | Retrieves all categories.          |
| `PUT`    | `/api/admin/categories/{categoryId}` | Update category using ID.          |
| `DELETE` | `/api/admin/categories/{categories}` | Delete category using ID.          |


> Note: All endpoints are secured via JWT and require Authorization header.

---

## 🛠️ Getting Started

### 📁 Clone the Repository

```bash
git clone https://github.com/ALABELEWE/oja-afefe-eCommerce-spring-API
cd oja-afefe-ecommerce-api
```

### 🧰 Prerequisites

- Java 17+
- Maven
- PostgreSQL or MySQL database
- (Optional) Postman or Swagger UI for testing

### ⚙️ Database Setup

1. Create a database named `oja_afefe_db` in PostgreSQL or MySQL.
2. Update your `application.properties` or `application.yml` with your DB credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/oja_afefe_db
   spring.datasource.username=your_username (recommended: root)
   spring.datasource.password=your_password
   ```

3. Tables will be auto-generated via JPA on application startup.

### ▶️ Run the Application

```bash
./mvnw spring-boot:run
```

Once started, visit:

- `http://localhost:8080/swagger-ui/index.html` for interactive API docs
- `http://localhost:8080/api/products` for product endpoint

---

## 🔮 Future Improvements

- OAuth2 / Google Login
- Multilingual Support
- Vendor Reviews & Ratings
- AI-powered product recommendations
- Geo-location filtering (e.g. Nearby stores, categories)
- Admin dashboard & analytics

---

## 📜 License

**Open Source** — Feel free to use, contribute, or fork this project.

---

## 🤝 Contributing

1. Fork this repo
2. Create a new branch: `git checkout -b feature/awesome-feature`
3. Commit your changes: `git commit -m 'Add feature'`
4. Push to your branch: `git push origin feature/awesome-feature`
5. Open a Pull Request

---

## ✨ Author

Developed by OSEIN RIDWAN ALABELEWE  
Feel free to connect on X @Alabelewe_001