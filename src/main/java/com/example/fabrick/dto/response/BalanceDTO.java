package com.example.fabrick.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceDTO {

    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("availableBalance")
    private BigDecimal availableBalance;

    @JsonProperty("currency")
    private String currency;
}
