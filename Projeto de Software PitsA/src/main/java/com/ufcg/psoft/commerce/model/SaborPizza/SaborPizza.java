package com.ufcg.psoft.commerce.model.SaborPizza;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Cliente.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "saboresPizza")
@EqualsAndHashCode(of = {"saborDaPizza", "tipoDeSabor"})
public class SaborPizza {


    @JsonProperty("idPizza")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_sabor")
    private Long idPizza;

    @JsonProperty("saborDaPizza")
    @Column(nullable = false, name = "desc_saborDaPizza")
    private String saborDaPizza;

    @JsonProperty("valorMedia")
    @Column(name = "double_valor_media", nullable = false)
    private double valorMedia;

    @JsonProperty("valorGrande")
    @Column(name = "double_valor_grande", nullable = false)
    @Positive(message = "PrecoG deve ser maior que zero")
    private double valorGrande;

    @JsonProperty("tipoDeSabor")
    @Column(nullable = false, name = "desc_tipoDeSabor")
    private String tipoDeSabor;

    private Boolean disponibilidadeSabor;

    @JsonIgnore
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Cliente> observers;

    public void register(Cliente observer) {
        this.observers.add(observer);
    }

    public int observersSize(){
        return observers.size();
    }

    public void unregister(Cliente observer) {
        if(this.observers.contains(observer)) {
            this.observers.remove(observer);
        }
    }
    public void notifyObservers() {
        if (disponibilidadeSabor != null) {
            for (Cliente c : observers) {
                c.update(this);
            }
        }
    }



}