package com.ufcg.psoft.commerce.controller.Associacao;


import com.ufcg.psoft.commerce.exception.Associacao.EntregadorCodigoAcessoNaoEntradoException;
import com.ufcg.psoft.commerce.exception.Associacao.EntregadorIdNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Associacao.EstabelecimentoIdNaoEncontradoException;
import com.ufcg.psoft.commerce.model.Associacao.Associacao;
import com.ufcg.psoft.commerce.service.Associacao.AssociacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/associacao", produces = MediaType.APPLICATION_JSON_VALUE)
public class AssociacaoController {

    @Autowired
    AssociacaoService associacaoService;


    @PostMapping
    public ResponseEntity<?> criarAssociacao(
            @RequestParam("entregadorId") String entregadorId,
            @RequestParam("codigoAcesso") String codigoAcesso,
            @RequestParam("estabelecimentoId") String estabelecimentoId

    ){
        ResponseEntity<?>  response;

        try{

            Associacao associacao = associacaoService.criarAssociacao(entregadorId,
                    codigoAcesso,
                    estabelecimentoId);

            response = ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(associacao);

        }catch (EntregadorIdNaoEncontradoException entregadorIdNaoEncontradoException){

            throw new EntregadorIdNaoEncontradoException();

        }catch (EntregadorCodigoAcessoNaoEntradoException entregadorCodigoAcessoNaoEntradoException){

            response = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            throw new EntregadorCodigoAcessoNaoEntradoException();

        }catch (EstabelecimentoIdNaoEncontradoException estabelecimentoIdNaoEncontradoException){

            throw new EstabelecimentoIdNaoEncontradoException();

        }

        return response;
    }

    @PutMapping
    public ResponseEntity<?>  aprovaEntregador(
            @RequestParam("codigoAcesso") String codigoAcesso,
            @RequestParam("idAssociacao") Long idAssociacao
    ){

        ResponseEntity<?> response;

        Associacao associacao;
        try{

            associacao = associacaoService.aprovarEntregador(codigoAcesso,
                    idAssociacao);

            response =  ResponseEntity
                    .status(HttpStatus.OK)
                    .body(associacao);

        }catch (EntregadorCodigoAcessoNaoEntradoException entregadorCodigoAcessoNaoEntradoException){

            throw new EntregadorCodigoAcessoNaoEntradoException();

        }catch (EntregadorIdNaoEncontradoException entregadorIdNaoEncontradoException){

            throw new EntregadorIdNaoEncontradoException();

        }catch (EstabelecimentoIdNaoEncontradoException estabelecimentoIdNaoEncontradoException){

            throw new EstabelecimentoIdNaoEncontradoException();

        }
        return response;
    }

}
