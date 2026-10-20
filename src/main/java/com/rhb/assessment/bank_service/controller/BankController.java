package com.rhb.assessment.bank_service.controller;

import com.rhb.assessment.bank_service.dto.AccountDto;
import com.rhb.assessment.bank_service.repository.CustomerRepository;
import com.rhb.assessment.bank_service.dto.CustomerDto;
import com.rhb.assessment.bank_service.model.Account;
import com.rhb.assessment.bank_service.model.Customer;
import com.rhb.assessment.bank_service.service.BankService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank")
public class BankController {

    private final BankService bankService;
    private final CustomerRepository customerRepository;

    public BankController(BankService bankService, CustomerRepository customerRepository) {
        this.bankService = bankService;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = bankService.createCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto) {
        Account createdAccount = bankService.createAccount(accountDto);
        if (createdAccount == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @GetMapping("/customers/search")
    public ResponseEntity<Page<CustomerDto>> searchCustomers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerDto> result = bankService.searchCustomersByName(name, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/accounts/by-email")
    public ResponseEntity<List<Account>> getAccountsByEmail(@RequestParam String email) {
        List<Account> accounts = bankService.getAccountsByCustomerEmail(email);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/external-google")
    public ResponseEntity<String> callGoogleApi() {
        String response = bankService.callExternalApi();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(customerDetails.getName());
                    existingCustomer.setEmail(customerDetails.getEmail());
                    Customer updatedCustomer = customerRepository.save(existingCustomer);
                    return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
