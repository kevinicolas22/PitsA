package com.ufcg.psoft.commerce.model.Associacao;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Associacao")
public class Associacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    @Column(name = "associacao_id")
    private Long id;

    @JsonProperty("idEstabelecimento")
    @Column(name = "estabelecimento_id")
    private Long estabelecimentoId;

    @JsonProperty("idEntregador")
    @Column(name = "entregador_id")
    private Long entregadorId;

    @JsonProperty("codigoAcessoEntregador")
    @Column(name = "desc_codigo_acesso_entregador")
    private String codigoAcesso;

    @JsonProperty("status")
    @Column(name = "desc_status")
    private Boolean status;

}
