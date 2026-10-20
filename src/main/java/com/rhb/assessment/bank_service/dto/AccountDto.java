package com.rhb.assessment.bank_service.dto;

import java.math.BigDecimal;

public record AccountDto(String accountNumber, BigDecimal balance, Long customerId) {}
