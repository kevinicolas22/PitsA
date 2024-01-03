package com.ufcg.psoft.commerce.service.Pedido;

import com.ufcg.psoft.commerce.dto.PedidoDTO.PedidoDTO;
import com.ufcg.psoft.commerce.enums.MetodoPagamento;
import com.ufcg.psoft.commerce.enums.StatusPedido;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteCodigoAcessoIncorretoException;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Pedido.PedidoCodigoAcessoIncorretoException;
import com.ufcg.psoft.commerce.exception.Pedido.PedidoNaoEncontradoException;
import com.ufcg.psoft.commerce.model.Pedido.Pedido;

import java.util.List;

public interface PedidoService {
    Pedido criarPedido(String clienteCodigoAcesso, PedidoDTO pedido);

    Pedido atualizarPedido(Long pedidoId, String clienteCodigoAcesso, PedidoDTO pedidoDTO);

    void removerPedido(Long id);

    Pedido getPedido(Long id);

    List<Pedido> getPedidos();

    Pedido confirmarPagamento(Long id, MetodoPagamento metodoPagamento, String codigoAcesso);

    boolean validarCodigoAcesso(Long id, String codigoAcesso) throws PedidoCodigoAcessoIncorretoException, PedidoNaoEncontradoException;

    void cancelarPedido(Long pedidoId, String clienteCodigoAcesso) throws PedidoNaoEncontradoException, PedidoCodigoAcessoIncorretoException;

    PedidoDTO clienteConsultaPedido(Long idPedido, Long idCliente, String codigoAcessoCliente)
            throws PedidoNaoEncontradoException, ClienteCodigoAcessoIncorretoException, ClienteNaoEncontradoException;

    List<PedidoDTO> consultarHistoricoPedidosCliente(Long idCliente, String codigoAcessoCliente);

    List<PedidoDTO> consultarHistoricoPedidosClientePorStatus(Long idCliente, String codigoAcesso, StatusPedido status);

    void notificarClienteIndisponibilidadeEntregadores(Long idPedido) throws PedidoNaoEncontradoException;

}

