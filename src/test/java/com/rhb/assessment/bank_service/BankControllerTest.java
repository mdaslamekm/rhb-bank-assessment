package com.rhb.assessment.bank_service;

import com.rhb.assessment.bank_service.controller.BankController;
import com.rhb.assessment.bank_service.dto.AccountDto;
import com.rhb.assessment.bank_service.dto.CustomerDto;
import com.rhb.assessment.bank_service.model.Account;
import com.rhb.assessment.bank_service.model.Customer;
import com.rhb.assessment.bank_service.repository.CustomerRepository;
import com.rhb.assessment.bank_service.service.BankService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankController.class)
public class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BankService bankService;

    @MockitoBean
    private CustomerRepository customerRepository;

    @Test
    public void testCreateCustomer() throws Exception {
        Customer customer = new Customer("Mohammed Aslam", "aslam@gmail.com");
        customer.setId(1L);

        Mockito.when(bankService.createCustomer(Mockito.any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post("/api/bank/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Mohammed Aslam"))
                .andExpect(jsonPath("$.email").value("aslam@gmail.com"));
    }

    @Test
    public void testCreateAccount() throws Exception {
        Customer customer = new Customer("Mohammed Aslam", "aslam@gmail.com");
        customer.setId(1L);
        Account account = new Account("ACC123", new BigDecimal("1500.00"), customer);
        account.setId(1L);

        AccountDto accountDto = new AccountDto("ACC123", new BigDecimal("1500.00"), 1L);

        Mockito.when(bankService.createAccount(Mockito.any(AccountDto.class))).thenReturn(account);

        mockMvc.perform(post("/api/bank/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("ACC123"))
                .andExpect(jsonPath("$.balance").value(1500.00));
    }

    @Test
    public void testCreateAccount_NotFound() throws Exception {
        AccountDto accountDto = new AccountDto("ACC123", new BigDecimal("1500.00"), 999L);

        Mockito.when(bankService.createAccount(Mockito.any(AccountDto.class))).thenReturn(null);

        mockMvc.perform(post("/api/bank/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSearchCustomersWithPagination() throws Exception {
        CustomerDto customerDto = new CustomerDto(1L, "Mohammed Aslam", "aslam@gmail.com");
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<CustomerDto> page = new PageImpl<>(List.of(customerDto), pageable, 1);

        Mockito.when(bankService.searchCustomersByName(Mockito.eq("Mohammed"), Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/bank/customers/search")
                        .param("name", "Mohammed")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Mohammed Aslam"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    public void testGetAccountsByEmail() throws Exception {
        Customer customer = new Customer("Mohammed Aslam", "aslam@gmail.com");
        Account account = new Account("ACC123", new BigDecimal("1500.00"), customer);

        Mockito.when(bankService.getAccountsByCustomerEmail("aslam@gmail.com"))
                .thenReturn(List.of(account));

        mockMvc.perform(get("/api/bank/accounts/by-email")
                        .param("email", "aslam@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("ACC123"))
                .andExpect(jsonPath("$[0].balance").value(1500.00));
    }

    @Test
    public void testCallGoogleApi() throws Exception {
        Mockito.when(bankService.callExternalApi()).thenReturn("<html>Google Home</html>");

        mockMvc.perform(get("/api/bank/external-google"))
                .andExpect(status().isOk())
                .andExpect(content().string("<html>Google Home</html>"));
    }

    @Test
    public void testUpdateCustomer_Success() throws Exception {
        Customer existingCustomer = new Customer("Mohammed Aslam", "aslam@gmail.com");
        existingCustomer.setId(1L);

        Customer updatedDetails = new Customer("Mohammed Aslam Updated", "aslam.updated@gmail.com");

        Customer savedCustomer = new Customer("Mohammed Aslam Updated", "aslam.updated@gmail.com");
        savedCustomer.setId(1L);

        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        Mockito.when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(savedCustomer);

        mockMvc.perform(put("/api/bank/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Mohammed Aslam Updated"))
                .andExpect(jsonPath("$.email").value("aslam.updated@gmail.com"));
    }

    @Test
    public void testUpdateCustomer_NotFound() throws Exception {
        Customer updatedDetails = new Customer("Mohammed Aslam Updated", "aslam.updated@gmail.com");

        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/bank/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCustomer_Success() throws Exception {
        Mockito.when(customerRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/bank/customers/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(customerRepository).deleteById(1L);
    }

    @Test
    public void testDeleteCustomer_NotFound() throws Exception {
        Mockito.when(customerRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/bank/customers/{id}", 1L))
                .andExpect(status().isNotFound());

        Mockito.verify(customerRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}
