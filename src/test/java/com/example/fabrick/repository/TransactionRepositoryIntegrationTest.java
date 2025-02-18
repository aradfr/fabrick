package com.example.fabrick.repository;

import com.example.fabrick.entity.TransactionEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// TransactionRepositoryIntegrationTest.java
@SpringBootTest
public class TransactionRepositoryIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void testSaveAndFindTransaction() {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransferId("tx-123");
        transaction.setExecutionDate(LocalDate.now());
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setCurrency("EUR");
        transaction.setDescription("Money Transfer Test");
        transaction.setCreditorName("John Doe");
        transaction.setCreditorAccountCode("IT23A0336844430152923804660");
        transaction.setStatus("EXECUTED");

        transactionRepository.save(transaction);

        List<TransactionEntity> transactions = transactionRepository.findByExecutionDateBetween(
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)
        );
        assertThat(transactions).isNotEmpty();
    }
}