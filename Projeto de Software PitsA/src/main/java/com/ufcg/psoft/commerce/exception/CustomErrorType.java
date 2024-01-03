package com.ufcg.psoft.commerce.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomErrorType {
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errors")
    private List<String> errors;
    public CustomErrorType(CommerceException e) {
        this.timestamp = LocalDateTime.now();
        this.message = e.getMessage();

        List<String> aux = new ArrayList<>();
        aux.add(e.getCause().getMessage()); //Era pra funcionar mais por algum motivo errors não é povoada de jeito nenhum

        this.errors = aux;
    }

}
