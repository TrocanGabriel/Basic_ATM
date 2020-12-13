package com.gtrocan.atm.utils.enums;

import lombok.Getter;

import java.text.MessageFormat;

@Getter
public enum  TransactionType {

    WITHDRAWAL_TRANSACTION("WT", "Process a withdrawal transaction");

    private String code;
    private String message;

    TransactionType(String code, String message){
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} | {1} : {2} ", name(), code, message);
    }
}
