package com.example.fabrick.controller;

import com.example.fabrick.dto.request.MoneyTransferRequest;
import com.example.fabrick.dto.response.ApiResponse;
import com.example.fabrick.dto.response.BalanceDTO;
import com.example.fabrick.dto.response.MoneyTransferResponse;
import com.example.fabrick.dto.response.TransactionDTO;
import com.example.fabrick.service.BankingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;












@ExtendWith(MockitoExtension.class)
class BankingControllerTest {

    @InjectMocks
    private BankingController bankingController;

    @Mock
    private BankingService bankingService;

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

    @Test
    void getBalance_ShouldReturnBalance() {
        // Arrange
        BalanceDTO expectedBalance = new BalanceDTO();
        expectedBalance.setBalance(new BigDecimal("1000.00"));
        when(bankingService.getBalance()).thenReturn(expectedBalance);

        // Act
        ResponseEntity<ApiResponse<BalanceDTO>> response = bankingController.getBalance();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("OK");
        assertThat(response.getBody().getPayload()).isEqualTo(expectedBalance);
    }

    @Test
    void getTransactions_WithValidDateRange_ShouldReturnTransactions() {
        // Arrange
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now();
        List<TransactionDTO> expectedTransactions = List.of(new TransactionDTO());
        when(bankingService.getTransactions(fromDate, toDate)).thenReturn(expectedTransactions);

        // Act
        ResponseEntity<ApiResponse<List<TransactionDTO>>> response =
                bankingController.getTransactions(fromDate, toDate);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("OK");
        assertThat(response.getBody().getPayload()).isEqualTo(expectedTransactions);
    }

    @Test
    void getTransactions_WithInvalidDateRange_ShouldThrowException() {
        // Arrange
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().minusDays(1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                bankingController.getTransactions(fromDate, toDate));
    }

    @Test
    void createMoneyTransfer_WithValidRequest_ShouldReturnResponse() {
        // Arrange
        MoneyTransferRequest request = createValidMoneyTransferRequest();
        MoneyTransferResponse expectedResponse = new MoneyTransferResponse();
        when(bankingService.createMoneyTransfer(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MoneyTransferResponse>> response =
                bankingController.createMoneyTransfer(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("OK");
        assertThat(response.getBody().getPayload()).isEqualTo(expectedResponse);
    }
}