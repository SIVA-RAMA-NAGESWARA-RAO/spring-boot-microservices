package com.example.productservice;

import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @BeforeAll
    static void startMongo() {
        mongo.start();
        System.setProperty("spring.data.mongodb.uri", mongo.getReplicaSetUrl("productdb"));
    }

    @AfterAll
    static void stopMongo() {
        mongo.stop();
    }

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ProductRepository repository;

    @Test
    void shouldCreateAndFetchProduct() {
        Product input = new Product(null, "Burger", "Veg burger", new BigDecimal("149.99"), 20);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Product> created = restTemplate.postForEntity(
                url(), new HttpEntity<>(input, headers), Product.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().getId()).isNotBlank();

        ResponseEntity<Product> fetched = restTemplate.getForEntity(
                url() + "/" + created.getBody().getId(), Product.class);

        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody().getName()).isEqualTo("Burger");
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void shouldReturnNotFoundForUnknownProduct() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url() + "/does-not-exist", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Product not found");
    }

    private String url() {
        return "http://localhost:" + port + "/api/products";
    }
}
