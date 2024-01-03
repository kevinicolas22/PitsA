package com.ufcg.psoft.commerce.service.Cliente;

import com.ufcg.psoft.commerce.dto.ClienteDTO.ClienteDTO;
import com.ufcg.psoft.commerce.enums.StatusPedido;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteCodigoAcessoIncorretoException;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteCodigoAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Pedido.PedidoNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Pizza.SaborPizzaClienteCadastrado;
import com.ufcg.psoft.commerce.exception.Pizza.SaborPizzaEstaDisponivel;
import com.ufcg.psoft.commerce.model.Cliente.Cliente;
import com.ufcg.psoft.commerce.model.Estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.Pedido.Pedido;
import com.ufcg.psoft.commerce.model.SaborPizza.SaborPizza;
import com.ufcg.psoft.commerce.repository.Cliente.ClienteRepository;
import com.ufcg.psoft.commerce.repository.Estabelecimento.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.repository.Pedido.PedidoRepository;
import com.ufcg.psoft.commerce.service.Estabelecimento.EstabelecimentoV1Service;
import com.ufcg.psoft.commerce.service.Pizza.SaborService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteV1Service implements ClienteService {

    @Autowired
    private  ClienteRepository clienteRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SaborService saborService;

    @Autowired
    private PedidoRepository pedidoRepository;


    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EstabelecimentoV1Service estabelecimentoService;

    @Override
    public Cliente adicionarCliente(ClienteDTO clienteDTO) {
        String codigoAcesso = clienteDTO.getCodigoAcesso();

        if (codigoAcesso == null || codigoAcesso.isEmpty() || !isValidCodigoAcesso(codigoAcesso)) {
            throw new ClienteCodigoAcessoInvalidoException();
        } else {
            return clienteRepository.save(
                    Cliente.builder()
                            .nome(clienteDTO.getNome())
                            .endereco(clienteDTO.getEndereco())
                            .codigoAcesso(codigoAcesso)
                            .build()
            );
        }
    }

    @Override
    public Cliente atualizarCliente(Long id, ClienteDTO clienteDTO) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);

        if (clienteOptional.isPresent()) {
            String codigoAcesso = clienteDTO.getCodigoAcesso();
            if (codigoAcesso != null && !codigoAcesso.isEmpty() && !isValidCodigoAcesso(codigoAcesso)) {
                throw new ClienteCodigoAcessoIncorretoException();
            }

            Cliente cliente = clienteOptional.get();
            cliente.setNome(clienteDTO.getNome());
            cliente.setEndereco(clienteDTO.getEndereco());

            return clienteRepository.save(cliente);
        } else {
            throw new ClienteNaoEncontradoException();
        }
    }

    @Override
    public void removerCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNaoEncontradoException();
        }
        // Não há necessidade de validar o código de acesso para remover o cliente.
        clienteRepository.deleteById(id);
    }

    @Override
    public Cliente getCliente(Long id) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);
        if (clienteOptional.isPresent()) {
            return clienteOptional.get();
        } else {
            throw new ClienteNaoEncontradoException();
        }
    }

    @Override
    public List<Cliente> getClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public boolean validarCodigoAcesso(Long id, String codigoAcesso) throws ClienteCodigoAcessoIncorretoException, ClienteNaoEncontradoException {
        Cliente cliente = getClienteById(id);
        if (!cliente.getCodigoAcesso().equals(codigoAcesso)) {
            throw new ClienteCodigoAcessoIncorretoException();
        }
        return true;
    }

    @Override
    public void demonstrarInteressePizza(Long id, String codigoAcesso, Long idPizza) {
        Cliente cliente = getClienteById(id);
        if (codigoAcesso != null && !codigoAcesso.isEmpty() && !isValidCodigoAcesso(codigoAcesso)) {
            throw new ClienteCodigoAcessoIncorretoException();
        }
        if (!cliente.getCodigoAcesso().equals(codigoAcesso)) {
            throw new ClienteCodigoAcessoIncorretoException();
        }

        // Correção: Verifica se idPizza é nulo antes de tentar consultar
        if (idPizza == null) {
            throw new IllegalArgumentException("O ID da pizza não pode ser nulo.");
        }

        SaborPizza pizza = saborService.consultarSaborPizzaById(idPizza);

        if (!String.valueOf(pizza.getDisponibilidadeSabor()).equalsIgnoreCase("true")) {
            if (!cliente.isSubscribed(pizza)) {
                cliente.subscribeTo(pizza);
                clienteRepository.save(cliente);
                saborService.salvarSaborPizzaCadastrado(pizza);

                // Adicionando notificação dos observadores quando a pizza está disponível
                if (pizza.getDisponibilidadeSabor()) {
                    pizza.notifyObservers();
                }
            } else {
                throw new SaborPizzaClienteCadastrado();
            }
        } else {
            throw new SaborPizzaEstaDisponivel();
        }
    }


    private Cliente getClienteById(Long id) throws ClienteNaoEncontradoException {
        return clienteRepository.findById(id).orElseThrow(ClienteNaoEncontradoException::new);
    }

    private boolean isValidCodigoAcesso(String codigoAcesso) {
        return codigoAcesso.matches("[0-9]+") && codigoAcesso.length() == 6;
    }

    @Override
    public Pedido confirmarEntrega(Long id, String codigoAcesso,Long idPedido){
        Cliente cliente= clienteRepository.findById(id).get();
        if(!codigoAcesso.equals(cliente.getCodigoAcesso())){
            throw  new ClienteCodigoAcessoIncorretoException();
        }
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(PedidoNaoEncontradoException::new);
        pedido.setStatus(StatusPedido.PEDIDO_ENTREGUE);
        pedidoRepository.save(pedido);
        //entregador fica disponivel novamente
        pedido.getEntregador().setDisponibilidade(true);
        Estabelecimento estabelecimento= estabelecimentoRepository.findById(pedido.getEstabelecimento()).get();
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom("PITS-A");
        message.setTo("jpcros40414@gmail.com");
        message.setSubject("PEDIDO RECEBIDO!");
        message.setText("MEU PEDIDO CHEGOU...");
        javaMailSender.send(message);
        if(estabelecimento.getPedidosEspera().size()>0){
            estabelecimentoService.atribuirPedidoAEntregador(estabelecimento.getId(),estabelecimento.getCodigoAcesso(),estabelecimento.getPedidosEspera().get(0).getId());
        }
        return pedido;

    }


}
