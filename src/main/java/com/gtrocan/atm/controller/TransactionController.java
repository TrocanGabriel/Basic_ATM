package com.gtrocan.atm.controller;

import com.gtrocan.atm.model.Withdrawal;
import com.gtrocan.atm.service.ITransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class TransactionController {

    public static final String LOG_FOR_ATM_WITHDRAWAL = "ATM {} : Starting Withdrawal process for {} account with the amount of {} RON";

    private final ITransactionService transactionService;

    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/atm/withdrawal")
    public Map<String, Integer> createTransaction(@RequestBody Withdrawal withdrawal) {
        log.info(LOG_FOR_ATM_WITHDRAWAL,withdrawal.getAtmId(), withdrawal.getAccountId(), withdrawal.getAmount());
        return transactionService.processTransaction(withdrawal);
    }
}
