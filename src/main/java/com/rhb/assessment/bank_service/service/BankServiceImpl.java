package com.rhb.assessment.bank_service.service;

import com.rhb.assessment.bank_service.dto.AccountDto;
import com.rhb.assessment.bank_service.dto.CustomerDto;
import com.rhb.assessment.bank_service.model.Customer;
import com.rhb.assessment.bank_service.model.Account;
import com.rhb.assessment.bank_service.repository.CustomerRepository;
import com.rhb.assessment.bank_service.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service
public class BankServiceImpl implements BankService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final RestClient restClient;

    public BankServiceImpl(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.restClient = RestClient.create();
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Account createAccount(AccountDto accountDto) {
        Customer customer = customerRepository.findById(accountDto.customerId())
                .orElse(null);
        if (customer == null) {
            return null;
        }
        Account account = new Account(accountDto.accountNumber(), accountDto.balance(), customer);
        return accountRepository.save(account);
    }

    @Override
    public Page<CustomerDto> searchCustomersByName(String name, Pageable pageable) {
        Page<Customer> customers = customerRepository.findByNameContainingIgnoreCase(name, pageable);
        return customers.map(customer -> new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail()
        ));
    }

    @Override
    public List<Account> getAccountsByCustomerEmail(String email) {
        return accountRepository.findAccountsByCustomerEmail(email);
    }

    @Override
    public String callExternalApi() {
        return restClient.get()
                .uri("https://www.google.com")
                .retrieve()
                .body(String.class);
    }
}
