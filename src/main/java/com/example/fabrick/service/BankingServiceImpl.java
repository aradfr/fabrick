package com.example.fabrick.service;

import com.example.fabrick.config.FabrickConfig;
import com.example.fabrick.dto.request.MoneyTransferRequest;
import com.example.fabrick.dto.response.ApiResponse;
import com.example.fabrick.dto.response.BalanceDTO;
import com.example.fabrick.dto.response.MoneyTransferResponse;
import com.example.fabrick.dto.response.TransactionDTO;
import com.example.fabrick.entity.TransactionEntity;
import com.example.fabrick.exception.BankingServiceException;
import com.example.fabrick.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class BankingServiceImpl implements BankingService {

    private final RestTemplate restTemplate;
    private final FabrickConfig config;
    private final TransactionRepository transactionRepository;

    @Autowired
    public BankingServiceImpl(RestTemplate restTemplate, FabrickConfig config, TransactionRepository transactionRepository) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.transactionRepository = transactionRepository;
    }


    @Override
    public BalanceDTO getBalance() {
        String url = UriComponentsBuilder
                .fromHttpUrl(config.getBaseUrl())
                .path("/api/gbs/banking/v4.0/accounts/{accountId}/balance")
                .buildAndExpand(config.getAccountId())
                .toUriString();

        HttpEntity<?> entity = new HttpEntity<>(createHeaders());

        try {
            ResponseEntity<ApiResponse<BalanceDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            validateResponse(response.getBody());
            return response.getBody().getPayload();
        } catch (Exception e) {
            log.error("Error getting balance for account {}", config.getAccountId(), e);
            throw new BankingServiceException("Failed to retrieve balance", e);
        }
    }
    @Override
    public List<TransactionDTO> getTransactions(LocalDate fromDate, LocalDate toDate) {
        String url = UriComponentsBuilder
                .fromHttpUrl(config.getBaseUrl())
                .path("/api/gbs/banking/v4.0/accounts/{accountId}/transactions")
                .queryParam("fromAccountingDate", fromDate)
                .queryParam("toAccountingDate", toDate)
                .buildAndExpand(config.getAccountId())
                .toUriString();

        HttpEntity<?> entity = new HttpEntity<>(createHeaders());

        try {
            ResponseEntity<ApiResponse<List<TransactionDTO>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            validateResponse(response.getBody());
            return response.getBody().getPayload();
        } catch (Exception e) {
            log.error("Error getting transactions for account {} between {} and {}",
                    config.getAccountId(), fromDate, toDate, e);
            throw new BankingServiceException("Failed to retrieve transactions", e);
        }
    }

    @Override
    public MoneyTransferResponse createMoneyTransfer(MoneyTransferRequest request) {
        String url = UriComponentsBuilder
                .fromHttpUrl(config.getBaseUrl())
                .path("/api/gbs/banking/v4.0/accounts/{accountId}/payments/money-transfers")
                .buildAndExpand(config.getAccountId())
                .toUriString();

        HttpHeaders headers = createHeaders();
        HttpEntity<MoneyTransferRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ApiResponse<MoneyTransferResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        validateResponse(response.getBody());
        MoneyTransferResponse moneyTransferResponse = response.getBody().getPayload();

        // Persist transaction entity after the money transfer is done
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransferId(moneyTransferResponse.getMoneyTransferId());
        // Using the execution date from the request or you can also set it to LocalDate.now()
        transaction.setExecutionDate(request.getExecutionDate());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setDescription(request.getDescription());
        // Set creditor name and account code if available
        if (request.getCreditor() != null) {
            transaction.setCreditorName(request.getCreditor().getName());
            if (request.getCreditor().getAccount() != null) {
                transaction.setCreditorAccountCode(request.getCreditor().getAccount().getAccountCode());
            }
        }
        transaction.setStatus(moneyTransferResponse.getStatus());
        transactionRepository.save(transaction);
        //TODO:Check here if tests didnt run
        return moneyTransferResponse;
    }


    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Auth-Schema", config.getAuthSchema());
        headers.set("Api-Key", config.getApiKey());
        headers.set("X-Time-Zone", config.getTimeZone().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private void validateResponse(ApiResponse<?> response) {
        if (response == null) {
            throw new BankingServiceException("Null response received from API");
        }
        if (!"OK".equals(response.getStatus())) {
            String errorMessage = response.getErrors() != null && !response.getErrors().isEmpty()
                    ? response.getErrors().get(0).getDescription()
                    : "Unknown error occurred";
            throw new BankingServiceException(errorMessage);
        }
    }
}
