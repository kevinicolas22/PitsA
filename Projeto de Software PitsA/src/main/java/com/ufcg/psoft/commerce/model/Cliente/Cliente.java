package com.ufcg.psoft.commerce.model.Cliente;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.SaborPizza.SaborPizza;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "clientes")
public class Cliente {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,name = "pk_id_cliente")
    private long id;

    @JsonProperty("codigoAcesso")
    @Column(nullable = false, name = "desc_codigoAcesso_cliente")
    private String codigoAcesso;

    @JsonProperty("nome")
    @Column(nullable = false, name = "desc_nome_cliente")
    private String nome;

    @JsonProperty("endereco")
    @Column(nullable = false, name = "desc_endereco_cliente")
    private String endereco;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "saborPizza_pk_id")
    private SaborPizza subject ;

    public boolean isSubscribed(SaborPizza subject){
        if(this.subject != null){
            return this.subject.getSaborDaPizza().equals(subject.getSaborDaPizza());
        }
        return false;
    }

    public void subscribeTo(SaborPizza subject) {
        if(!isSubscribed(subject) && subject.getDisponibilidadeSabor()==false){
            subject.register(this);
            this.subject = subject;

        }
    }

    public void unsubscribeFrom(SaborPizza subject) {
        if (this.subject != null) {
            this.subject.unregister(this);
            this.subject = null;
        }
    }


    public void update(SaborPizza saborPizza) {
        if(this.subject == null) {
            System.out.println(this.nome + " você não está inscrito no sabor " + saborPizza.getSaborDaPizza());
            return;
        }
        System.out.println(this.nome + ", a pizza de " + saborPizza.getSaborDaPizza() + " agora está disponivel" );
    }

    public void notificaPedidoEmRota(Entregador entregador){
        System.out.println(" - PEDIDO EM ROTA - \n" +
                "Cliente: " + this.getNome() + "\n" +
                "Entregador: " + entregador.getNome() + "\n" +
                "Tipo do Veiculo: " + entregador.getTipoVeiculo() + "\n" +
                "Cor do Veiculo: " + entregador.getCorVeiculo() + "\n" +
                "Placa do Veiculo: " + entregador.getPlacaVeiculo() + "\n");
    }

    public void notificaSemEntregadores(){
        System.out.println(" Nao ha entregadores disponiveis no momento...\n" +
                            "- Seu pedido sera enviado assim que possivel!");
    }

}
