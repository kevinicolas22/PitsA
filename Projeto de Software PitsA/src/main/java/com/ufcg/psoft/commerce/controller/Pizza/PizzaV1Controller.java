package com.ufcg.psoft.commerce.controller.Pizza;

import com.ufcg.psoft.commerce.dto.PizzaDTO.SaborPostPutDTO;
import com.ufcg.psoft.commerce.exception.Estabelecimento.CodigoAcessoEstabelecimentoException;
import com.ufcg.psoft.commerce.exception.Estabelecimento.EstabelecimentoNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Pizza.SaborPizzaNaoEncontradoException;
import com.ufcg.psoft.commerce.service.Estabelecimento.EstabelecimentoV1Service;
import com.ufcg.psoft.commerce.service.Pizza.SaborService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/pizza/v1/")
@CrossOrigin
public class PizzaV1Controller {

    @Autowired
    SaborService saborService;
    @Autowired
    EstabelecimentoV1Service estabelecimentoV1Service;

    @PostMapping
    public ResponseEntity<?> criarSaborPizza (
            @Valid @RequestParam ("codigoAcessoEstabelecimento")String codigoAcessoEstabelecimento,
            @Valid @RequestParam  ("idEstabelecimento")Long idEstabelecimento,
            @Valid @RequestBody SaborPostPutDTO sabor) throws EstabelecimentoNaoEncontradoException, CodigoAcessoEstabelecimentoException {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saborService.criarSabor(idEstabelecimento,codigoAcessoEstabelecimento,sabor));

    }

    @PutMapping(value = "/{idPizza}")
    public ResponseEntity<?> atualizarSaborPizza(
            @Valid @RequestParam  ("idEstabelecimento")Long idEstabelecimento,
            @Valid @RequestParam("codigoAcessoEstabelecimento")String codigoAcessoEstabelecimento,
            @Valid @PathVariable("idPizza") Long idPizza,
            @Valid @RequestBody SaborPostPutDTO sabor) throws EstabelecimentoNaoEncontradoException, CodigoAcessoEstabelecimentoException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saborService.atualizarSaborPizza(idEstabelecimento,codigoAcessoEstabelecimento,idPizza,sabor));
    }

    @PutMapping(value = "/{idPizza}/disponibilidade")
    public ResponseEntity<?> atualizarSaborPizzaDisponibilidade(
            @Valid @RequestParam  ("idEstabelecimento")Long idEstabelecimento,
            @Valid @RequestParam("codigoAcessoEstabelecimento")String codigoAcessoEstabelecimento,
            @Valid @PathVariable("idPizza") Long idPizza,
            @Valid @RequestBody SaborPostPutDTO sabor,
            @Valid @RequestParam("disponibilidade") Boolean disponibilidade) throws EstabelecimentoNaoEncontradoException, CodigoAcessoEstabelecimentoException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saborService.atualizarSaborPizzaDisponibilidade(idEstabelecimento,codigoAcessoEstabelecimento,idPizza,sabor,disponibilidade));
    }

    @GetMapping
    public ResponseEntity<?> buscarTodosOsSabores(
            @Valid @RequestParam  ("idEstabelecimento")Long idEstabelecimento,
            @Valid @RequestParam("codigoAcessoEstabelecimento")String codigoAcessoEstabelecimento) throws EstabelecimentoNaoEncontradoException, CodigoAcessoEstabelecimentoException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saborService.buscarTodosSaboresPizza(idEstabelecimento,codigoAcessoEstabelecimento));
    }

    @GetMapping(value = "/{idPizza}")
    public ResponseEntity<?> buscarSaborId(
            @Valid @RequestParam  ("idEstabelecimento")Long idEstabelecimento,
            @Valid @RequestParam ("codigoAcessoEstabelecimento")String codigoAcessoEstabelecimento,
            @Valid @PathVariable("idPizza") Long idPizza
    ) throws EstabelecimentoNaoEncontradoException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saborService.buscarId(idEstabelecimento,codigoAcessoEstabelecimento,idPizza));
    }

    @DeleteMapping(value = "/{idPizza}")
    public ResponseEntity<?> deletarSaborPizza(
            @Valid @RequestParam  ("idEstabelecimento")Long idEstabelecimento,
            @Valid @RequestParam ("codigoAcessoEstabelecimento")String codigoAcessoEstabelecimento,
            @Valid @PathVariable("idPizza") Long idPizza)
            throws SaborPizzaNaoEncontradoException, EstabelecimentoNaoEncontradoException, CodigoAcessoEstabelecimentoException {
        saborService.deletarSaborPizza(idEstabelecimento,codigoAcessoEstabelecimento,idPizza);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}
