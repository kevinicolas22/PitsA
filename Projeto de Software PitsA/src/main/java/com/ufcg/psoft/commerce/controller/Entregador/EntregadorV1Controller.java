package com.ufcg.psoft.commerce.controller.Entregador;

import com.ufcg.psoft.commerce.dto.Entregador.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.commerce.exception.Entregador.CodigoAcessoEntregadorException;
import com.ufcg.psoft.commerce.exception.Entregador.DadosDeDisponibiliadadeInvalidosException;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.service.Entregador.EntregadorV1Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/entregadores", produces = MediaType.APPLICATION_JSON_VALUE)
public class EntregadorV1Controller {

    @Autowired
    EntregadorV1Service entregadorService;

    @PostMapping
    ResponseEntity<?> criarEntregador(
            @RequestBody @Valid EntregadorPostPutRequestDTO entregadorPostPutDTO
    ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entregadorService.adicionarEntregador(entregadorPostPutDTO));
    }

    @GetMapping("/{id}")
    ResponseEntity<?> buscarEntregadorPorId(
            @PathVariable("id") Long id
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorService.getEntregador(id));
    }

    @GetMapping
    ResponseEntity<?> listarEntregadores(
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorService.getEntregadores());
    }

    @PutMapping("/{id}")
    ResponseEntity<?> atualizarEntregador(
            @PathVariable("id") Long id,
            @Valid @RequestParam("codigoAcesso") String codigoAcessoEntregador,
            @RequestBody @Valid EntregadorPostPutRequestDTO entregadorPostPutDTO
    ){
        Entregador entregadorUpdate= entregadorService.updateEntregador(id, codigoAcessoEntregador, entregadorPostPutDTO);
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorUpdate);
    }

    @PutMapping("/{id}/update")
    ResponseEntity<?> updateStatus(
            @Valid @RequestParam("codigoAcessoEstabelecimento")String codigoAcessoEstabelecimento,
            @Valid @PathVariable("id") Long id,
            @Valid @RequestBody EntregadorPostPutRequestDTO entregadorPostPutDTO
    ){
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorService.updateStatus(id));
    }

    @PutMapping("/{id}/disponibilidade")
    ResponseEntity<?> updateDisponibilidade(
            @Valid @PathVariable("id") Long id,
            @Valid @RequestParam("codigoAcesso") String codigoAcessoEntregador,
            @Valid @RequestParam("disponibilidade") boolean disponibilidade
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorService.atualizaDisponibilidade(id, codigoAcessoEntregador, disponibilidade));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> excluirEntregador(
            @PathVariable("id") Long id,
            @Valid @RequestParam("codigoAcesso") String codigoAcessoEstabelecimento
    ){
        entregadorService.removerEntregador(id, codigoAcessoEstabelecimento);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();

    }


    @PutMapping("/DisponibilidadeEntregador/")
    ResponseEntity<?> alterarDisponibilidade(
            @RequestParam("CodigoAcesso") String codigoAcesso,
            @RequestParam("NovaDisponibilidade") String disponibilidade
    ){

        ResponseEntity<?> response;
        try{

            Entregador entregador = entregadorService
                    .alterarDiponibilidade(codigoAcesso, disponibilidade);

            response = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(entregador);

        }catch (CodigoAcessoEntregadorException codigoAcessoEntregadorException){

            throw new CodigoAcessoEntregadorException();

        }catch (DadosDeDisponibiliadadeInvalidosException e){

            throw new DadosDeDisponibiliadadeInvalidosException();

        }

        return response;

    }

}