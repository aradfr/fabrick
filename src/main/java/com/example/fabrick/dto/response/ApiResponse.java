package com.example.fabrick.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {

    @JsonProperty("status")
    private String status;

    @JsonProperty("errors")
    private List<Error> errors;

    @JsonProperty("payload")
    private T payload;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {
        @JsonProperty("code")
        private String code;

        @JsonProperty("description")
        private String description;

        @JsonProperty("params")
        private String params;
    }
}
