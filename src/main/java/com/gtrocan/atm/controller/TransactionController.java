package com.gtrocan.atm.controller;

import com.gtrocan.atm.model.Withdrawal;
import com.gtrocan.atm.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TransactionController {

    @Autowired
    private final ITransactionService transactionService;

    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/atm/withdrawal")
    public Map<String, Integer> createTransaction(@RequestBody Withdrawal withdrawal) {
        return transactionService.processTransaction(withdrawal);
    }
}
