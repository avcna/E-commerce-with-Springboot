# SB-Ecom

A Spring Boot backend for an e-commerce application, featuring product/category management with image upload, shopping cart, orders, addresses, and JWT-based authentication.

## Tech Stack

- Java 17
- Spring Boot 4.0.6 (Web, Data JPA, Validation, Security)
- PostgreSQL (runtime), H2 and MySQL drivers also available
- JWT (jjwt)
- ModelMapper
- Maven (with included wrapper `mvnw`)

## Prerequisites

- Java 17 (JDK)
- A PostgreSQL database instance
- Maven is not required to be installed globally — this project ships with the Maven Wrapper (`mvnw`)

## Configuration

The app reads its datasource credentials from environment variables. Set these before running:

| Variable      | Description                          |
|---------------|---------------------------------------|
| `DB_URL`      | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/ecommerce` |
| `DB_USERNAME` | Database username                     |
| `DB_PASSWORD` | Database password                     |
| `STRIPE_SECRET_KEY` | Stripe secret key (optional; only needed for `POST /api/order/stripe-client-secret`) |

Other notable settings in `src/main/resources/application.properties`:

- `server.port=5000` — the app runs on port `5000`
- `project.image=images/` — folder used for uploaded product images
- `spring.app.jwtSecret` / `spring.app.jwtExpirationMs` — JWT signing key and expiration
- `spring.jpa.hibernate.ddl-auto=update` — schema is auto-updated on startup

> Note: the JWT secret in `application.properties` is a placeholder for local development. Use a strong, unique secret (ideally via an environment variable) in any real deployment.

## How to Run

1. Clone the repository and open a terminal in the project root.

2. Export the required database environment variables:

   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/ecommerce
   export DB_USERNAME=your_db_username
   export DB_PASSWORD=your_db_password
   ```

3. Make sure your PostgreSQL server is running and the database from `DB_URL` exists.

4. Run the application using the Maven Wrapper:

   ```bash
   ./mvnw spring-boot:run
   ```

   On Windows, use `mvnw.cmd spring-boot:run` instead.

5. The API will be available at:

   ```
   http://localhost:5000
   ```

### Alternative: build and run the jar

```bash
./mvnw clean package
java -jar target/sb-ecom-0.0.1-SNAPSHOT.jar
```

## Running Tests

```bash
./mvnw test
```

## Main API Areas

- `AuthController` — registration, login, JWT-based authentication
- `CategoryController` — category CRUD
- `ProductController` — product CRUD, image upload, and pagination
- `CartController` — shopping cart operations
- `OrderController` — order placement and management
- `AddressController` — user address management

## Paying with Stripe (Test Mode)

Card payments go through Stripe in test mode, so no real money is charged. When the payment form asks for card details, fill it in like this:

| Field         | Value                                                  |
|---------------|--------------------------------------------------------|
| Card number   | `4242 4242 4242 4242`                                  |
| Expiration    | any date in the future, e.g. `12 / 34`                 |
| Security code | any digits, e.g. `123`                                 |
| Country       | anything                                               |

The backend needs the `STRIPE_SECRET_KEY` environment variable (a `sk_test_...` key from the Stripe sandbox dashboard) for `POST /api/order/stripe-client-secret` to work.

## API Documentation

The API documentation (Swagger UI) is available here, and you can try the endpoints directly from it:

http://ecom-app-env-1.eba-2vtjbpuc.eu-north-1.elasticbeanstalk.com/swagger-ui/index.html#

Authentication uses the `springBootEcom` JWT cookie, so call `POST /api/auth/signin` first; the browser then sends the cookie with every following request. Open the link over `http://` (the deployment has no HTTPS).

When running locally, Swagger UI is at `http://localhost:5000/swagger-ui/index.html`. A static copy of the OpenAPI specification is also kept in [`openapi.yaml`](openapi.yaml) for importing into tools such as Postman.
