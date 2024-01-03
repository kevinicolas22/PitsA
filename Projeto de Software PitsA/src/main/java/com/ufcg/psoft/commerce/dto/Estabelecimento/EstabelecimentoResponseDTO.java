package com.ufcg.psoft.commerce.dto.Estabelecimento;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Cliente.Cliente;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.SaborPizza.SaborPizza;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstabelecimentoResponseDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("codigoAcesso")
    private String codigoAcesso;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("clientes")
    private Set<Cliente> clientes;

    @JsonProperty("saboresPizza")
    private Set<SaborPizza> saboresPizza;

    @JsonProperty("entregadores")
    private Set<Entregador> entregadores;


}
