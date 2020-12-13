package com.gtrocan.atm.service.impl;


import com.gtrocan.atm.entity.ATM;
import com.gtrocan.atm.entity.Account;
import com.gtrocan.atm.entity.Transaction;
import com.gtrocan.atm.model.Withdrawal;
import com.gtrocan.atm.repository.ATMRepository;
import com.gtrocan.atm.repository.AccountRepository;
import com.gtrocan.atm.repository.TransactionRepository;
import com.gtrocan.atm.service.ITransactionService;
import com.gtrocan.atm.utils.enums.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ATMRepository atmRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, ATMRepository atmRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.atmRepository = atmRepository;
    }

    @Override
    public Map<String, Integer> processTransaction(Withdrawal withdrawal) {
        Optional<Account> accountOptional =  accountRepository.findById(withdrawal.getAccountId());
        Optional<ATM> atmOptional = atmRepository.findById(withdrawal.getAtmId());
        Account account;
        ATM atm;

        if(accountOptional.isPresent()){
            account = accountOptional.get();
        } else {
            throw new RuntimeException();
        }
        if(atmOptional.isPresent()){
            atm = atmOptional.get();
        } else {
            throw new RuntimeException();
        }

            Map<String, Integer> withdrawalDenomination = calculateDenomination(withdrawal.getAmount(),atm.getDenomination());
            updateATMDenomination(atm, withdrawalDenomination);
            Transaction transaction = createTransaction(account, withdrawal);
            Integer transactionAmount = transaction.getAmount();
            updateAccountBalance(account,transactionAmount);
            return withdrawalDenomination;
    }

    private void updateATMDenomination(ATM atm, Map<String, Integer> withdrawalDenomination) {
        Map<String, Integer> atmDenomination = atm.getDenomination();
        for(Map.Entry<String, Integer> banknotes : withdrawalDenomination.entrySet()) {
            Integer banknotesNeeded = banknotes.getValue();
            String key =banknotes.getKey();
            atmDenomination.put(key, atmDenomination.get(key) - banknotesNeeded);
        }
        atm.setDenomination(atmDenomination);
        atmRepository.save(atm);
    }

    private    Map<String, Integer>  calculateDenomination(Integer amount, Map<String, Integer> atmDenomination) {
        Map<String, Integer> withdrawalDenomination = new HashMap<>();
        TreeMap<String, Integer> sorted = new TreeMap<>(Collections.reverseOrder());
        sorted.putAll(atmDenomination);
        Set<Map.Entry<String, Integer>> mappings = sorted.entrySet();
        for(Map.Entry<String, Integer> banknotes : mappings) {
            int banknoteValue =  Integer.parseInt(banknotes.getKey());
            if(amount >= banknoteValue){
                int divider = amount / banknoteValue;
                int banknotesNeeded = (divider <= banknotes.getValue()) ? divider : banknotes.getValue();
                amount -= banknoteValue * banknotesNeeded;
                withdrawalDenomination.put(banknotes.getKey(), banknotesNeeded);
            }
            if(amount == 0){
                break;
            }
        }
        return withdrawalDenomination;
    }

    private Transaction createTransaction(Account account, Withdrawal withdrawal) {
        Transaction transaction = new Transaction();
        transaction.setAmount(withdrawal.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAWAL_TRANSACTION);
        return transactionRepository.save(transaction);
    }

    private void updateAccountBalance(Account account, Integer transactionAmount) {
        Integer newBalance = account.getAccountBalance() - transactionAmount;
        account.setAccountBalance(newBalance);
        accountRepository.save(account);
    }

}
