package com.ufcg.psoft.commerce.model.SaborPizza;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.TamanhoPizza;
import com.ufcg.psoft.commerce.exception.Pizza.TamanhoPizzaInvalidoException;
import com.ufcg.psoft.commerce.model.Pedido.Pedido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "pizzas")
public class Pizza {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "pk_id_pizza")
    private Long id;

    @JsonProperty("sabor1")
    @ManyToOne
    private SaborPizza sabor1;

    @JsonProperty("sabor2")
    @ManyToOne
    private SaborPizza sabor2;

    @JsonProperty("tamanho")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "desc_tamanho")
    @Getter
    private TamanhoPizza tamanho;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    public double calcularValorPizza() {
        double valorTotal = 0.0;

        if (this.tamanho == TamanhoPizza.GRANDE) {
            valorTotal += sabor1.getValorGrande();

            if (sabor2 != null) {
                valorTotal += sabor2.getValorGrande();
                valorTotal /= 2;
            }
        } else if (this.tamanho == TamanhoPizza.MEDIA) {
            valorTotal = sabor1.getValorMedia();
        } else {
            throw new TamanhoPizzaInvalidoException();
        }

        return valorTotal;
    }
}
