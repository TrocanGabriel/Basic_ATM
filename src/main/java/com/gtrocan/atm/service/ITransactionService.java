package com.gtrocan.atm.service;

import com.gtrocan.atm.model.Withdrawal;

import java.util.Map;

public interface ITransactionService {

    Map<String, Integer> processTransaction(Withdrawal withdrawal);
}
