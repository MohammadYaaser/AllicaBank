package com.allica.bank.customer.web;

import com.allica.bank.customer.app.CustomerService;
import com.allica.bank.customer.domain.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Create a new customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Object> createCustomer(@Valid @RequestBody Customer request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @Operation(summary = "Get all customers")
    @ApiResponse(responseCode = "200", description = "List of customers")
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @Operation(summary = "Get customer by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update customer's preferred name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preferred name updated"),
            @ApiResponse(responseCode = "400", description = "Invalid preferred name"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PatchMapping("/{id}/preferred-name")
    public ResponseEntity<Customer> updatePreferredName(@PathVariable Long id, @RequestBody String preferredName) {
        return customerService.updatePreferredName(id, preferredName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete customer by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (customerService.deleteCustomer(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
