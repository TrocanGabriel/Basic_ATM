package com.gtrocan.atm.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"transactions"})
@EqualsAndHashCode(exclude = {"transactions"})
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer accountBalance;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Transaction> transactions;
}
