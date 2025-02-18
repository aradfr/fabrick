package com.example.fabrick.repository;

import com.example.fabrick.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByExecutionDateBetween(LocalDate fromDate, LocalDate toDate);
}