package com.ufcg.psoft.commerce.dto.Entregador;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregadorPostPutRequestDTO {

    @JsonProperty("id")//
    private Long id;

    @JsonProperty("codigoAcesso")
    @NotBlank(message = "Codigo de acesso obrigatorio")
    @NotNull(message = "codigo de acesso obrigatorio")
    private String codigoAcesso;

    @JsonProperty("nome")
    @NotBlank(message = "Nome e obrigatorio")
    @NotNull(message = "O nome do entregador não pode ser nulo")
    private String nome;

    @JsonProperty("placaVeiculo")
    @NotBlank(message = "Placa do veiculo e obrigatoria")
    private String placaVeiculo;

    @JsonProperty("tipoVeiculo")
    @NotBlank(message = "Tipo do veiculo e obrigatorio")

    private String tipoVeiculo;

    @JsonProperty("corVeiculo")
    @NotBlank(message = "Cor do veiculo e obrigatoria")
    private String corVeiculo;

    @JsonProperty("aprovado")
    private boolean aprovado;


    @JsonProperty("isDisponibilidade")
    public boolean isDisponibilidade;

    @JsonProperty("EstadodeDisponibilidade")
    public String EstadoDeDisponibilidade;
}
