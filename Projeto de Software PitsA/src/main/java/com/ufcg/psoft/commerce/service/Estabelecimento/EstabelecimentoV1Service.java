package com.ufcg.psoft.commerce.service.Estabelecimento;

import com.ufcg.psoft.commerce.dto.Estabelecimento.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.PizzaDTO.SaborResponseDTO;
import com.ufcg.psoft.commerce.enums.MetodoPagamento;
import com.ufcg.psoft.commerce.enums.StatusPedido;
import com.ufcg.psoft.commerce.enums.TipoDeSabor;
import com.ufcg.psoft.commerce.exception.Entregador.EntregadorNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Estabelecimento.CodigoAcessoEstabelecimentoException;
import com.ufcg.psoft.commerce.exception.Estabelecimento.CodigoAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.Estabelecimento.EstabelecimentoNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Pedido.MetodoPagamentoInvalidoException;
import com.ufcg.psoft.commerce.exception.Pedido.PedidoCodigoAcessoIncorretoException;
import com.ufcg.psoft.commerce.exception.Pedido.PedidoNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Pedido.StatusPedidoInvalidoException;
import com.ufcg.psoft.commerce.exception.Pizza.TipoDeSaborNaoExisteException;
import com.ufcg.psoft.commerce.model.Cliente.Cliente;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.Estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.Pedido.Pedido;
import com.ufcg.psoft.commerce.model.SaborPizza.SaborPizza;
import com.ufcg.psoft.commerce.repository.Associacao.AssociacaoRepository;
import com.ufcg.psoft.commerce.repository.Cliente.ClienteRepository;
import com.ufcg.psoft.commerce.repository.Entregador.EntregadorRepository;
import com.ufcg.psoft.commerce.repository.Estabelecimento.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.repository.Pedido.PedidoRepository;
import com.ufcg.psoft.commerce.service.Associacao.AssociacaoService;
import com.ufcg.psoft.commerce.service.Cliente.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EstabelecimentoV1Service {

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
            private JavaMailSender javaMailSender;
    ClienteService clienteService;
    AssociacaoService associacaoService;
    @Autowired
    private AssociacaoRepository associacaoRepository;

    @Autowired
    private EntregadorRepository entregadorRepository;


    @Bean
    private ModelMapper mapeadorEstabelecimento() {

        return new ModelMapper();

    }

    private Estabelecimento converteTDOParaEntidade(EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO) {

        return mapeadorEstabelecimento().map(estabelecimentoPostPutRequestDTO, Estabelecimento.class);

    }

    public Estabelecimento add(EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO) {
        Estabelecimento estabelecimento = converteTDOParaEntidade(estabelecimentoPostPutRequestDTO);

        if(estabelecimento.getEntregadores() == null){

            estabelecimento.setEntregadores(new HashSet<Entregador>());

        }

        if (estabelecimento.getCodigoAcesso().length() != 6) {

            throw new CodigoAcessoInvalidoException();

        }


        return estabelecimentoRepository.save(estabelecimento);

    }

    public Estabelecimento getOne(long id) {

        Estabelecimento estabelecimento = null;

        if (estabelecimentoRepository.existsById(id)) {

            estabelecimento = estabelecimentoRepository.findById(id).get();


        } else throw new EstabelecimentoNaoEncontradoException();

        return estabelecimento;


    }

    public List<Estabelecimento> getAll() {

        return estabelecimentoRepository.findAll();
    }

    public Estabelecimento put(long id, EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO) throws EstabelecimentoNaoEncontradoException, CodigoAcessoEstabelecimentoException {
        Estabelecimento estabelecimento = null;

        if (estabelecimentoPostPutRequestDTO.getCodigoAcesso().length() != 6) throw new CodigoAcessoInvalidoException();


        if (estabelecimentoRepository.existsById(id)) {

            estabelecimento = converteTDOParaEntidade(estabelecimentoPostPutRequestDTO);

            estabelecimento.setId(id);

            estabelecimentoRepository.saveAndFlush(estabelecimento);

        } else throw new EstabelecimentoNaoEncontradoException();


        return estabelecimento;

    }

    public void delete(long id) {
        if (estabelecimentoRepository.existsById(id)) {

            estabelecimentoRepository.deleteById(id);

        }


    }

    public Estabelecimento getByCodigoAcesso(String codigoAcesso) throws EstabelecimentoNaoEncontradoException {

        Estabelecimento estabelecimento = null;

        if (estabelecimentoRepository.existsByCodigoAcesso(codigoAcesso)) {


            estabelecimento = estabelecimentoRepository.findByCodigoAcesso(codigoAcesso).get();

        } else throw new EstabelecimentoNaoEncontradoException();

        return estabelecimento;

    }

    public void validaCodigoAcessoEstabelecimento(String codigoAcesso) throws CodigoAcessoEstabelecimentoException, EstabelecimentoNaoEncontradoException {
        Estabelecimento estabelecimento = findEstabelecimento();

        if (!estabelecimento.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoAcessoEstabelecimentoException();
        }
    }

    // Método para verificar se o código de acesso do estabelecimento existe
    private boolean verificaCodigoAcesso(String codigoAcesso) throws EstabelecimentoNaoEncontradoException {
        Estabelecimento estabelecimento = findEstabelecimento();
        return estabelecimento.getCodigoAcesso().equals(codigoAcesso);
    }

    // Método para encontrar o estabelecimento
    public Estabelecimento findEstabelecimento() throws EstabelecimentoNaoEncontradoException {
        List<Estabelecimento> estabelecimentoList = estabelecimentoRepository.findAll();
        if (estabelecimentoList.isEmpty()) {
            throw new EstabelecimentoNaoEncontradoException();
        }

        return estabelecimentoList.get(0);
    }

    public SaborResponseDTO saborResponseDTOMapeador(SaborPizza saborPizza) {

        return mapeadorEstabelecimento().map(saborPizza, SaborResponseDTO.class);

    }

    public Set<SaborResponseDTO> recuperarSabores(long id, EstabelecimentoPostPutRequestDTO postPutRequestDTO) throws EstabelecimentoNaoEncontradoException {

        Set<SaborResponseDTO> resultado = new HashSet<>();
        if (estabelecimentoRepository.existsById(id)) {
            Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).get();
            Iterator<SaborPizza> iterator = estabelecimento.getSaboresPizza().iterator();
            while (iterator.hasNext()) {
                resultado.add(saborResponseDTOMapeador(iterator.next()));
            }
        } else throw new EstabelecimentoNaoEncontradoException();
        return resultado;
    }


    public Set<SaborResponseDTO> recuperarSaboresPorTipo(long id, EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO, TipoDeSabor tipo) throws EstabelecimentoNaoEncontradoException {

        Set<SaborResponseDTO> resultado = new HashSet<SaborResponseDTO>();

        if (estabelecimentoRepository.existsById(id)) {
            Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).get();

            for (SaborPizza saborPizza : estabelecimento.getSaboresPizza()) {

                if (String.valueOf(saborPizza.getTipoDeSabor()).equals(String.valueOf(tipo))) {

                    resultado.add(saborResponseDTOMapeador(saborPizza));

                }

            }


        } else throw new EstabelecimentoNaoEncontradoException();

        return resultado;
    }

    public Set<SaborPizza> getCardapioDisponibilidade(Long id, String codigoAcesso, Boolean disponibilidade) {
        Estabelecimento e = estabelecimentoRepository.findById(id).orElseThrow(EstabelecimentoNaoEncontradoException::new);

        if (!codigoAcesso.equals(e.getCodigoAcesso())) {
            throw new CodigoAcessoInvalidoException();
        }

        Stream<SaborPizza> cardapioDisponibilidade = e.getSaboresPizza().stream().filter(
                item -> item.getDisponibilidadeSabor() == disponibilidade
        );

        return cardapioDisponibilidade.collect(Collectors.toSet());
    }


    public Set<SaborPizza> getCardapioCompleto(Long id, String codigoAcesso) {
        Estabelecimento e = estabelecimentoRepository.findById(id).orElseThrow(EstabelecimentoNaoEncontradoException::new);
        if (codigoAcesso.equals(e.getCodigoAcesso())) throw new CodigoAcessoInvalidoException();
        return e.getSaboresPizza();
    }

    public Set<SaborPizza> listarCardapioPorTipoDePizza(Long id, String codigoAcesso, String tipo) {
        Estabelecimento e = estabelecimentoRepository.findById(id).orElseThrow(EstabelecimentoNaoEncontradoException::new);

        if (codigoAcesso.equals(e.getCodigoAcesso())) throw new CodigoAcessoInvalidoException();

        if (!tipo.equalsIgnoreCase("SALGADO") && !tipo.equalsIgnoreCase("DOCE")) {
            throw new TipoDeSaborNaoExisteException();
        }
        Stream<SaborPizza> cardapioTipo = e.getSaboresPizza().stream().filter(
                item -> Objects.equals(item.getTipoDeSabor(), tipo)
        );
        return cardapioTipo.collect(Collectors.toSet());
    }

    private boolean isValidCodigoAcesso(String codigoAcesso) {
        return codigoAcesso.matches("[0-9]+") && codigoAcesso.length() == 6;
    }

    public SaborPizza alterarDisponibilidadeSaborPizza(Long idEstabelecimento, Long idSabor, String codigoAcesso, boolean disponibilidade) {
        Optional<Estabelecimento> estabelecimentoOptional = estabelecimentoRepository.findById(idEstabelecimento);
        if (!estabelecimentoOptional.isPresent()) {
            throw new EstabelecimentoNaoEncontradoException();
        }
        if (!isValidCodigoAcesso(codigoAcesso)) {
            throw new CodigoAcessoInvalidoException();
        }

        Estabelecimento estabelecimento = estabelecimentoOptional.get();
        estabelecimento.setDisponibilidadeSabor(idSabor, disponibilidade);
        SaborPizza sabor = estabelecimento.getSaborPizzaById(idSabor);
        estabelecimentoRepository.save(estabelecimento);
        if (disponibilidade == true) {
            sabor.notifyObservers();
        }
        return estabelecimento.getSaborPizzaById(idSabor);
    }

    public Pedido confirmarPreparacaoPedido(Long id, String codigoAcesso, Long idPedido) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).orElseThrow(EstabelecimentoNaoEncontradoException::new);
        Pedido pedidoAtual = pedidoRepository.findById(idPedido).orElseThrow(() -> new PedidoNaoEncontradoException());
        if (!isValidCodigoAcesso(codigoAcesso)) {
            throw new CodigoAcessoInvalidoException();
        }
        if (estabelecimento.getCodigoAcesso().equals(codigoAcesso)) {

            if(!estabelecimento.isSubcribe(pedidoAtual)){
                estabelecimento.subscribeTo(pedidoAtual); //Notificação de quando o pedido pronto
            }
            pedidoAtual.setStatus(StatusPedido.PEDIDO_PRONTO);

            pedidoRepository.save(pedidoAtual);

            pedidoAtual=atribuirPedidoAEntregador(estabelecimento.getId(),estabelecimento.getCodigoAcesso(),pedidoAtual.getId());
            return pedidoAtual;
        } else {
            throw new CodigoAcessoEstabelecimentoException();
        }

    }

    public Pedido atribuirPedidoAEntregador(Long id, String codigoAcesso, Long idPedido) {
        //pegando os ids
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).get();
        Pedido pedidoAtual = pedidoRepository.findById(idPedido).get();
        Cliente clientePedido= clienteRepository.findById(pedidoAtual.getCliente()).get();
        if (!isValidCodigoAcesso(codigoAcesso)) {
            throw new CodigoAcessoInvalidoException();
        }
        if (estabelecimento.getCodigoAcesso().equals(codigoAcesso)) {
                List<Entregador> entregadores= ordenarEntregadoresPorTempoDisponivel(estabelecimento.getEntregadoresDisponiveis());
                //Quando nao houver entregadores disponiveis, o pedido será incluido na lista dos pedidos em espera do estabelecimento
                if(entregadores.isEmpty()){
                    estabelecimento.getPedidosEspera().add(pedidoAtual);
                    estabelecimentoRepository.save(estabelecimento);
                    SimpleMailMessage message= new SimpleMailMessage();
                    message.setFrom("PITS-A");
                    message.setTo("jpcros40414@gmail.com");
                    message.setSubject("AGUARDE UM POUCO!");
                    message.setText("NOSSOS ENTREGADORES ESTÃO OCUPADOS, ASSIM QUE POSSÍVEL ENVIAREMOS O SEU PEDIDO...");
                    javaMailSender.send(message);
                }
                else{
                    Entregador entregadorDisponivelPedido = entregadores.get(0);
                    SimpleMailMessage message= new SimpleMailMessage();
                    message.setFrom("PITS-A");
                    message.setTo("jpcros40414@gmail.com");
                    message.setSubject("PEDIDO EM ROTA!");
                    message.setText("OBA! SEU PEDIDO SAIU PARA ENTREGA...\n\nENTREGADOR: "+ entregadorDisponivelPedido.getNome()+"\nPLACA: "+ entregadorDisponivelPedido.getPlacaVeiculo()+"\nCOR: "+entregadorDisponivelPedido.getCorVeiculo()+"\nTIPO: "+entregadorDisponivelPedido.getTipoVeiculo());
                    javaMailSender.send(message);
                    entregadorDisponivelPedido.setDisponibilidade(false);
                    pedidoAtual.setEntregador(entregadorDisponivelPedido);
                    entregadorRepository.save(entregadorDisponivelPedido);
                    pedidoAtual.setStatus(StatusPedido.PEDIDO_EM_ROTA);
                    pedidoRepository.save(pedidoAtual);
                    return pedidoAtual;
                }
            }
         else {
            throw new CodigoAcessoEstabelecimentoException();
        }
         return pedidoAtual;

    }

    // metodo com uma lista de entregadores adiconados e ordenados por tempo disponivel
    public List<Entregador> ordenarEntregadoresPorTempoDisponivel(List<Entregador> entregadores) {
        List<Entregador> entregadoresPorTempo = entregadores;

        entregadoresPorTempo.sort(Entregador::compareTo);

        return entregadoresPorTempo;
    }

    public Pedido disponibilizarMetodoPagamento(String metodoPagamento,
                                                String codigoAcessoEstabelecimento,
                                                String codigoAcessoPedido){


        if(!pedidoRepository.existsByCodigoAcesso(codigoAcessoPedido)){

            throw new PedidoCodigoAcessoIncorretoException();

        }

        Pedido pedido = pedidoRepository.findByCodigoAcesso(codigoAcessoPedido).get();

        if(!pedido.getCodigoAcessoEstabelecimento().equals(codigoAcessoEstabelecimento)){

            throw new CodigoAcessoEstabelecimentoException();

        }


        double valor = calculaValor(pedido.getValorTotal(), metodoPagamento);

        MetodoPagamento metodoPAG;

        if(metodoPagamento.equals("PIX")) metodoPAG = MetodoPagamento.PIX;
        else if(metodoPagamento.equals("DEBITO")) metodoPAG = MetodoPagamento.CARTAO_DEBITO;
        else if (metodoPagamento.equals("CREDITO")) metodoPAG = MetodoPagamento.CARTAO_CREDITO;
        else throw new MetodoPagamentoInvalidoException();

        pedido.setMetodoPagamento(metodoPAG);
        pedido.setValorTotal(valor);

        return pedidoRepository.saveAndFlush(pedido);

    }

    private Double calculaValor(Double valor, String metodoPagamento){

        Double resultado;

        if(metodoPagamento.equals("PIX")){

            resultado = valor * 0.95;

        }else if (metodoPagamento.equals("DEBITO")){

            resultado = valor * 0.975;

        }else{

            resultado = valor;

        }


        return resultado;
    }

    public Pedido atribuirEntregadorNotificarCliente(Long id, String codigoAcesso, Long idPedido) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).orElseThrow(EstabelecimentoNaoEncontradoException::new);
        Pedido pedidoAtual = pedidoRepository.findById(idPedido).orElseThrow(PedidoNaoEncontradoException::new);

        if (estabelecimento.getCodigoAcesso().equals(codigoAcesso)) {
            if(pedidoAtual.getStatus().equals(StatusPedido.PEDIDO_EM_ROTA)) {
                Cliente clientePedido = clienteRepository.findById(pedidoAtual.getCliente()).get();
                Entregador entregadorPedido = pedidoAtual.getEntregador();
                if (entregadorPedido == null) {
                    throw new EntregadorNaoEncontradoException();
                }
                clientePedido.notificaPedidoEmRota(entregadorPedido);
                return pedidoAtual;
            }
            throw new StatusPedidoInvalidoException();

        }
        throw new CodigoAcessoEstabelecimentoException();

    }


    public Entregador aprovarEntregador(Long id, String codigoAcesso){
        Entregador entregador;

        if(!estabelecimentoRepository.existsByCodigoAcesso(codigoAcesso)){

            throw new CodigoAcessoEstabelecimentoException();


        }

        if(!entregadorRepository.existsById(id)) {

            throw new EntregadorNaoEncontradoException();

        }

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findByCodigoAcesso(codigoAcesso).get();

        entregador = entregadorRepository.findById(id).get();

        entregador.setEstadoDeDisposicao("Descanso");

        estabelecimento.aprovarEntregador(entregador);

        entregador = entregadorRepository.save(entregador);

        estabelecimentoRepository.save(estabelecimento);

        return entregador;
    }
}