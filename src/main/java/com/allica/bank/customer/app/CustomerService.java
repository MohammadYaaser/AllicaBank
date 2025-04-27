package com.allica.bank.customer.app;

import com.allica.bank.customer.domain.Customer;
import com.allica.bank.customer.domain.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepo customerRepository;

    @Autowired
    public CustomerService(CustomerRepo customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::toDTO);
    }

    public Optional<Customer> updatePreferredName(Long id, String preferredName) {
        return customerRepository.findById(id)
                .map(customer -> {
                    if (preferredName == null || preferredName.trim().isEmpty()) {
                        throw new IllegalArgumentException("Preferred name cannot be empty if provided");
                    }
                    customer.setPreferredName(preferredName);
                    customerRepository.save(customer);
                    return toDTO(customer);
                });
    }

    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Customer toDTO(Customer customer) {
        return new Customer(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPreferredName(),
                customer.getDateOfBirth()
        );
    }
}