package com.ufcg.psoft.commerce.model.Estabelecimento;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.StatusPedido;
import com.ufcg.psoft.commerce.exception.Pizza.SaborPizzaNaoEncontradoException;
import com.ufcg.psoft.commerce.model.Cliente.Cliente;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.Pedido.Pedido;
import com.ufcg.psoft.commerce.model.SaborPizza.SaborPizza;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Estabelecimento")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Estabelecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    @Column(name = "estabelecimento_id")
    private Long id;

    @JsonProperty("codigoAcesso")
    @Column(name = "codigo_acesso")
    private String codigoAcesso;

    @JsonProperty("nome")
    @Column(name = "nome_estabelecimento")
    private String nome;

    @OneToMany(cascade=CascadeType.PERSIST)
    private Set<SaborPizza> saboresPizza;

    @OneToMany(cascade=CascadeType.PERSIST)
    private Set<Cliente> clientes;


    @JsonIgnore
    @JoinColumn(name = "estabelecimento_id")
    @OneToMany(cascade= CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<Entregador> entregadores;

    @JsonIgnore
    @JoinColumn(name = "estabelecimento_id")
    @OneToMany(cascade= CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Pedido> pedidosEspera;



    //Funcoes de Estabelecimento, toda operação feita em estabelecimento tem que ser forneciado o codigo de acesso
    //do estabelecimento

    public Cliente clientePorCodigoAcesso(String codigoAcessoCliente, String codigoAcessoEstabelecimento){
        Cliente cliente = null;

        if (codigoAcessoEstabelecimento.equals(this.codigoAcesso)){

            for(Cliente c : clientes){

                if(c.getCodigoAcesso().equals(codigoAcessoCliente)){

                    cliente = c;
                    break;
                }
            }
        }
        return cliente;
    }

    @JsonIgnore
    public Entregador getEntregadorDisponivel() {
        for(Entregador entregador: entregadores){
            if (entregador.isDisponibilidade()){
                return entregador;
            }
        }
        return null;
    }

    @JsonIgnore
    public List<Entregador> getEntregadoresDisponiveis(){
        List<Entregador> retorno= new ArrayList<>();
        for(Entregador entregadorr:entregadores){
            if(entregadorr.isDisponibilidade() && entregadorr.getEstadoDeDisposicao().equals("Ativo")){
                retorno.add(entregadorr);
            }
        }
        return  retorno;
    }




    public Entregador entregadorPorCodigoAcesso(String codigoAcessoEntregador, String codigoAcessoEstabelecimento){
        Entregador entregadorEncontrado = null;

        if(codigoAcessoEstabelecimento.equals(this.codigoAcesso)){


            for (Entregador entregador : this.entregadores){

                if(entregador.getCodigoAcesso().equals(codigoAcessoEntregador)){

                    entregadorEncontrado = entregador;
                    break;
                }
            }
        }
        return entregadorEncontrado;

    }



    public SaborPizza setDisponibilidadeSabor(Long idSaborPizza, boolean disponibilidade){
        for (SaborPizza sabor: saboresPizza) {
            if(sabor.getIdPizza().equals(idSaborPizza)){
                sabor.setDisponibilidadeSabor(disponibilidade);
                if(disponibilidade== true){
                    sabor.notifyObservers();
                }
                return sabor;
            }
        }
        throw new SaborPizzaNaoEncontradoException();

    }

    public SaborPizza getSaborPizzaById(Long id){
        for (SaborPizza sabor: saboresPizza) {
            if(sabor.getIdPizza().equals(id)){

                return sabor;
            }
        }
        throw new SaborPizzaNaoEncontradoException();
    }




    public void aprovarEntregador(Entregador entregador){

        entregadores.add(entregador);

    }

    public void update(Pedido pedido){

        if(pedido == null){

            System.out.println("Você não tem nenhuma notificação.");

        }else{

            System.out.println("Pedido Entregue!");

        }

    }



    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pedido_pk_id")
    private Pedido subjectPedido;

    public boolean isSubcribe(Pedido pedido){
        boolean resultado = false;

        if(this.subjectPedido != null){
            resultado = this.subjectPedido.getEstabelecimento().equals(pedido.getEstabelecimento());
        }
        return resultado;
    }

    public void subscribeTo(Pedido pedido){

        if(!isSubcribe(pedido) && !pedido.getStatus().equals(StatusPedido.PEDIDO_ENTREGUE)){

            pedido.addObserver(this);
            this.subjectPedido = pedido;

        }

    }

    public void unsubcriteTo(Pedido pedido){
        if(pedido != null){

        subjectPedido.removeObserverEstabelecimento(this);
        this.subjectPedido = null;
        }

    }




}