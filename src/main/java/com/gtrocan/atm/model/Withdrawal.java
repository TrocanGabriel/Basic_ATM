package com.gtrocan.atm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Withdrawal {

    @NotNull
    private Integer amount;

    @NotNull
    private Long accountId;

    @NotNull
    private Long atmId;
}
