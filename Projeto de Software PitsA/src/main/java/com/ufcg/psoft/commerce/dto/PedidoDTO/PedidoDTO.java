package com.ufcg.psoft.commerce.dto.PedidoDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.validators.CodigoAcessoConstraint;
import com.ufcg.psoft.commerce.enums.MetodoPagamento;
import com.ufcg.psoft.commerce.enums.StatusPedido;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.SaborPizza.Pizza;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    @Getter
    @JsonProperty("id")
    private Long id;

    @CodigoAcessoConstraint
    @JsonProperty("codigoAcesso")
    @NotBlank(message = "O código de acesso não pode estar vazio")
    private String codigoAcesso;

    @JsonProperty("clienteId")
    @NotNull(message = "O ID do cliente não pode estar vazio")
    private Long clienteId;

    @JsonProperty("estabelecimentoId")
    @NotNull(message = "O ID do estabelecimento não pode estar vazio")
    private Long estabelecimentoId;

    @JsonProperty("pizzas")
    private List<Pizza> pizzas;

    @JsonProperty("enderecoEntrega")
    private String enderecoEntrega;

    @JsonProperty("metodoPagamento")
    @NotNull(message = "o metodo de pagamento nao pode ser nulo")
    private MetodoPagamento metodoPagamento;

    @JsonProperty("status")
    private StatusPedido statusPedido;

    @JsonProperty("entregador")
    private Entregador entregador;
}

