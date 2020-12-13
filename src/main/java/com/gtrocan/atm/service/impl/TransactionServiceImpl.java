package com.gtrocan.atm.service.impl;


import com.gtrocan.atm.model.Withdrawal;
import com.gtrocan.atm.service.ITransactionService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TransactionServiceImpl implements ITransactionService {

    @Override
    public Map<String, Integer> processTransaction(Withdrawal withdrawal) {
        return null;
    }
}
