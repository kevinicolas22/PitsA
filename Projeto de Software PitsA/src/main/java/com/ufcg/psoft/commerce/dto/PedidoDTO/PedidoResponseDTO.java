package com.ufcg.psoft.commerce.dto.PedidoDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.validators.CodigoAcessoConstraint;
import com.ufcg.psoft.commerce.enums.MetodoPagamento;
import com.ufcg.psoft.commerce.enums.StatusPedido;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.SaborPizza.Pizza;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    @Getter
    @JsonProperty("id")
    private Long id;

    @CodigoAcessoConstraint
    @JsonProperty("codigoAcesso")
    private String codigoAcesso;

    @JsonProperty("clienteId")
    private Long clienteId;

    @JsonProperty("estabelecimentoId")
    private Long estabelecimentoId;

    @JsonProperty("pizzas")
    private List<Pizza> pizzas;

    @JsonProperty("enderecoEntrega")
    private String enderecoEntrega;

    @JsonProperty("metodoPagamento")
    private MetodoPagamento metodoPagamento;

    @JsonProperty("status")
    private StatusPedido statusPedido;

    @JsonProperty("entregador")
    private Entregador entregador;

}
