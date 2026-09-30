# Product Service

Spring Boot REST API for managing products using MongoDB, with integration tests running against a real MongoDB Testcontainer.

## Requirements
- Java 17+
- Maven 3.9+
- Docker Desktop running (required for Testcontainers tests)
- MongoDB locally for normal application execution, unless `MONGODB_URI` is provided

## Run MongoDB locally
```bash
docker run -d --name product-mongodb -p 27017:27017 mongo:7.0
```

## Run the service
```bash
mvn spring-boot:run
```

The service runs on `http://localhost:8081`.

## REST endpoints
- `POST /api/products` - create product
- `GET /api/products` - get all products
- `GET /api/products/{id}` - get product by ID
- `PUT /api/products/{id}` - update product
- `DELETE /api/products/{id}` - delete product

### Sample POST body
```json
{
  "name": "Burger",
  "description": "Veg burger",
  "price": 149.99,
  "quantity": 20
}
```

## Run integration tests
Make sure Docker Desktop is running, then:
```bash
mvn test
```

Testcontainers automatically starts a temporary MongoDB container for the integration tests.
