package com.ufcg.psoft.commerce.controller.Estabelecimento;


import com.ufcg.psoft.commerce.dto.Estabelecimento.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.PizzaDTO.SaborResponseDTO;
import com.ufcg.psoft.commerce.enums.TipoDeSabor;
import com.ufcg.psoft.commerce.exception.Entregador.EntregadorNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Estabelecimento.CodigoAcessoEstabelecimentoException;
import com.ufcg.psoft.commerce.exception.Estabelecimento.CodigoAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.Estabelecimento.EstabelecimentoNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Pedido.PedidoCodigoAcessoIncorretoException;
import com.ufcg.psoft.commerce.model.Cliente.Cliente;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.Estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.Pedido.Pedido;
import com.ufcg.psoft.commerce.service.Estabelecimento.EstabelecimentoV1Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(value = "/estabelecimentos",  produces = MediaType.APPLICATION_JSON_VALUE)
public class EstabelecimentoV1Controller {

    @Autowired
    private EstabelecimentoV1Service estabelecimentov1Service;

    final String msgErro = "Nenhum estabelecimento encontrado com esse id";

    @PostMapping
    public ResponseEntity<?> post (@RequestBody EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO,
                                   @RequestParam String codigoAcesso){

        ResponseEntity<?> response;



        try {
            estabelecimentoPostPutRequestDTO.setCodigoAcesso(codigoAcesso);
            Estabelecimento estabelecimento = estabelecimentov1Service.add(estabelecimentoPostPutRequestDTO);
            response = ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(estabelecimento);
        }catch (CodigoAcessoInvalidoException codigoAcessoInvalidoException){

            throw new CodigoAcessoInvalidoException();

        }
        return response;

    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") long id){

        ResponseEntity<?> response;

        try{
            Estabelecimento estabelecimento = estabelecimentov1Service.getOne(id);
            response = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(estabelecimento);

        }catch (EstabelecimentoNaoEncontradoException estabelecimento){


            response = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(msgErro);
        }

        return response;

    }

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentov1Service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put (@PathVariable("id") long id, @RequestBody EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO) throws EstabelecimentoNaoEncontradoException{

        ResponseEntity<?> response;

        try {
            Estabelecimento estabelecimento = estabelecimentov1Service.put(id, estabelecimentoPostPutRequestDTO);

            response = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(estabelecimento);

        }catch (EstabelecimentoNaoEncontradoException estabelecimentoNaoEncontradoException){

            response = ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(msgErro);

        }catch (CodigoAcessoEstabelecimentoException e){

            response = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Codigo de acesso inválido!");
        }

        return response;
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id){
        Estabelecimento estabelecimento = estabelecimentov1Service.getOne(id);
        ResponseEntity<?> response;


        if(estabelecimento == null){

            response = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(msgErro);

        }else{
            estabelecimentov1Service.delete(id);
            response = ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }
        return response;
    }

    @RequestMapping(value = "consultaCliente/{codigoAcessoEstabelecimento}/{CodigoAcessoCliente}", method = RequestMethod.GET)
    public ResponseEntity<?> consultaCliente(@PathVariable("codigoAcessoEstabelecimento") String codigoAcessoEstabelecimento,
                                             @PathVariable("CodigoAcessoCliente") String codigoAcessoCliente) throws EstabelecimentoNaoEncontradoException {


        Estabelecimento estabelecimento = estabelecimentov1Service.getByCodigoAcesso(codigoAcessoEstabelecimento);

        ResponseEntity<?> response;

        if(estabelecimento != null){

            Cliente cliente = estabelecimento.clientePorCodigoAcesso(codigoAcessoCliente, codigoAcessoEstabelecimento);

            if(cliente != null){

                response = ResponseEntity
                        .status(HttpStatus.OK)
                        .body(cliente);

            }else{

                response = ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Codigo de acesso de cliente, ou estabelecimento, errado!");

            }

        }else{


            response = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(msgErro);

        }

        return response;

    }


    @RequestMapping(value = "consultaEntregador/{codigoAcessoEstabelecimento}/{codigoAcessoEntregador}", method = RequestMethod.GET)
    public ResponseEntity<?> consultaEntregador(@PathVariable("codigoAcessoEstabelecimento") String codigoAcessoEstabelecimento,
                                                @PathVariable("codigoAcessoEntregador") String codigoAcessoEntregador) throws EstabelecimentoNaoEncontradoException {

        Estabelecimento estabelecimento = estabelecimentov1Service.getByCodigoAcesso(codigoAcessoEstabelecimento);

        ResponseEntity<?> response;

        if(estabelecimento != null){
            Entregador entregador = estabelecimento.entregadorPorCodigoAcesso(codigoAcessoEntregador, codigoAcessoEstabelecimento);

            if(entregador != null){

                response = ResponseEntity
                        .status(HttpStatus.OK)
                        .body(entregador);

            }else{

                response = ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Codigo de acesso do entregador, ou estabelecimento, incorreto!");

            }



        }else{

            response = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(msgErro);

        }

        return response;


    }


    @RequestMapping(value = "/{id}/SaborPizzaes", method = RequestMethod.GET)
    public ResponseEntity<?> recuperarSabores(@PathVariable("id") long id, @RequestBody EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO){

        ResponseEntity<?> response;

        try {

            Set<SaborResponseDTO> sabor = estabelecimentov1Service.recuperarSabores(id, estabelecimentoPostPutRequestDTO);

            response = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(sabor);

        }catch (EstabelecimentoNaoEncontradoException estabelecimentoNaoEncontradoException){

            throw new EstabelecimentoNaoEncontradoException();
        }

        return response;

    }

    @RequestMapping(value = "/{id}/SaborPizzaes/tipoDeSabor", method = RequestMethod.GET)
    public ResponseEntity<?> recuperarSaboresPorTipo(@PathVariable("id") long id,
                                                     @RequestBody EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO,
                                                     @RequestParam("SaborPizza") TipoDeSabor tipoPizza){

        ResponseEntity<?> response;
        try {

            Set<SaborResponseDTO> sabor = estabelecimentov1Service.recuperarSaboresPorTipo(id,
                    estabelecimentoPostPutRequestDTO,
                    tipoPizza);

            response = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(sabor);

        }catch (EstabelecimentoNaoEncontradoException estabelecimentoNaoEncontradoException){

            response = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(msgErro);

        }

        return response;

    }
    @GetMapping("{id}/sabores/disponibilidade")
    ResponseEntity getCardapioDisponibilidade(
            @Param("codigoAcesso") String codigoAcesso,
            @PathVariable("id") Long id,
            @Param("disponibilidade") Boolean disponibilidade
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentov1Service.getCardapioDisponibilidade(id,codigoAcesso,disponibilidade));
    }
    @GetMapping("{id}/sabores")
    ResponseEntity getCardapioCompleto(
            @Param("codigoAcesso") String codigoAcesso,
            @PathVariable("id") Long id
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentov1Service.getCardapioCompleto(id,codigoAcesso));
    }


    @GetMapping("{id}/sabores/tipo")
    ResponseEntity listarCardapioPorTipoDePizza(
            @Param("codigoAcesso") String codigoAcesso,
            @PathVariable("id") Long id,
            @Param("tipo") String tipo
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentov1Service.listarCardapioPorTipoDePizza(id,codigoAcesso,tipo));
    }

    @PutMapping("{id}/disponibilidade")
    ResponseEntity<?> alterarDisponibilidadeSaborCardapio(
            @Valid @PathVariable("id") Long id,
            @Valid @RequestParam("idSaborPizza") Long idSaborPizza,
            @Valid @RequestParam("codigoAcesso") String codigoAcesso,
            @Valid @RequestParam("disponibilidade") boolean disponibilidade
    ){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentov1Service.alterarDisponibilidadeSaborPizza(id,idSaborPizza,codigoAcesso,disponibilidade));
    }

    @PutMapping("{id}/pedido-pronto")
    ResponseEntity<?> confirmarPedidoPronto(
            @Valid @PathVariable("id") Long id,
            @Valid @RequestParam("idPedido") Long idPedido,
            @Valid @RequestParam("codigoAcessoEstabelecimento") String codigoAcesso
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentov1Service.confirmarPreparacaoPedido(id,codigoAcesso,idPedido));

    }

    @PutMapping("{id}/envia-pedido")
    ResponseEntity<?> enviaPedido(
            @Valid @PathVariable("id") Long id,
            @Valid @RequestParam("codigoAcesso") String codigoAcesso,
            @Valid @RequestParam("idPedido") Long idPedido){
        return ResponseEntity.status(HttpStatus.OK)
                .body(estabelecimentov1Service.atribuirPedidoAEntregador(id,codigoAcesso,idPedido));
    }

    @GetMapping("{id}/notificarCliente")
    ResponseEntity<?> notificarClientePedidoRota(
            @Valid @PathVariable("id") Long id,
            @Valid @RequestParam("idPedido") Long idPedido,
            @Valid @RequestParam("codigoAcesso") String codigoAcesso
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentov1Service.atribuirEntregadorNotificarCliente(id,codigoAcesso,idPedido));
    }

    @PostMapping("/disponibilidadePagamento")
    public ResponseEntity<?> disponibilizarMetodoPagamento(
            @RequestParam("MetodoPagamento") String metodoPagamento,
            @RequestParam("CodigoAcessoEstabelecimento") String codigoAcessoEstabelecimento,
            @RequestParam("CodigoAcessoPedido") String codigoAcessoPedido
    ){
        ResponseEntity<?> response;
        try{

            Pedido resultado = estabelecimentov1Service.disponibilizarMetodoPagamento(metodoPagamento,
                    codigoAcessoEstabelecimento,
                    codigoAcessoPedido);

            response = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(resultado);

        }catch (CodigoAcessoEstabelecimentoException codigoAcessoEstabelecimentoException){

            throw new CodigoAcessoEstabelecimentoException();

        }catch (PedidoCodigoAcessoIncorretoException codigoAcessoPedidoException){

            throw new PedidoCodigoAcessoIncorretoException();

        }

        return response;


    }

    @PostMapping("aprovarEntregador/{IdEntregador}/{CodigoAcessoEstabelecimento}")
    public ResponseEntity<?> aprovaEntregador(

            @PathVariable("IdEntregador") Long IdEntregador,
            @PathVariable("CodigoAcessoEstabelecimento") String codigoAcesso


    ){

        ResponseEntity<?> response;

        try{

            Entregador entregador = estabelecimentov1Service
                    .aprovarEntregador(IdEntregador, codigoAcesso);

            response = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(entregador);

        }catch (EntregadorNaoEncontradoException ca){

            throw new EntregadorNaoEncontradoException();

        }catch (CodigoAcessoEstabelecimentoException e){

            throw new CodigoAcessoEstabelecimentoException();

        }


        return response;

    }


}
