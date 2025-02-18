package com.example.fabrick.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transferId;
    private LocalDate executionDate;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String creditorName;
    private String creditorAccountCode;
    private String status;
}