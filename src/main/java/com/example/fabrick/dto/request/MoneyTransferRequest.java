package com.example.fabrick.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoneyTransferRequest {

    @NotBlank(message = "Receiver name is required")
    @JsonProperty("receiverName")
    private String receiverName;

    @JsonProperty("description")
    @NotBlank(message = "Description is required")
    private String description;

    @JsonProperty("currency")
    @NotBlank(message = "Currency is required")
    private String currency;

    @JsonProperty("amount")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @JsonProperty("executionDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Execution date is required")
    private LocalDate executionDate;

    @JsonProperty("creditor")
    @NotNull(message = "Creditor details are required")
    private Creditor creditor;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Creditor {
        @NotBlank(message = "Creditor name is required")
        @JsonProperty("name")
        private String name;

        @NotNull(message = "Creditor account is required")
        @JsonProperty("account")
        private Account account;

        @JsonProperty("address")
        private Address address;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Account {
        @NotBlank(message = "Account code (IBAN) is required")
        @JsonProperty("accountCode")
        private String accountCode;

        @JsonProperty("bicCode")
        private String bicCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        @JsonProperty("address")
        private String address;

        @JsonProperty("city")
        private String city;

        @JsonProperty("countryCode")
        private String countryCode;
    }
}