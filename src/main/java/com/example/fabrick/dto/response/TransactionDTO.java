package com.example.fabrick.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDTO {

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("operationId")
    private String operationId;

    @JsonProperty("accountingDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate accountingDate;

    @JsonProperty("valueDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate valueDate;

    @JsonProperty("type")
    private TransactionType type;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("description")
    private String description;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionType {
        @JsonProperty("enumeration")
        private String enumeration;

        @JsonProperty("value")
        private String value;
    }
}
