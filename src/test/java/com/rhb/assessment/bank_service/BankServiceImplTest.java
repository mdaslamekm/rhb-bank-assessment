package com.rhb.assessment.bank_service;

import com.rhb.assessment.bank_service.dto.AccountDto;
import com.rhb.assessment.bank_service.dto.CustomerDto;
import com.rhb.assessment.bank_service.model.Account;
import com.rhb.assessment.bank_service.model.Customer;
import com.rhb.assessment.bank_service.repository.AccountRepository;
import com.rhb.assessment.bank_service.repository.CustomerRepository;
import com.rhb.assessment.bank_service.service.BankServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BankServiceImpl bankService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("Mohammed Aslam", "aslam@gmail.com");
        customer.setId(1L);
    }

    @Test
    void testCreateCustomer() {
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer result = bankService.createCustomer(customer);

        assertNotNull(result);
        assertEquals("Mohammed Aslam", result.getName());
        assertEquals("aslam@gmail.com", result.getEmail());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testCreateAccount_CustomerFound() {
        AccountDto dto = new AccountDto("ACC123", new BigDecimal("1500.00"), 1L);
        Account account = new Account("ACC123", new BigDecimal("1500.00"), customer);
        account.setId(10L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = bankService.createAccount(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("ACC123", result.getAccountNumber());
        assertEquals(new BigDecimal("1500.00"), result.getBalance());
        assertEquals(customer, result.getCustomer());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_CustomerNotFound() {
        AccountDto dto = new AccountDto("ACC123", new BigDecimal("1500.00"), 999L);

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        Account result = bankService.createAccount(dto);

        assertNull(result);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testGetAccountsByCustomerEmail() {
        Account account1 = new Account("ACC123", new BigDecimal("1500.00"), customer);
        Account account2 = new Account("ACC456", new BigDecimal("2500.50"), customer);

        when(accountRepository.findAccountsByCustomerEmail("aslam@gmail.com"))
                .thenReturn(List.of(account1, account2));

        List<Account> accounts = bankService.getAccountsByCustomerEmail("aslam@gmail.com");

        assertEquals(2, accounts.size());
        assertEquals("ACC123", accounts.get(0).getAccountNumber());
        assertEquals("ACC456", accounts.get(1).getAccountNumber());
        verify(accountRepository, times(1)).findAccountsByCustomerEmail("aslam@gmail.com");
    }

    @Test
    void testSearchCustomersByName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findByNameContainingIgnoreCase("Mohammed", pageable))
                .thenReturn(page);

        Page<CustomerDto> result = bankService.searchCustomersByName("Mohammed", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Mohammed Aslam", result.getContent().get(0).name());
        verify(customerRepository, times(1)).findByNameContainingIgnoreCase("Mohammed", pageable);
    }
}
