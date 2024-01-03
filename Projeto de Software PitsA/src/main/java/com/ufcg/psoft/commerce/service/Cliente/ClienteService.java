package com.ufcg.psoft.commerce.service.Cliente;

import com.ufcg.psoft.commerce.dto.ClienteDTO.ClienteDTO;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteCodigoAcessoIncorretoException;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteNaoEncontradoException;
import com.ufcg.psoft.commerce.model.Cliente.Cliente;
import com.ufcg.psoft.commerce.model.Pedido.Pedido;

import java.util.List;

public interface ClienteService {
    Cliente adicionarCliente(ClienteDTO cliente);

    public Cliente atualizarCliente(Long id, ClienteDTO cliente);

    public void removerCliente(Long id);

    Cliente getCliente(Long id);

    List<Cliente> getClientes();

    boolean validarCodigoAcesso(Long id, String codigoAcesso) throws ClienteCodigoAcessoIncorretoException, ClienteNaoEncontradoException;
     void demonstrarInteressePizza(Long id, String codigoAcesso, Long idPizza);

     public Pedido confirmarEntrega(Long id, String codigoAcesso, Long idPedido);

}
