package com.allica.bank.customer.app;

import com.allica.bank.customer.domain.Customer;
import com.allica.bank.customer.domain.CustomerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTests {

    @Mock
    private CustomerRepo customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer(1L, "John", "Doe", "Johnny", LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldCreateCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        var result = customerService.createCustomer(customer);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getFirstName(), result.getFirstName());
        assertEquals(customer.getLastName(), result.getLastName());
        assertEquals(customer.getPreferredName(), result.getPreferredName());
        assertEquals(customer.getDateOfBirth(), result.getDateOfBirth());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void shouldGetAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));

        List<Customer> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customer.getFirstName(), result.getFirst().getFirstName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldGetEmptyListWhenNoCustomersExist() {
        when(customerRepository.findAll()).thenReturn(List.of());

        List<Customer> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldGetCustomerById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerById(1L);

        assertTrue(result.isPresent());
        assertEquals(customer.getId(), result.get().getId());
        assertEquals(customer.getPreferredName(), result.get().getPreferredName());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnEmptyForNonExistentCustomerId() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.getCustomerById(1L);

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void shouldUpdatePreferredName() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Optional<Customer> result = customerService.updatePreferredName(1L, "Jane");

        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getPreferredName());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void shouldThrowExceptionForEmptyPreferredName() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.updatePreferredName(1L, ""));

        assertEquals("Preferred name cannot be empty if provided", exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionForNullPreferredName() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.updatePreferredName(1L, null));

        assertEquals("Preferred name cannot be empty if provided", exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldReturnEmptyForNonExistentPreferredNameUpdate() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.updatePreferredName(1L, "Jane");

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldDeleteCustomer() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        boolean result = customerService.deleteCustomer(1L);

        assertTrue(result);
        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldReturnFalseForNonExistentCustomerDeletion() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        boolean result = customerService.deleteCustomer(1L);

        assertFalse(result);
        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, never()).deleteById(1L);
    }
}