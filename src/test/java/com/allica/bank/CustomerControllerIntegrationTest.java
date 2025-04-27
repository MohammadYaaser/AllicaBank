package com.allica.bank;

import com.allica.bank.customer.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class CustomerControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Customer validCustomer;

    @BeforeEach
    void setUp() {
        validCustomer = new Customer(null, "John", "Doe", null, LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldCreateCustomerWithValidData() {
        ResponseEntity<Customer> response = restTemplate.postForEntity("/customers", validCustomer, Customer.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), response.getBody().getDateOfBirth());
        assertNull(response.getBody().getPreferredName());
    }

    @Test
    void shouldReturnBadRequestForEmptyFirstName() {
        Customer invalidCustomer = new Customer(null, null, "Doe", null, LocalDate.of(1990, 1, 1));
        ResponseEntity<String> response = restTemplate.postForEntity("/customers", invalidCustomer, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("First name is required"));
    }

    @Test
    void shouldReturnBadRequestForFutureDateOfBirth() {
        Customer invalidCustomer = new Customer(null, "John", "Doe", null, LocalDate.now().plusDays(1));
        ResponseEntity<String> response = restTemplate.postForEntity("/customers", invalidCustomer, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Date of birth must be in the past"));
    }

    @Test
    void shouldGetAllCustomers() {
        restTemplate.postForEntity("/customers", validCustomer, Customer.class);
        ResponseEntity<Customer[]> response = restTemplate.getForEntity("/customers", Customer[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 1);
        assertEquals("John", response.getBody()[0].getFirstName());
    }

    @Test
    void shouldGetCustomerById() {
        ResponseEntity<Customer> createResponse = restTemplate.postForEntity("/customers", validCustomer, Customer.class);
        Long id = createResponse.getBody().getId();
        ResponseEntity<Customer> response = restTemplate.getForEntity("/customers/" + id, Customer.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
    }

    @Test
    void shouldReturnNotFoundForNonExistentCustomer() {
        ResponseEntity<String> response = restTemplate.getForEntity("/customers/999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldUpdatePreferredName() {
        ResponseEntity<Customer> createResponse = restTemplate.postForEntity("/customers", validCustomer, Customer.class);
        Long id = createResponse.getBody().getId();
        HttpEntity<String> updateRequest = new HttpEntity<>("Johnny");
        ResponseEntity<Customer> response = restTemplate.exchange(
                "/customers/" + id + "/preferred-name",
                HttpMethod.PATCH,
                updateRequest,
                Customer.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Johnny", response.getBody().getPreferredName());
    }

    @Test
    void shouldReturnNotFoundForNonExistentPreferredNameUpdate() {
        HttpEntity<String> updateRequest = new HttpEntity<>("Johnny");
        ResponseEntity<String> response = restTemplate.exchange(
                "/customers/999/preferred-name",
                HttpMethod.PATCH,
                updateRequest,
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnBadRequestForEmptyPreferredName() {
        ResponseEntity<Customer> createResponse = restTemplate.postForEntity("/customers", validCustomer, Customer.class);
        Long id = createResponse.getBody().getId();
        HttpEntity<String> updateRequest = new HttpEntity<>("");
        ResponseEntity<String> response = restTemplate.exchange(
                "/customers/" + id + "/preferred-name",
                HttpMethod.PATCH,
                updateRequest,
                String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldDeleteCustomer() {
        ResponseEntity<Customer> createResponse = restTemplate.postForEntity("/customers", validCustomer, Customer.class);
        Long id = createResponse.getBody().getId();
        ResponseEntity<Void> response = restTemplate.exchange(
                "/customers/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        ResponseEntity<String> getResponse = restTemplate.getForEntity("/customers/" + id, String.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForNonExistentCustomerDeletion() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/customers/999",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}