package com.rhb.assessment.bank_service.service;

import com.rhb.assessment.bank_service.dto.AccountDto;
import com.rhb.assessment.bank_service.dto.CustomerDto;
import com.rhb.assessment.bank_service.model.Customer;
import com.rhb.assessment.bank_service.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BankService {
    Customer createCustomer(Customer customer);
    Account createAccount(AccountDto accountDto);
    Page<CustomerDto> searchCustomersByName(String name, Pageable pageable);
    List<Account> getAccountsByCustomerEmail(String email);
    String callExternalApi();
}
