package com.ufcg.psoft.commerce.model.Entregador;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name= "entregadores")
public class Entregador implements Comparable<Entregador> {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,name = "pk_id_entregador")
    private Long id;//


    @JsonProperty("codigoAcesso")
    @Column(nullable = false, name = "codigoAcesso_entregador")
    private String codigoAcesso;

    @JsonProperty("nome")
    @Column(nullable = false, name = "nome_entregador")
    private String nome;

    @JsonProperty("placaVeiculo")
    @Column(nullable = false, name = "placaVeiculo_entregador")
    private String placaVeiculo;

    @JsonProperty("tipoVeiculo")
    @Column(nullable = false, name = "tipoVeiculo_entregador")
    private String tipoVeiculo;

    @JsonProperty("corVeiculo")
    @Column(nullable = false, name = "corVeiculo_entregador")
    private String corVeiculo;

    @JsonProperty("aprovado")
    @Column(nullable = false, name = "desc_aprovaçap_entregador")
    private boolean aprovado;

    @JsonProperty("isDisponibilidade")
    @Column(nullable = false, name = "desc_disponibilidade_entregador")
    private boolean isDisponibilidade;

    @JsonProperty("EstadodeDisponibilidade")
    @Column(name = "desc_estado_diponibilidade")
    private String estadoDeDisposicao;

    @JsonIgnore
    @JsonProperty("tempoDisponivel")
    @Column(name = "desc_tempo_disponivel")
    private LocalDateTime tempoDisponivel;


    @Override
    public int compareTo(Entregador outroEntregador) {
        if (this.tempoDisponivel.isBefore(outroEntregador.getTempoDisponivel())) {
            return -1; // Retorna um número negativo se this for menor que outroEntregador
        } else if (this.tempoDisponivel.isAfter(outroEntregador.getTempoDisponivel())) {
            return 1; // Retorna um número positivo se this for maior que outroEntregador
        } else {
            return 0; // Retorna 0 se this for igual a outroEntregador
        }
    }
}