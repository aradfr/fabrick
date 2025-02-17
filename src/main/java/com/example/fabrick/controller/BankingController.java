package com.example.fabrick.controller;

import com.example.fabrick.dto.request.MoneyTransferRequest;
import com.example.fabrick.dto.response.ApiResponse;
import com.example.fabrick.dto.response.BalanceDTO;
import com.example.fabrick.dto.response.MoneyTransferResponse;
import com.example.fabrick.dto.response.TransactionDTO;
import com.example.fabrick.service.BankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/banking")
@Tag(name = "Banking Operations", description = "APIs for banking operations")
@Slf4j
public class BankingController {

    private final BankingService bankingService;
    private TimeZone timeZone;

    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
        this.timeZone = TimeZone.getDefault();
    }

    @GetMapping("/balance")
    @Operation(summary = "Get account balance", description = "Retrieves the current balance for account")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Balance retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BalanceDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad request"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<ApiResponse<BalanceDTO>> getBalance() {
        log.info("Retrieving account balance");
        BalanceDTO balance = bankingService.getBalance();
        return ResponseEntity.ok(createSuccessResponse(balance));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get account transactions", description = "Retrieves transactions for the specified date range")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TransactionDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid date range"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        log.info("Retrieving transactions from {} to {}", fromDate, toDate);
        validateDateRange(fromDate, toDate);
        List<TransactionDTO> transactions = bankingService.getTransactions(fromDate, toDate);
        return ResponseEntity.ok(createSuccessResponse(transactions));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Create money transfer", description = "Creates a new money transfer")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Money transfer created successfully",
                    content = @Content(schema = @Schema(implementation = MoneyTransferResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid transfer request"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<ApiResponse<MoneyTransferResponse>> createMoneyTransfer(
            @Valid @RequestBody MoneyTransferRequest request) {

        log.info("Creating money transfer for amount: {}", request.getAmount());
        MoneyTransferResponse response = bankingService.createMoneyTransfer(request);
        return ResponseEntity.ok(createSuccessResponse(response));
    }

    @ModelAttribute
    public void setResponseHeader(@RequestHeader(value = "Time-Zone", required = false) String timeZoneHeader) {
        if (timeZoneHeader != null && !timeZoneHeader.isEmpty()) {
            try {
                this.timeZone = TimeZone.getTimeZone(timeZoneHeader);
                log.debug("Time zone set to: {}", this.timeZone.getID());
            } catch (Exception e) {
                log.warn("Invalid timezone header: {}. Using default timezone.", timeZoneHeader);
            }
        }
    }

    private <T> ApiResponse<T> createSuccessResponse(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus("OK");
        response.setPayload(data);
        return response;
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }
        if (toDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("To date cannot be in the future");
        }
    }
}