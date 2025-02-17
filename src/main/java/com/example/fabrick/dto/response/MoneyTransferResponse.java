package com.example.fabrick.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoneyTransferResponse {

    @JsonProperty("moneyTransferId")
    private String moneyTransferId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("creditor")
    private Creditor creditor;

    @JsonProperty("debtor")
    private Debtor debtor;

    @JsonProperty("cro")
    private String cro;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("trn")
    private String trn;

    @JsonProperty("description")
    private String description;

    @JsonProperty("createdDatetime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private LocalDateTime createdDatetime;

    @JsonProperty("accountedDatetime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private LocalDateTime accountedDatetime;

    @JsonProperty("debtorValueDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate debtorValueDate;

    @JsonProperty("creditorValueDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creditorValueDate;

    @JsonProperty("amount")
    private Amount amount;

    @JsonProperty("isUrgent")
    private boolean urgent;

    @JsonProperty("isInstant")
    private boolean instant;

    @JsonProperty("fees")
    private List<Fee> fees;

    @JsonProperty("hasTaxRelief")
    private Boolean hasTaxRelief;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Creditor {
        @JsonProperty("name")
        private String name;

        @JsonProperty("account")
        private Account account;

        @JsonProperty("address")
        private Address address;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Debtor {
        @JsonProperty("name")
        private String name;

        @JsonProperty("account")
        private Account account;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Account {
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

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        @JsonProperty("debtorAmount")
        private BigDecimal debtorAmount;

        @JsonProperty("creditorAmount")
        private BigDecimal creditorAmount;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("conversionRate")
        private BigDecimal conversionRate;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fee {
        @JsonProperty("feeCode")
        private String feeCode;

        @JsonProperty("description")
        private String description;

        @JsonProperty("amount")
        private BigDecimal amount;

        @JsonProperty("currency")
        private String currency;
    }
}
