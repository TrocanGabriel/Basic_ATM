package com.gtrocan.atm.exception;


import com.gtrocan.atm.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleInvalidAccount(AccountNotFoundException exception){
        log.info("Account used for transaction is invalid");

        List<String> details = new ArrayList<>();
        details.add(exception.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Account  used for transaction is invalid", details);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public final  ResponseEntity<ErrorResponse> handleNotEnoughMoneyIssue(NotEnoughMoneyException exception){
        log.info("Not enough money to process transaction");

        List<String> details = new ArrayList<>();
        details.add(exception.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Not enough money to process transaction", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public final  ResponseEntity<ErrorResponse> handleInvalidAmount(InvalidAmountException exception) {
        log.info("Amount used for transaction is invalid");

        List<String> details = new ArrayList<>();
        details.add(exception.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Amount used for transaction is invalid", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidATMException.class)
    public final  ResponseEntity<ErrorResponse> handleInvalidATM(InvalidATMException exception) {
        log.info("ATM ID used for transaction is invalid");

        List<String> details = new ArrayList<>();
        details.add(exception.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("ATM ID used for transaction is invalid", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
