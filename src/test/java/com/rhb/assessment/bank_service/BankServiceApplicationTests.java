package com.rhb.assessment.bank_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BankServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void testCreateCustomerEndpoint() throws Exception {
		mockMvc.perform(post("/api/bank/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Mohammed Aslam\",\"email\":\"aslam@gmail.com\"}"))
				.andDo(print())
				.andExpect(status().isCreated());
	}

	@Test
	void testCreateAccountAndQueryByEmail() throws Exception {
		String customerResponse = mockMvc.perform(post("/api/bank/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Mohammed Aslam\",\"email\":\"aslam.acct@gmail.com\"}"))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Integer customerId = com.jayway.jsonpath.JsonPath.read(customerResponse, "$.id");

		mockMvc.perform(post("/api/bank/accounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountNumber\":\"ACC999\",\"balance\":1500.00,\"customerId\":" + customerId + "}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.accountNumber").value("ACC999"))
				.andExpect(jsonPath("$.balance").value(1500.00));

		mockMvc.perform(get("/api/bank/accounts/by-email")
				.param("email", "aslam.acct@gmail.com"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].accountNumber").value("ACC999"))
				.andExpect(jsonPath("$[0].balance").value(1500.00));
	}

	@Test
	void testBadJsonReturnsFriendly400() throws Exception {
		mockMvc.perform(post("/api/bank/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ bad: json }"))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.hint").exists());
	}
}
