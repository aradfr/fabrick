package com.example.fabrick.service;

import com.example.fabrick.config.FabrickConfig;
import com.example.fabrick.dto.request.MoneyTransferRequest;
import com.example.fabrick.dto.response.ApiResponse;
import com.example.fabrick.dto.response.BalanceDTO;
import com.example.fabrick.dto.response.MoneyTransferResponse;
import com.example.fabrick.dto.response.TransactionDTO;
import com.example.fabrick.exception.BankingServiceException;
import com.example.fabrick.repository.TransactionRepository;
import com.example.fabrick.service.BankingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;


import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BankingServiceImplTest {

    @InjectMocks
    private BankingServiceImpl bankingService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private FabrickConfig config;

    @Mock
    private TransactionRepository transactionRepository;


    private MoneyTransferRequest createValidMoneyTransferRequest() {
        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setReceiverName("John Doe");
        request.setDescription("Test Transfer");
        request.setCurrency("EUR");
        request.setAmount(new BigDecimal("1000.00"));
        request.setExecutionDate(LocalDate.now());

        MoneyTransferRequest.Creditor creditor = new MoneyTransferRequest.Creditor();
        creditor.setName("John Doe");

        MoneyTransferRequest.Account account = new MoneyTransferRequest.Account();
        account.setAccountCode("IT23A0336844430152923804660");
        account.setBicCode("SELBIT2BXXX");
        creditor.setAccount(account);

        MoneyTransferRequest.Address address = new MoneyTransferRequest.Address();
        address.setAddress("Via Roma 1");
        address.setCity("Rome");
        address.setCountryCode("IT");
        creditor.setAddress(address);

        request.setCreditor(creditor);
        return request;
    }

    @BeforeEach
    void setUp() {
        when(config.getBaseUrl()).thenReturn("https://sandbox.platfr.io");
        when(config.getAccountId()).thenReturn(Long.valueOf("14537780"));
        when(config.getAuthSchema()).thenReturn("S2S");
        when(config.getApiKey()).thenReturn("FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP");
        when(config.getTimeZone()).thenReturn(TimeZone.getDefault().toZoneId());

    }

    @Test
    void getBalance_ShouldReturnBalance() {
        // Arrange
        BalanceDTO expectedBalance = new BalanceDTO();
        expectedBalance.setBalance(new BigDecimal("1000.00"));
        expectedBalance.setAvailableBalance(new BigDecimal("1000.00"));
        expectedBalance.setCurrency("EUR");

        ApiResponse<BalanceDTO> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("OK");
        apiResponse.setPayload(expectedBalance);

        ResponseEntity<ApiResponse<BalanceDTO>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        BalanceDTO result = bankingService.getBalance();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(result.getCurrency()).isEqualTo("EUR");
    }

    @Test
    void getTransactions_ShouldReturnTransactions() {
        // Arrange
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now();

        TransactionDTO transaction = new TransactionDTO();
        transaction.setTransactionId("1234567890");
        transaction.setOperationId("9876543210");
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setCurrency("EUR");

        List<TransactionDTO> expectedTransactions = List.of(transaction);

        ApiResponse<List<TransactionDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("OK");
        apiResponse.setPayload(expectedTransactions);

        ResponseEntity<ApiResponse<List<TransactionDTO>>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        List<TransactionDTO> result = bankingService.getTransactions(fromDate, toDate);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTransactionId()).isEqualTo("1234567890");
        assertThat(result.get(0).getAmount()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    void createMoneyTransfer_WithValidRequest_ShouldCreateTransfer() {
        // Arrange
        MoneyTransferRequest request = createValidMoneyTransferRequest();

        MoneyTransferResponse expectedResponse = new MoneyTransferResponse();
        expectedResponse.setMoneyTransferId("1234567890");
        expectedResponse.setStatus("EXECUTED");

        ApiResponse<MoneyTransferResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("OK");
        apiResponse.setPayload(expectedResponse);

        ResponseEntity<ApiResponse<MoneyTransferResponse>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                argThat(entity -> {
                    HttpEntity<?> httpEntity = (HttpEntity<?>) entity;
                    return httpEntity.getBody() instanceof MoneyTransferRequest &&
                            ((MoneyTransferRequest) httpEntity.getBody()).getAmount()
                                    .equals(new BigDecimal("1000.00"));
                }),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        MoneyTransferResponse result = bankingService.createMoneyTransfer(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMoneyTransferId()).isEqualTo("1234567890");
        assertThat(result.getStatus()).isEqualTo("EXECUTED");
    }
    @Test
    void createMoneyTransfer_WhenApiReturnsError_ShouldThrowException() {
        // Arrange
        MoneyTransferRequest request = createValidMoneyTransferRequest();

        ApiResponse<MoneyTransferResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("KO");
        ApiResponse.Error error = new ApiResponse.Error();
        error.setCode("API000");
        error.setDescription("Errore tecnico  La condizione BP049 non e' prevista per il conto id 14537780");
        apiResponse.setErrors(List.of(error));

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(apiResponse));

        // Act & Assert
        BankingServiceException exception = assertThrows(
                BankingServiceException.class,
                () -> bankingService.createMoneyTransfer(request)
        );

        assertThat(exception.getMessage()).contains("BP049");
    }


}