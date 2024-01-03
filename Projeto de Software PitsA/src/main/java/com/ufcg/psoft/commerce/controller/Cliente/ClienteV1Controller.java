package com.ufcg.psoft.commerce.controller.Cliente;

import com.ufcg.psoft.commerce.dto.ClienteDTO.ClienteDTO;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteNaoEncontradoException;
import com.ufcg.psoft.commerce.service.Cliente.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/v1/clientes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClienteV1Controller {

    @Autowired
    ClienteService clienteService;

    @PostMapping
    ResponseEntity<?> criarCliente(
            @RequestBody @Valid ClienteDTO clienteDTO
    ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.adicionarCliente(clienteDTO));
    }
    @PutMapping("/{id}")
    ResponseEntity<?> atualizarCliente(
            @PathVariable("id") Long id,
            @RequestBody @Valid ClienteDTO clienteDTO
    ){
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.atualizarCliente(id, clienteDTO));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> excluirCliente(
            @PathVariable("id") Long id
    ){
        clienteService.removerCliente(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();

    }

    @GetMapping("/{id}")
    ResponseEntity<?> buscarClientePorId(
            @PathVariable("id") Long id
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.getCliente(id));

    }

    @GetMapping
    ResponseEntity<?> listarClientes(
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.getClientes());
    }

    @PutMapping("/{id}/{codigoAcesso}/{idPizza}")
    ResponseEntity<?> demonstrarInteressePizza(
            @PathVariable("id") Long id,
            @PathVariable ("codigoAcesso")String codigoAcesso,
            @PathVariable ("idPizza")Long idPizza
    ) {
        clienteService.demonstrarInteressePizza(id,codigoAcesso,idPizza);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/confirma-entrega")
    ResponseEntity<?> confirmarEntrega(
            @Valid @PathVariable("id") Long id,
            @Valid @RequestParam("codigoAcesso") String codigoAcesso,
            @Valid @RequestParam("idPedido") Long idPedido
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.confirmarEntrega(id,codigoAcesso,idPedido));
    }


    @ExceptionHandler(ClienteNaoEncontradoException.class)
    ResponseEntity<?> handleClienteNaoEncontradoException(ClienteNaoEncontradoException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

}

