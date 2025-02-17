package com.example.fabrick.service;

import com.example.fabrick.dto.response.BalanceDTO;
import com.example.fabrick.dto.response.TransactionDTO;
import com.example.fabrick.dto.response.MoneyTransferResponse;
import com.example.fabrick.dto.request.MoneyTransferRequest;
import java.time.LocalDate;

import java.util.List;

public interface BankingService {
    BalanceDTO getBalance();

    List<TransactionDTO> getTransactions(LocalDate fromDate,LocalDate toDate);

    MoneyTransferResponse createMoneyTransfer(MoneyTransferRequest request);

}
