# rhb-bank-assessment
RHB Bank Technical Assessment:

A clean, extensible Java 17 and Spring Boot API demonstrating REST methods, search pagination, custom JPA table joins, external integration, and AspectJ logging.

---

## 🛠️ Project Requirements Checklist Compliance

* **a. HTTP Methods:** Full implementation of `@PostMapping`, `@GetMapping`, `@PutMapping`, and `@DeleteMapping` routing endpoints inside the controller tier.
* **b. Search with Pagination:** Implemented via Spring Data JPA `Pageable` parameters to filter customer records by name without overloading server memory profiles.
* **c. Request/Response Logging:** Configured an independent AspectJ wrapper (`@Aspect`) to intercept, log, and measure the performance execution time of all controller requests.
* **d. In-Memory Database:** Structured using an integrated H2 database environment that initializes instantly on application startup.
* **e. Two-Table Join Query:** Custom JPQL statement linking the `Customer` and `Account` tables using a structural database `JOIN` filtered by email.
* **f. External API Call:** Uses Spring's modern `RestClient` framework to securely perform background HTTP GET requests out to `://google.com`.
* **g. Unit Tests:** Robust test coverage leveraging `MockMvc` and `@MockitoBean` structures to validate core controller actions and response payloads.

---

## 🚀 How to Run the Application

### Prerequisites
* Java JDK 17
* Apache Maven (Wrapper included)

### 1. Build and Compile
Open your terminal at the project root directory and execute a clean package build:
```bash
./mvnw clean package -DskipTests
```

### 2. Run the Server
Launch the application using the Spring Boot plugin runner:
```bash
./mvnw spring-boot:run
```
The application server will initialize and remain active on **`http://localhost:8080`**.

### 3. Access H2 In-Memory Database Console
To view database tables and schemas directly inside your browser:
* **URL:** `http://localhost:8080/h2-console`
* **JDBC URL:** `jdbc:h2:mem:rhbdb`
* **Username:** `sa`
* **Password:** *(Leave completely blank)*

---

## 🧪 Manual API Testing (Postman)

The comprehensive manual testing file **`rhb-assessment-collection.json`** is located at the root level of this project repository.

### How to use it:
1. Open the Postman desktop application.
2. Click **Import** in the top-left sidebar panel.
3. Select or drag-and-drop the `rhb-assessment-collection.json` file to instantly load all pre-configured endpoints (`POST`, `GET`, `PUT`, `DELETE`).
4. Ensure your Spring Boot application is n your machine, then execute the requests.
