package com.gtrocan.atm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ATM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location;

    @ElementCollection
    @CollectionTable(name = "atm_denomination_mapping",
            joinColumns = {@JoinColumn(name = "atm_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "denomination")
    @Column(name = "amount")
    private Map<String, Integer> denomination = new HashMap<>();
}
