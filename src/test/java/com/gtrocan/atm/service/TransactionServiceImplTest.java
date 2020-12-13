package com.gtrocan.atm.service;


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
import com.gtrocan.atm.service.impl.TransactionServiceImpl;
import com.gtrocan.atm.utils.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ATMRepository atmRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Withdrawal mockWithdrawal(Integer amount, Long accountId, Long atmId ){
        return Withdrawal.builder()
                .amount(amount)
                .accountId(accountId)
                .atmId(atmId)
                .build();
    }

    private ATM mockATM(Long id, String location, Map<String, Integer> denomination){
        return ATM.builder()
                .id(id)
                .location(location)
                .denomination(denomination)
                .build();
    }

    private Account mockAccount(Long accountId, Integer accountBalance, List<Transaction> transactionList){
        return Account.builder()
                .id(accountId)
                .accountBalance(accountBalance)
                .transactions(transactionList)
                .build();
    }

    private Transaction mockTransaction(Long id, TransactionType transactionType, Integer amount, LocalDateTime transactionTime, Account account){
        return Transaction.builder()
                .id(id)
                .transactionType(transactionType)
                .transactionDate(transactionTime)
                .amount(amount)
                .account(account)
                .build();
    }

    @Test
    public void processWithdrawal_ValidAmount_ValidAccount_ValidATM(){
        Withdrawal mockWithdrawal = mockWithdrawal(100,1L,1L);
        Account mockAccount = mockAccount(1L, 1000, Collections.emptyList());
        Map<String, Integer> denomination = new HashMap<>();
        denomination.put("100",2);
        ATM mockATM = mockATM(1L, "Location1", denomination);
        Transaction mockTransaction = mockTransaction(1L, TransactionType.WITHDRAWAL_TRANSACTION, 100, LocalDateTime.now(), mockAccount);

        ArgumentCaptor<ATM> atmArgumentCaptor = ArgumentCaptor.forClass(ATM.class);
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);

        when(atmRepository.findById(any())).thenReturn(Optional.of(mockATM));
        when(accountRepository.findById(any())).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.save(any())).thenReturn(mockTransaction);

        transactionService.processTransaction(mockWithdrawal);

        verify(accountRepository).save(accountArgumentCaptor.capture());
        assertEquals(mockWithdrawal.getAccountId(),accountArgumentCaptor.getValue().getId());
        assertEquals(900, accountArgumentCaptor.getValue().getAccountBalance());

        verify(atmRepository).save(atmArgumentCaptor.capture());
        assertEquals(mockWithdrawal.getAtmId(),atmArgumentCaptor.getValue().getId());
        assertEquals(1, atmArgumentCaptor.getValue().getDenomination().get("100"));

        verify(transactionRepository).save(transactionArgumentCaptor.capture());
        assertEquals(100,transactionArgumentCaptor.getValue().getAmount());
    }

    @Test
    public void processWithdrawal_InvalidAmount_ValidAccount_ValidATM(){
        Withdrawal mockWithdrawal = mockWithdrawal(102,1L,1L);

        Exception exception = assertThrows(InvalidAmountException.class, () -> {
            transactionService.processTransaction(mockWithdrawal);
        });

        String expected = "Amount used in transaction is invalid";
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }

    @Test
    public void processWithdrawal_ValidAmount_ValidAccount_InvalidATM(){
        Withdrawal mockWithdrawal = mockWithdrawal(100,1L,1L);

        when(accountRepository.findById(any())).thenReturn(Optional.empty());


        Exception exception = assertThrows(AccountNotFoundException.class, () -> {
            transactionService.processTransaction(mockWithdrawal);
        });

        String expected = "Account is not valid for this transaction";
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }

    @Test
    public void processWithdrawal_ValidAmount_InvalidAccount_ValidATM(){
        Withdrawal mockWithdrawal = mockWithdrawal(100,1L,1L);
        Account mockAccount = mockAccount(1L, 1000, Collections.emptyList());

        when(accountRepository.findById(any())).thenReturn(Optional.of(mockAccount));
        when(atmRepository.findById(any())).thenReturn(Optional.empty());


        Exception exception = assertThrows(InvalidATMException.class, () -> {
            transactionService.processTransaction(mockWithdrawal);
        });

        String expected = "ATM id is invalid";
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }

    @Test
    public void processWithdrawal_ValidAmount_ValidAccount_ValidATM_NotEnoughAccountBalance(){
        Withdrawal mockWithdrawal = mockWithdrawal(10000,1L,1L);
        Account mockAccount = mockAccount(1L, 1000, Collections.emptyList());
        Map<String, Integer> denomination = new HashMap<>();
        denomination.put("100",2);
        ATM mockATM = mockATM(1L, "Location1", denomination);

        when(atmRepository.findById(any())).thenReturn(Optional.of(mockATM));
        when(accountRepository.findById(any())).thenReturn(Optional.of(mockAccount));

        Exception exception = assertThrows(NotEnoughMoneyException.class, () -> {
            transactionService.processTransaction(mockWithdrawal);
        });

        String expected = "Account does not have enough money";
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }

    @Test
    public void processWithdrawal_ValidAmount_ValidAccount_ATMNotEnoughMoney(){
        Withdrawal mockWithdrawal = mockWithdrawal(500,1L,1L);
        Account mockAccount = mockAccount(1L, 1000, Collections.emptyList());
        Map<String, Integer> denomination = new HashMap<>();
        denomination.put("100",2);
        ATM mockATM = mockATM(1L, "Location1", denomination);

        when(atmRepository.findById(any())).thenReturn(Optional.of(mockATM));
        when(accountRepository.findById(any())).thenReturn(Optional.of(mockAccount));

        Exception exception = assertThrows(NotEnoughMoneyException.class, () -> {
            transactionService.processTransaction(mockWithdrawal);
        });

        String expected = "ATM does not have enough money for this transaction";
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }

}
