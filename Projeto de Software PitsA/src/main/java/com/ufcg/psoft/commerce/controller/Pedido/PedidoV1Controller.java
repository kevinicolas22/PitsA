package com.ufcg.psoft.commerce.controller.Pedido;

import com.ufcg.psoft.commerce.dto.PedidoDTO.PedidoDTO;
import com.ufcg.psoft.commerce.enums.MetodoPagamento;
import com.ufcg.psoft.commerce.enums.StatusPedido;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteCodigoAcessoIncorretoException;
import com.ufcg.psoft.commerce.exception.Pedido.PedidoNaoCancelavelException;
import com.ufcg.psoft.commerce.exception.Pedido.PedidoNaoEncontradoException;
import com.ufcg.psoft.commerce.model.Pedido.Pedido;
import com.ufcg.psoft.commerce.service.Pedido.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/pedidos", produces = MediaType.APPLICATION_JSON_VALUE)
public class PedidoV1Controller {

    @Autowired
    PedidoService pedidoService;

    @PostMapping
    ResponseEntity<Pedido> criarPedido(
            @RequestParam("clienteCodigoAcesso") String clienteCodigoAcesso,
            @RequestBody @Valid PedidoDTO pedidoDTO
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoService.criarPedido(clienteCodigoAcesso, pedidoDTO));
    }

    @PutMapping("/{id}")
    ResponseEntity<Pedido> atualizarPedido(
            @PathVariable("id") Long id,
            @RequestParam String clienteCodigoAcesso,
            @RequestBody @Valid PedidoDTO pedidoDTO
    ) throws PedidoNaoEncontradoException {
        Pedido pedido = pedidoService.atualizarPedido(id, clienteCodigoAcesso, pedidoDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedido);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> excluirPedido(
            @PathVariable("id") Long id
    ) throws PedidoNaoEncontradoException {
        pedidoService.removerPedido(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{id}")
    ResponseEntity<Pedido> buscarPedidoPorId(
            @PathVariable("id") Long id
    ) throws PedidoNaoEncontradoException {
        Pedido pedido = pedidoService.getPedido(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedido);
    }

    @GetMapping
    ResponseEntity<List<Pedido>> listarPedidos() {
        List<Pedido> pedidos = pedidoService.getPedidos();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidos);
    }

    @PutMapping("/{id}/confirmar-pagamento")
    ResponseEntity<?> confirmarPagamento(
            @PathVariable("id") Long id,
            @RequestParam("metodoPagamento") MetodoPagamento metodoPagamento,
            @RequestParam("clienteCodigoAcesso") String clienteCodigoAcesso
    ) throws PedidoNaoEncontradoException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.confirmarPagamento(id, metodoPagamento, clienteCodigoAcesso));
    }

    @DeleteMapping("/{id}/cancelar-pedido")
    public ResponseEntity<?> cancelarPedido(
            @PathVariable("id") Long id,
            @RequestParam("clienteCodigoAcesso") String clienteCodigoAcesso
    ) throws PedidoNaoCancelavelException {
        pedidoService.cancelarPedido(id, clienteCodigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{id}/consulta-pedido")
    ResponseEntity<PedidoDTO> consultarPedidoEspecifico(
            @PathVariable("id") Long id,
            @RequestParam("codigoAcessoCliente") String codigoAcessoCliente
    ) throws PedidoNaoEncontradoException, ClienteCodigoAcessoIncorretoException {
        PedidoDTO pedidoDTO = pedidoService.clienteConsultaPedido(id, id, codigoAcessoCliente);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoDTO);
    }


    @GetMapping("/historico")
    ResponseEntity<List<PedidoDTO>> consultarHistoricoPedidosCliente(
            @RequestParam("idCliente") Long idCliente,
            @RequestParam("codigoAcessoCliente") String codigoAcessoCliente
    ) {
        List<PedidoDTO> pedidos = pedidoService.consultarHistoricoPedidosCliente(idCliente, codigoAcessoCliente);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidos);
    }

    @GetMapping("/historico/status")
    ResponseEntity<List<PedidoDTO>> consultarHistoricoPedidosClientePorStatus(
            @RequestParam("idCliente") Long idCliente,
            @RequestParam("codigoAcessoCliente") String codigoAcessoCliente,
            @RequestParam("status") StatusPedido status
    ) {
        List<PedidoDTO> pedidos = pedidoService.consultarHistoricoPedidosClientePorStatus(idCliente, codigoAcessoCliente, status);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidos);
    }

    @PutMapping("/{id}/notificar-indisponibilidade-entregadores")
    public ResponseEntity<?> notificarIndisponibilidadeEntregadores(
            @PathVariable("id") Long id
    ) throws PedidoNaoEncontradoException {
        pedidoService.notificarClienteIndisponibilidadeEntregadores(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Cliente notificado sobre a indisponibilidade de entregadores.");
    }


    @ExceptionHandler(PedidoNaoEncontradoException.class)
    ResponseEntity<?> handlePedidoNaoEncontradoException(PedidoNaoEncontradoException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
