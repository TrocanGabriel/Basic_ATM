package com.gtrocan.atm.service.impl;


import com.gtrocan.atm.entity.ATM;
import com.gtrocan.atm.entity.Account;
import com.gtrocan.atm.entity.Transaction;
import com.gtrocan.atm.exception.AccountNotFoundException;
import com.gtrocan.atm.exception.InvalidATMException;
import com.gtrocan.atm.exception.InvalidAmountException;
import com.gtrocan.atm.exception.NotEnoughMoneyException;
import com.gtrocan.atm.model.Withdrawal;
import com.gtrocan.atm.repository.ATMRepository;
import com.gtrocan.atm.repository.AccountRepository;
import com.gtrocan.atm.repository.TransactionRepository;
import com.gtrocan.atm.service.ITransactionService;
import com.gtrocan.atm.utils.enums.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class TransactionServiceImpl implements ITransactionService {

    private static final String INVALID_AMOUNT_EXCEPTION = "Amount used in transaction is invalid";
    private static final String ACCOUNT_NOT_FOUND_EXCEPTION = "Account is not valid for this transaction";
    private static final String ATM_NOT_FOUND_EXCEPTION = "ATM id is invalid";
    private static final String NOT_ENOUGH_MONEY_EXCEPTION = "Account does not have enough money";
    private static final String ATM_NOT_ENOUGH_MONEY_EXCEPTION = "ATM does not have enough money for this transaction";

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
        log.debug("Processing withdrawal for account id {} of {} RON", withdrawal.getAccountId(), withdrawal.getAmount());

        Optional<Account> accountOptional =  accountRepository.findById(withdrawal.getAccountId());
        Optional<ATM> atmOptional = atmRepository.findById(withdrawal.getAtmId());
        Account account;
        ATM atm;

        if(withdrawal.getAmount() % 10 != 0){
            log.debug("Failed withdrawal for account id {} because amount of {} RON is incorrect", withdrawal.getAccountId(), withdrawal.getAmount());
            throw new InvalidAmountException(INVALID_AMOUNT_EXCEPTION);
        }
        if(accountOptional.isPresent()){
            account = accountOptional.get();
        } else {
            log.debug("Failed withdrawal for account id {} because account is invalid", withdrawal.getAccountId());
            throw new AccountNotFoundException(ACCOUNT_NOT_FOUND_EXCEPTION);
        }
        if(atmOptional.isPresent()){
            atm = atmOptional.get();
        } else {
            log.debug("Failed withdrawal for account id {} because ATM id of {} is invalid", withdrawal.getAccountId(), withdrawal.getAtmId());
            throw new InvalidATMException(ATM_NOT_FOUND_EXCEPTION);
        }

        if(account.getAccountBalance() > withdrawal.getAmount()){
            Map<String, Integer> withdrawalDenomination = calculateDenomination(withdrawal.getAmount(),atm.getDenomination());
            updateATMDenomination(atm, withdrawalDenomination);
            Transaction transaction = createTransaction(account, withdrawal);
            Integer transactionAmount = transaction.getAmount();
            updateAccountBalance(account,transactionAmount);
            return withdrawalDenomination;
        } else {
            log.debug("Failed withdrawal for account id {} because account does not have enough money for a transaction of {} RON", withdrawal.getAccountId(), withdrawal.getAmount());
            throw new NotEnoughMoneyException(NOT_ENOUGH_MONEY_EXCEPTION);
        }
    }

    private void updateATMDenomination(ATM atm, Map<String, Integer> withdrawalDenomination) {
        log.debug("Updating ATM available denominations for ATM id {} ", atm.getId());

        Map<String, Integer> atmDenomination = atm.getDenomination();
        for(Map.Entry<String, Integer> banknotes : withdrawalDenomination.entrySet()) {
            Integer banknotesNeeded = banknotes.getValue();
            String key =banknotes.getKey();
            atmDenomination.put(key, atmDenomination.get(key) - banknotesNeeded);
        }
        atm.setDenomination(atmDenomination);
        atmRepository.save(atm);
    }

    private Map<String, Integer>  calculateDenomination(Integer amount, Map<String, Integer> atmDenomination) {
        log.debug("Calculating available denominations for Withdrawal of amount {} RON ", amount);

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
        if(amount != 0) {
            log.debug("The available money in ATM were not enough for this transaction of {} RON", amount);
            throw new NotEnoughMoneyException(ATM_NOT_ENOUGH_MONEY_EXCEPTION);
        }
        return withdrawalDenomination;
    }

    private Transaction createTransaction(Account account, Withdrawal withdrawal) {
        log.debug("Creating transaction for Withdrawal of amount {} RON for account id: {} ", withdrawal.getAmount(), account.getId());

        Transaction transaction = new Transaction();
        transaction.setAmount(withdrawal.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAWAL_TRANSACTION);
        return transactionRepository.save(transaction);
    }

    private void updateAccountBalance(Account account, Integer transactionAmount) {
        log.debug("Update account with id: {} following withdrawal of {} RON ", account.getId(), transactionAmount);

        Integer newBalance = account.getAccountBalance() - transactionAmount;
        account.setAccountBalance(newBalance);
        accountRepository.save(account);
    }

}
