package com.ufcg.psoft.commerce.model.Pedido;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.MetodoPagamento;
import com.ufcg.psoft.commerce.enums.StatusPedido;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.Estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.SaborPizza.Pizza;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "pedidos")
public class Pedido {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "pk_id_pedido")
    private long id;

    @JsonProperty("codigoAcesso")
    @Column(nullable = false, name = "desc_codigoAcesso_pedido")
    private String codigoAcesso;

    @JsonProperty("clienteId")
    @JoinColumn(name = "fk_id_cliente")
    private Long cliente;

    @JsonProperty("estabelecimentoId")
    @JoinColumn(name = "fk_id_estabelecimento")
    private Long estabelecimento;

    @JsonProperty("entregador")
    @ManyToOne
    @JoinColumn(name = "fk_id_entregador")
    private Entregador entregador;

    @JsonProperty("pizzas")
    @OneToMany(mappedBy = "pedido")
    private List<Pizza> pizzas;

    @JsonProperty("enderecoEntrega")
    @Column(name = "desc_endereco_entrega")
    private String enderecoEntrega;

    @JsonProperty("metodoPagamento")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "enum_metodo_pagamento")
    private MetodoPagamento metodoPagamento;

    @JsonProperty("valorTotal")
    @Column(nullable = false, name = "num_valor_total")
    private double valorTotal;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "enum_status_pedido")
    private StatusPedido status;

    @JsonProperty("codigoAcessoEstabelecimento")
    @Column(name = "desc_codigo_acesso_estabelecimento")
    private String codigoAcessoEstabelecimento;

    @Getter
    @JsonProperty("dataPedido")
    @Column(name = "data_pedido")
    private Date dataPedido;


    @JsonIgnore
    @OneToMany(mappedBy = "subjectPedido", cascade = CascadeType.ALL)
    private List<Estabelecimento> observersEstabelecimento;


    public void addObserver(Estabelecimento estabelecimento1) {

        observersEstabelecimento.add(estabelecimento1);

    }

    public int tamanhoObserverEstabelecmento(){

        return observersEstabelecimento.size();

    }


    public void removeObserverEstabelecimento(Estabelecimento estabelecimento){

        observersEstabelecimento.remove(estabelecimento);

    }

    public void notificaOsObservers(){

        for (Estabelecimento e : observersEstabelecimento){
            e.update(this);

        }

    }


}


