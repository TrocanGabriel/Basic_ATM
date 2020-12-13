package com.gtrocan.atm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtrocan.atm.model.Withdrawal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    public static final String WITHDRAWAL_URL = "/atm/withdrawal";

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void processWithdrawalTransaction_returns_200_and_denomination(){
        Withdrawal mockWithdrawal = new Withdrawal(100,1L,1L);

        try {
            mockMvc.perform(post(WITHDRAWAL_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockWithdrawal)))
                    .andExpect(status().isOk());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
