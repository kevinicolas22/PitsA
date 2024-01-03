package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.ClienteDTO.ClienteDTO;
import com.ufcg.psoft.commerce.dto.PedidoDTO.PedidoDTO;
import com.ufcg.psoft.commerce.dto.PedidoDTO.PedidoResponseDTO;
import com.ufcg.psoft.commerce.dto.PizzaDTO.SaborPostPutDTO;
import com.ufcg.psoft.commerce.enums.MetodoPagamento;
import com.ufcg.psoft.commerce.enums.TamanhoPizza;
import com.ufcg.psoft.commerce.exception.Associacao.EstabelecimentoIdNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.exception.Pizza.PizzaSemSaboresException;
import com.ufcg.psoft.commerce.model.Associacao.Associacao;
import com.ufcg.psoft.commerce.model.Cliente.Cliente;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.Estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.Pedido.Pedido;
import com.ufcg.psoft.commerce.model.SaborPizza.Pizza;
import com.ufcg.psoft.commerce.model.SaborPizza.SaborPizza;
import com.ufcg.psoft.commerce.repository.Cliente.ClienteRepository;
import com.ufcg.psoft.commerce.repository.Entregador.EntregadorRepository;
import com.ufcg.psoft.commerce.repository.Estabelecimento.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.repository.Pedido.PedidoRepository;
import com.ufcg.psoft.commerce.repository.Pizza.SaborRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes da entidade Pedido")
public class PedidoControllerTests {

    final String URI_ENTREGADORES = "/v1/entregadores";
    final String URL_CLIENTES = "/v1/clientes";
    final String URL_TEMPLATE = "/v1/pedidos";
    final String URI_ASSOCIACAO = "/associacao";
    final String URI_SABORES = "/pizza/v1/";

    final String URI_ESTABELECIMENTOS=  "/estabelecimentos";

    Estabelecimento estabelecimento;

    @Autowired
    MockMvc mockMvc;


    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    EntregadorRepository entregadorRepository;

    Entregador entregador, entregadorDois;
    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    ObjectMapper objectMapper;

    PedidoDTO pedidoDTO;

    Pedido pedido, pedidoDois;

    com.ufcg.psoft.commerce.enums.StatusPedido statusPedido;

    SaborPostPutDTO saborPostPutDTO;

    SaborPizza sabor;

    Cliente cliente;

    Pizza pizzaM;

    @BeforeEach
    void setup() {

        ClienteDTO clienteDTO = ClienteDTO.builder()
                .nome("Cliente de Exemplo")
                .endereco("Rua Exemplo, 123")
                .codigoAcesso("123456")
                .build();

        cliente = clienteRepository.save(objectMapper.convertValue(clienteDTO, Cliente.class));

        Set<SaborPizza> cardapio = new HashSet<>();
        Set<Entregador> entregadores = new HashSet<>();
        entregadores.add(entregador);
        entregadores.add(entregadorDois);

        objectMapper.registerModule(new JavaTimeModule());
        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .nome("bia")
                .saboresPizza(cardapio)
                .entregadores(entregadores)
                .codigoAcesso("654311")
                .build());
        sabor = saborRepository.save(SaborPizza.builder()
                .saborDaPizza("Calabresa")
                .tipoDeSabor("salgado")
                .valorMedia(10.0)
                .valorGrande(15.0)
                .disponibilidadeSabor(true)
                //.estabelecimento(estabelecimento)
                .build());
        saborPostPutDTO = SaborPostPutDTO.builder()
                .saborDaPizza(sabor.getSaborDaPizza())
                .tipoDeSabor(sabor.getTipoDeSabor())
                .valorMedia(sabor.getValorMedia())
                .valorGrande(sabor.getValorGrande())
                .disponibilidadeSabor(sabor.getDisponibilidadeSabor())
                .build();

        pizzaM = Pizza.builder()
                .sabor1(sabor)
                .tamanho(TamanhoPizza.MEDIA)
                .build();


        List<Pizza> pizzas = List.of(pizzaM);

        pedido = pedidoRepository.save(Pedido.builder()
                .codigoAcesso("123456")
                .cliente(cliente.getId())
                .estabelecimento(estabelecimento.getId())
                .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                .enderecoEntrega("Rua nova,123")
                .pizzas(pizzas)
                .status(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_RECEBIDO)
                .build());



        pedidoDTO = PedidoDTO.builder()
                .codigoAcesso(pedido.getCodigoAcesso())
                .clienteId(pedido.getCliente())
                .estabelecimentoId(pedido.getEstabelecimento())
                .metodoPagamento(pedido.getMetodoPagamento())
                .enderecoEntrega(pedido.getEnderecoEntrega())
                .pizzas(pedido.getPizzas())
                .build();

        entregador = entregadorRepository.save(Entregador.builder()
                .nome("Entregador Um")
                .placaVeiculo("ABC-1234")
                .corVeiculo("Branco")
                .tipoVeiculo("Carro")
                .codigoAcesso("123456")
                .isDisponibilidade(true)
                .estadoDeDisposicao("Descanso")
                .build());

        entregadorDois = entregadorRepository.save(Entregador.builder()
                .nome("EntregadorDois")
                .placaVeiculo("ABC-4321")
                .corVeiculo("Preto")
                .tipoVeiculo("Moto")
                .codigoAcesso("234567")
                .isDisponibilidade(true)
                .estadoDeDisposicao("Descanso")
                .build());


    }

    @AfterEach
    void tearDown() {

        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        saborRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        entregadorRepository.deleteAll();

    }

    @Nested
    @DisplayName("Conjunto de casos de verificação Pedido")
    class PedidoVerificacao {

        @Test
        @DisplayName("quando Criar Pedido com Dados Válidos então o pedido é criado e retornado")
        void quandoCriarPedidoComDadosValidos() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();


            // Act
            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoResultado = objectMapper.readValue(resultadoStr, Pedido.class);
            pedidoRepository.save(pedidoResultado);
            // Assert
            //assertNotNull(pedidoResultado.getId());
            assertTrue(pedidoResultado.getId() > 0);
            assertEquals(cliente.getId(), pedidoResultado.getCliente());
            assertEquals(estabelecimento.getId(), pedidoResultado.getEstabelecimento());
            assertNotNull(pedidoResultado.getMetodoPagamento());
            assertNotNull(pedidoResultado.getEnderecoEntrega());
            assertEquals(pedidoDTO.getEnderecoEntrega(), pedidoResultado.getEnderecoEntrega());
        }

        @Test
        @DisplayName("quando Criar Pedido Com Dados Inválidos retorna badRequest")
        void quandoCriarPedidoComVariosDadosInvalidos() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("")
                    .pizzas(pizzas)
                    .build();

            // Act
            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoResultado = objectMapper.readValue(resultadoStr, Pedido.class);

        }

        @Test
        @DisplayName("quando Criar Pedido com Código de Acesso em Branco então lança exceção correspondente")
        void quandoCriarPedidoComCodigoAcessoEmBranco() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            // Configurar PedidoDTO com código de acesso em branco
            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("")  // Dados inválidos: código de acesso em branco
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            // Act and Assert
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            // Verificar a exceção lançada
            assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);

            // Verificar a mensagem de erro na resposta
            String responseContent = result.getResponse().getContentAsString();
            assertTrue(responseContent.contains("O código de acesso não pode estar vazio"));
        }

        @Test
        @DisplayName("quando Criar Pedido com Código de Acesso Inválido então lança exceção correspondente")
        void quandoCriarPedidoComCodigoAcessoInvalido() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            // Configurar PedidoDTO com código de acesso inválido
            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("código_inválido")  // Dados inválidos: código de acesso inválido
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            // Act and Assert
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            // Verificar a exceção lançada
            assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);

            // Verificar a mensagem de erro na resposta
            String responseContent = result.getResponse().getContentAsString();
            assertTrue(responseContent.contains("Código de acesso inválido"));
        }

        @Test
        @DisplayName("quando Criar Pedido com Estabelecimento Inválido então lança exceção correspondente")
        void quandoCriarPedidoComEstabelecimentoInvalido() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            // Configurar PedidoDTO com estabelecimento inválido
            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(999L)  // Estabelecimento inexistente
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            // Act and Assert
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            // Verificar a exceção lançada
            assertTrue(result.getResolvedException() instanceof EstabelecimentoIdNaoEncontradoException);

            // Verificar a mensagem de erro na resposta
            String responseContent = result.getResponse().getContentAsString();
            assertTrue(responseContent.contains("O estabelecimento consultado nao existe!"));
        }

        @Test
        @DisplayName("quando Criar Pedido com Cliente Inválido então lança exceção correspondente")
        void quandoCriarPedidoComClienteInvalido() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            // Configurar PedidoDTO com cliente inválido
            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(999L)  // Cliente inexistente
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            // Act and Assert
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            // Verificar a exceção lançada
            assertTrue(result.getResolvedException() instanceof ClienteNaoEncontradoException);

            // Verificar a mensagem de erro na resposta
            String responseContent = result.getResponse().getContentAsString();
            assertTrue(responseContent.contains("Cliente não Encontrado"));
        }

        @Test
        @DisplayName("quando Criar Pedido com Pizza Inválida então lança exceção correspondente")
        void quandoCriarPedidoComPizzaInvalida() throws Exception {
            // Arrange
            // Configurar PedidoDTO com pizza inválida (sem sabor)
            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(Collections.emptyList())  // Dados inválidos: lista de pizzas vazia
                    .build();

            // Act and Assert
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            // Verificar a exceção lançada
            assertTrue(result.getResolvedException() instanceof PizzaSemSaboresException);

            // Verificar a mensagem de erro na resposta
            String responseContent = result.getResponse().getContentAsString();
            assertTrue(responseContent.contains("Sabor Nao Existe"));
        }

        @Test
        @DisplayName("quando Criar Pedido com Endereço de Entrega Vazio então é entregue no endereço principal")
        void quandoCriarPedidoComEnderecoDeEntregaVazio() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("")  // Endereço de entrega vazio
                    .pizzas(pizzas)
                    .build();

            // Act
            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoResultado = objectMapper.readValue(resultadoStr, Pedido.class);

            // Assert
            assertNotNull(pedidoResultado.getId());
            assertTrue(pedidoResultado.getId() > 0);
            assertEquals(cliente.getId(), pedidoResultado.getCliente());
            assertEquals(estabelecimento.getId(), pedidoResultado.getEstabelecimento());
            assertNotNull(pedidoResultado.getMetodoPagamento());
            assertNotNull(pedidoResultado.getEnderecoEntrega());
            assertEquals(cliente.getEndereco(), pedidoResultado.getEnderecoEntrega());
        }
    }

    @Nested
    @DisplayName("Atualização de Pedidos")
    class AtualizacaoDePedidos {

        @Test
        @DisplayName("quando Atualizar Pedido com Novo Endereço de Entrega então o pedido é atualizado corretamente")
        void quandoAtualizarPedidoComNovoEnderecoEntrega() throws Exception {
            // Arrange
            // Cria um pedido inicial
            List<Pizza> pizzas = List.of(pizzaM);

            pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoBase = objectMapper.readValue(resultadoStr, Pedido.class);

            // Novo endereço de entrega
            String novoEndereco = "Rua Nova, 456";
            pedidoDTO.setEnderecoEntrega(novoEndereco);

            // Act
            resultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URL_TEMPLATE + "/" + pedidoBase.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk()) // 200
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoAtualizado = objectMapper.readValue(resultadoStr, Pedido.class);

            // Assert
            assertEquals(pedidoBase.getId(), pedidoAtualizado.getId());
            assertEquals(pedidoBase.getCodigoAcesso(), pedidoAtualizado.getCodigoAcesso()); // Código de acesso não deve ser atualizado
            assertEquals(novoEndereco, pedidoAtualizado.getEnderecoEntrega());
        }

        @Test
        @DisplayName("quando Atualizar Pedido com Método de Pagamento PIX então o pedido é atualizado corretamente")
        void quandoAtualizarPedidoComMetodoPagamentoPIX() throws Exception {
            // Arrange
            // Cria um pedido inicial
            List<Pizza> pizzas = List.of(pizzaM);

            pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoBase = objectMapper.readValue(resultadoStr, Pedido.class);

            // Novo método de pagamento PIX


            pedidoDTO.setMetodoPagamento(MetodoPagamento.PIX);

            // Act
            resultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URL_TEMPLATE + "/" + pedidoBase.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk()) // 200
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoAtualizado = objectMapper.readValue(resultadoStr, Pedido.class);

            // Assert
            assertEquals(pedidoBase.getId(), pedidoAtualizado.getId());
            assertEquals(pedidoBase.getCodigoAcesso(), pedidoAtualizado.getCodigoAcesso()); // Código de acesso não deve ser atualizado
            assertEquals(MetodoPagamento.PIX, pedidoAtualizado.getMetodoPagamento());
        }

        @Test
        @DisplayName("quando Atualizar Pedido com Método de Pagamento Débito então o pedido é atualizado corretamente")
        void quandoAtualizarPedidoComMetodoPagamentoDebito() throws Exception {
            // Arrange
            // Cria um pedido inicial
            List<Pizza> pizzas = List.of(pizzaM);

            pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_DEBITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoBase = objectMapper.readValue(resultadoStr, Pedido.class);



            pedidoDTO.setMetodoPagamento(MetodoPagamento.CARTAO_DEBITO);

            // Act
            resultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URL_TEMPLATE + "/" + pedidoBase.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk()) // 200
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoAtualizado = objectMapper.readValue(resultadoStr, Pedido.class);

            // Assert
            assertEquals(pedidoBase.getId(), pedidoAtualizado.getId());
            assertEquals(pedidoBase.getCodigoAcesso(), pedidoAtualizado.getCodigoAcesso()); // Código de acesso não deve ser atualizado
            assertEquals(MetodoPagamento.CARTAO_DEBITO, pedidoAtualizado.getMetodoPagamento());
        }

        @Test
        @DisplayName("quando Atualizar Pedido com Endereço de Entrega em Branco então utiliza o endereço principal do cliente")
        void quandoAtualizarPedidoComEnderecoEntregaEmBrancoUsaEnderecoPrincipalDoCliente() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            // Cria um pedido inicial
            pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_DEBITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoBase = objectMapper.readValue(resultadoStr, Pedido.class);

            // Novo endereço de entrega em branco
            pedidoDTO.setEnderecoEntrega("");

            // Act
            String resultadoAtualizadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URL_TEMPLATE + "/" + pedidoBase.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk()) // 200
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoAtualizado = objectMapper.readValue(resultadoAtualizadoStr, Pedido.class);

            // Assert
            assertNotNull(pedidoAtualizado.getId());
            assertTrue(pedidoAtualizado.getId() > 0);
            assertEquals(cliente.getId(), pedidoAtualizado.getCliente());
            assertEquals(estabelecimento.getId(), pedidoAtualizado.getEstabelecimento());
            assertNotNull(pedidoAtualizado.getMetodoPagamento());
            assertNotNull(pedidoAtualizado.getEnderecoEntrega());
            assertEquals(cliente.getEndereco(), pedidoAtualizado.getEnderecoEntrega());
        }


    }

    @Nested
    @DisplayName("Exclusão de Pedidos")
    class ExclusaoDePedidos {

        @Test
        @DisplayName("quando Excluir Pedido Existente então o pedido é removido com sucesso")
        void quandoExcluirPedidoExistente() throws Exception {
            // Criar o pedido
            List<Pizza> pizzas = List.of(pizzaM);

            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_DEBITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andReturn();

            Pedido pedidoBase = objectMapper.readValue(result.getResponse().getContentAsString(), Pedido.class);

            // Excluir o pedido
            mockMvc.perform(MockMvcRequestBuilders.delete(URL_TEMPLATE + "/" + pedidoBase.getId()))
                    .andExpect(status().isNoContent());


            // Verificar se o pedido foi removido
            assertFalse(pedidoRepository.existsById(pedidoBase.getId()));
        }

        @Test
        @DisplayName("quando Excluir Pedido Inexistente retorna notFound")
        void quandoExcluirPedidoInexistente() throws Exception {
            // Tentar excluir um pedido inexistente
            mockMvc.perform(MockMvcRequestBuilders.delete(URL_TEMPLATE + "/999"))
                    .andExpect(status().isNotFound()); // 404
        }

    }

    @Nested
    @DisplayName("Busca de Pedidos")
    class BuscaDePedidos {

        @Test
        @DisplayName("quando Buscar Pedido Existente então o pedido é retornado")
        void quandoBuscarPedidoExistente() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_DEBITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andReturn();

            Pedido pedidoBase = objectMapper.readValue(result.getResponse().getContentAsString(), Pedido.class);

            // Act
            result = mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/" + pedidoBase.getId()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            Pedido pedidoEncontrado = objectMapper.readValue(result.getResponse().getContentAsString(), Pedido.class);

            // Assert
            assertEquals(pedidoBase.getId(), pedidoEncontrado.getId());
            assertEquals(pedidoDTO.getCodigoAcesso(), pedidoEncontrado.getCodigoAcesso());
            // ... outras verificações conforme necessário
        }

        @Test
        @DisplayName("quando Buscar Pedido por ID Inexistente então Retorna Not Found")
        void quandoBuscarPedidoPorIdInexistenteEntaoRetornaNotFound() throws Exception {
            // Act and Assert
            mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("quando Listar Pedidos então os pedidos são retornados")
        void quandoListarPedidos() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            PedidoDTO pedidoDTO1 = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_DEBITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            PedidoDTO pedidoDTO2 = PedidoDTO.builder()
                    .codigoAcesso("789012")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.PIX)
                    .enderecoEntrega("Rua Velha, 456")
                    .pizzas(pizzas)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO1)))
                    .andExpect(MockMvcResultMatchers.status().isCreated());

            mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO2)))
                    .andExpect(MockMvcResultMatchers.status().isCreated());

            // Act
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            // Assert
            List<Pedido> pedidosEncontrados = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Pedido>>() {
            });
            assertEquals(3, pedidosEncontrados.size());

            // Você pode adicionar mais verificações conforme necessário
            assertTrue(pedidosEncontrados.stream().anyMatch(pedido -> pedido.getCodigoAcesso().equals(pedidoDTO1.getCodigoAcesso())));
            //assertTrue(pedidosEncontrados.stream().anyMatch(pedido -> pedido.getCodigoAcesso().equals(pedidoDTO2.getCodigoAcesso())));
        }

        @Test
        @DisplayName("quando Listar Pedidos com ID Inválido então retorna Lista Vazia")
        void quandoListarPedidosComIdInvalido() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            PedidoDTO pedidoDTO1 = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_DEBITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            PedidoDTO pedidoDTO2 = PedidoDTO.builder()
                    .codigoAcesso("789012")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_DEBITO)
                    .enderecoEntrega("Rua Velha, 456")
                    .pizzas(pizzas)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO1)))
                    .andExpect(MockMvcResultMatchers.status().isCreated());

            mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO2)))
                    .andExpect(MockMvcResultMatchers.status().isCreated());

            // Act //Assert
            mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("conjunto de testes para verificaçao do status dos pedidos")
    class StatusPedido {
        @Test
        @DisplayName("Quando um pedido for criado, seu status deverá ser 'PEDIDO_RECEBIDO'")
        public void criaStatus() throws Exception {
            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoResultado = objectMapper.readValue(resultadoStr, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertEquals(statusPedido.PEDIDO_RECEBIDO, pedidoResultado.getStatusPedido());
        }

        @Test
        @DisplayName("Quando o cliente confirmar o pagamento, o status do pedido devera ser alterado para PEDIDO_EM_PREPARO")
        public void preparaPedido() throws Exception {

            String metodo = String.valueOf(pedidoDTO.getMetodoPagamento());
            // confirma o pagamento
            String ResultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URL_TEMPLATE + "/" + pedido.getId() + "/confirmar-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("metodoPagamento", metodo)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoResultado = objectMapper.readValue(ResultadoStr, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();
            assertEquals(pedidoResultado.getStatusPedido(), statusPedido.PEDIDO_EM_PREPARO);
        }


        // teste completo e adaptado para a funcionalidade da US19, onde a atribuiaçao de um entregador que está a mais tempo esperando e o envio do pedido sao feitos de forma automatica
        @Test
        @DisplayName("Quando o funcionario do estabelecimento confirmar que o pedido está pronto, o status devera ser PEDIDO_PRONTO")
        public void confirmaPedidoPronto() throws Exception{

            //entregador 1 se associa ao estabelecimento
            String responseJsonString1 = mockMvc.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultadoAssociaçao = objectMapper.readValue(responseJsonString1, Associacao.AssociacaoBuilder.class).build();

            //Entregador 1 é aprovado
            String responseJsonString2 = mockMvc.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("idAssociacao", String.valueOf(resultadoAssociaçao.getId())))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Entregador 2 se associa ao estabelecimento
            String responseJsonString3 = mockMvc.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregadorDois.getId().toString())
                            .param("codigoAcesso", entregadorDois.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultadoAssociaçao2 = objectMapper.readValue(responseJsonString3, Associacao.AssociacaoBuilder.class).build();

            //Entregador 2 é aprovado
            String responseJsonString4 = mockMvc.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("idAssociacao", String.valueOf(resultadoAssociaçao2.getId())))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Entregador dois muda sua disposiçao para 'ativo' (primeiro do que o entregador 1)
            String responseJsonString5 = mockMvc.perform(put(URI_ENTREGADORES + "/DisponibilidadeEntregador/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("CodigoAcesso", entregadorDois.getCodigoAcesso())
                            .param("NovaDisponibilidade", "Ativo")
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Thread.sleep(2000);

            //Entregador 1 muda sua disposiçao para 'ativo'
            String responseJsonString6 = mockMvc.perform(put(URI_ENTREGADORES + "/DisponibilidadeEntregador/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("CodigoAcesso", entregador.getCodigoAcesso())
                            .param("NovaDisponibilidade", "Ativo")
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Funcionario confirma que o pedido está pronto e o pedido é enviado e atribuido ao entregador 2 automaticamente
            String ResultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/pedido-pronto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoResultado = objectMapper.readValue(ResultadoStr, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();
            assertEquals(pedidoResultado.getStatusPedido(), statusPedido.PEDIDO_EM_ROTA);
        }

        @Test
        @DisplayName("Quando o pedido a ser dado como finalizado não existir")
        public void confirmaPedidoProntoInexistente() throws Exception{
            String responseJsonString = mockMvc.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultado = objectMapper.readValue(responseJsonString, Associacao.AssociacaoBuilder.class).build();

            String responseJsonString2 = mockMvc.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("idAssociacao", String.valueOf(resultado.getId())))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String ResultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/pedido-pronto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(93l))
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultadoErro = objectMapper.readValue(ResultadoStr, CustomErrorType.class);

            assertEquals("Pedido Nao Encontrado", resultadoErro.getMessage());
        }

        @Test
        @DisplayName("Quando o funcionario do estabelecimento atribuir o pedido a um entregador, o status deverá ser PEDIDO_EM_ROTA")
        public void enviaPedido() throws Exception{

            String responseJsonString = mockMvc.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultado = objectMapper.readValue(responseJsonString, Associacao.AssociacaoBuilder.class).build();

            String responseJsonString2 = mockMvc.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("idAssociacao", String.valueOf(resultado.getId())))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String responseJsonString6 = mockMvc.perform(put(URI_ENTREGADORES + "/DisponibilidadeEntregador/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("CodigoAcesso", entregador.getCodigoAcesso())
                            .param("NovaDisponibilidade", "Ativo")
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String ResultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/envia-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoResultado = objectMapper.readValue(ResultadoStr, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();
            assertEquals(pedidoResultado.getStatusPedido(), statusPedido.PEDIDO_EM_ROTA);
        }

        @Test
        @DisplayName("Quando o cliente confirmar a entrega, o status do pedido devera ser PEDIDO_ENTREGUE")
        public void confirmaEntregaPedido()throws Exception{


            //cria uma associaçao para um entregador
            String responseJsonString = mockMvc.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", String.valueOf(estabelecimento.getId())))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultadoAssociaçao = objectMapper.readValue(responseJsonString, Associacao.AssociacaoBuilder.class).build();

            //aprova o entregador e o adiciona ao estabelecimento
            String responseJsonString2 = mockMvc.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("idAssociacao", String.valueOf(resultadoAssociaçao.getId())))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String responseJsonString6 = mockMvc.perform(put(URI_ENTREGADORES + "/DisponibilidadeEntregador/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("CodigoAcesso", entregador.getCodigoAcesso())
                            .param("NovaDisponibilidade", "Ativo")
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // estabelecimento envia o pedido para o cliente atraves deste entregador
            String ResultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/pedido-pronto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //cliente confirma entrega
            String ResultadoStr3 = mockMvc.perform(MockMvcRequestBuilders.put(URL_CLIENTES + "/" + cliente.getId() + "/confirma-entrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoResultado = objectMapper.readValue(ResultadoStr3, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();
            assertEquals(pedidoResultado.getStatusPedido(), statusPedido.PEDIDO_ENTREGUE);
        }

    }


    @Nested
    @DisplayName("conjunto de testes para cancelar pedidos")
    class CancelarPedido {

        @Test
        @DisplayName("Quando o cliente cancela um pedido, o status do pedido deve ser 'CANCELADO'")
        public void cancelaPedidoComSucesso() throws Exception {
            List<Pizza> pizzas = List.of(pizzaM);

            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            // Cria o pedido
            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoResponseDTO = objectMapper.readValue(resultadoStr, PedidoResponseDTO.class);

            // Cliente cancela o pedido
            mockMvc.perform(delete(URL_TEMPLATE + "/" + pedidoResponseDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isNoContent());

            // Obtém o pedido cancelado para verificar o status
            String resultadoCancelamentoStr = mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/" + pedidoResponseDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            // Verifica se o pedido foi cancelado corretamente
            assertEquals("Pedido Nao Encontrado", resultadoCancelamentoStr);
        }

        @Test
        @DisplayName("Quando o cliente não pode cancelar um pedido, deve retornar status 'BAD_REQUEST'")
        public void clienteNaoPodeCancelarPedido() throws Exception {
            List<Pizza> pizzas = List.of(pizzaM);


            // Cria o pedido
            String resultadoStr = mockMvc.perform(post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoResponseDTO = objectMapper.readValue(resultadoStr, PedidoResponseDTO.class);


            String responseJsonString1 = mockMvc.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultadoAssociaçao = objectMapper.readValue(responseJsonString1, Associacao.AssociacaoBuilder.class).build();

            //Entregador 1 é aprovado
            String responseJsonString2 = mockMvc.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("idAssociacao", String.valueOf(resultadoAssociaçao.getId())))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String responseJsonString6 = mockMvc.perform(put(URI_ENTREGADORES + "/DisponibilidadeEntregador/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("CodigoAcesso", entregador.getCodigoAcesso())
                            .param("NovaDisponibilidade", "Ativo")
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            // Confirma que o pedido está pronto
            mockMvc.perform(put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/pedido-pronto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .param("idPedido", String.valueOf(pedidoResponseDTO.getId()))
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk());

            // Cliente tenta cancelar o pedido
            mockMvc.perform(delete(URL_TEMPLATE + "/" + pedidoResponseDTO.getId() + "/cancelar-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest());

        }

        @Test
        @DisplayName("Quando o cliente tenta cancelar um pedido com código de acesso incorreto, deve retornar status 'BAD_REQUEST'")
        public void clienteTentaCancelarPedidoComCodigoAcessoIncorreto() throws Exception {
            List<Pizza> pizzas = List.of(pizzaM);

            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            // Cria o pedido
            String resultadoStr = mockMvc.perform(post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoResponseDTO = objectMapper.readValue(resultadoStr, PedidoResponseDTO.class);

            // Cliente tenta cancelar o pedido com código de acesso incorreto
            mockMvc.perform(delete(URL_TEMPLATE + "/" + pedidoResponseDTO.getId() + "/cancelar-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", "codigoIncorreto")) // Código de acesso incorreto
                    .andExpect(status().isBadRequest());
        }

    }

    @Nested
    @DisplayName("Testes de Consulta de Pedidos")
    class ConsultaPedidosTests {

        //Rodar Isolado

//        @Test
//        @DisplayName("Quando Cliente Consulta Pedido com Código Correto, Deve Retornar o Pedido Correspondente")
//        void quandoClienteConsultaPedidoComCodigoCorretoDeveRetornarPedidoCorrespondente() throws Exception {
//
//            MvcResult consultaResult = mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/" + cliente.getId() + "/consulta-pedido")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .param("idPedido", String.valueOf(pedido.getId()))
//                            .param("codigoAcessoCliente", cliente.getCodigoAcesso()))
//                    .andExpect(status().isOk())
//                    .andReturn();
//
//            String consultaResultContent = consultaResult.getResponse().getContentAsString();
//            PedidoDTO pedidoConsultado = objectMapper.readValue(consultaResultContent, PedidoDTO.class);
//
//            // Assert
//            assertNotNull(pedidoConsultado);
//            assertEquals(pedido.getId(), pedidoConsultado.getId());
//        }

        @Test
        @DisplayName("Quando Cliente Consulta Pedido com Código Incorreto, Deve Retornar Erro")
        void quandoClienteConsultaPedidoComCodigoIncorretoDeveRetornarErro() throws Exception {
            // Simule um cenário onde o código de acesso do cliente é incorreto
            String codigoAcessoIncorreto = "codigo_incorreto";

            // Execute a consulta
            mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/" + pedido.getId() + "/consulta-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", codigoAcessoIncorreto))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("Quando Cliente Consulta Pedido que não Pertence a Ele, Deve Retornar Erro")
        void quandoClienteConsultaPedidoQueNaoPertenceAEleDeveRetornarErro() throws Exception {
            // Crie um novo pedido associado a um cliente diferente
            Cliente clienteOutro = criarCliente("OutroCliente", "Rua Outra, 456", "654321");
            Pedido pedidoOutroCliente = criarPedido(clienteOutro, estabelecimento);

            // Execute a consulta com o código de acesso do cliente original
            mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/" + pedidoOutroCliente.getId() + "/consulta-pedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest());
        }

        // Método auxiliar para criar um cliente
        private Cliente criarCliente(String nome, String endereco, String codigoAcesso) {
            ClienteDTO clienteDTO = ClienteDTO.builder()
                    .nome(nome)
                    .endereco(endereco)
                    .codigoAcesso(codigoAcesso)
                    .build();
            return clienteRepository.save(objectMapper.convertValue(clienteDTO, Cliente.class));
        }

        // Método auxiliar para criar um pedido associado a um cliente
        private Pedido criarPedido(Cliente cliente, Estabelecimento estabelecimento) {
            List<Pizza> pizzas = List.of(pizzaM);
            return pedidoRepository.save(Pedido.builder()
                    .codigoAcesso("123456")
                    .cliente(cliente.getId())
                    .estabelecimento(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua nova,123")
                    .pizzas(pizzas)
                    .status(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_RECEBIDO)
                    .build());
        }


        @Test
        @DisplayName("Quando Cliente Consulta Histórico de Pedidos, Deve Retornar Lista de Pedidos Correspondente")
        void quandoClienteConsultaHistoricoPedidosEntaoRetornaListaCorrespondente() throws Exception {
            // Arrange
            //Salva um novo sabor
            MvcResult consultaResult = mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/historico")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idCliente", String.valueOf(cliente.getId()))
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso()))
                    .andExpect(status().isOk())
                    .andReturn();


            String consultaResultContent = consultaResult.getResponse().getContentAsString();
            List<PedidoDTO> historicoPedidos = objectMapper.readValue(consultaResultContent, new TypeReference<List<PedidoDTO>>() {});

            // Assert
            assertNotNull(historicoPedidos);
            assertEquals(1, historicoPedidos.size());
        }

        @Test
        @DisplayName("Quando Cliente Consulta Histórico de Pedidos com Código de Acesso Incorreto, Deve Retornar Erro")
        void quandoClienteConsultaHistoricoPedidosComCodigoAcessoIncorretoDeveRetornarErro() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            PedidoDTO pedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            // Criação do pedido
            mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isCreated());

            // Tentativa de consulta do histórico com código de acesso incorreto
            mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/historico")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idCliente", String.valueOf(cliente.getId()))
                            .param("codigoAcessoCliente", "codigoIncorreto")) // Código de acesso incorreto
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Quando Cliente Consulta Histórico de Pedidos por Status, Deve Retornar Lista Correspondente")
        void quandoClienteConsultaHistoricoPedidosPorStatusDeveRetornarListaCorrespondente() throws Exception {
            // Arrange
            List<Pizza> pizzas = List.of(pizzaM);

            // Criação do primeiro pedido
            PedidoDTO primeiroPedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 123")
                    .pizzas(pizzas)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(primeiroPedidoDTO)))
                    .andExpect(status().isCreated());

            // Criação do segundo pedido
            PedidoDTO segundoPedidoDTO = PedidoDTO.builder()
                    .codigoAcesso("123456")
                    .clienteId(cliente.getId())
                    .estabelecimentoId(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua Nova, 456")
                    .pizzas(pizzas)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCodigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(segundoPedidoDTO)))
                    .andExpect(status().isCreated());

            // Consulta do histórico por status
            MvcResult consultaResult = mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/historico/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idCliente", String.valueOf(cliente.getId()))
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("status", "PEDIDO_RECEBIDO"))
                    .andExpect(status().isOk())
                    .andReturn();

            String consultaResultContent = consultaResult.getResponse().getContentAsString();
            List<PedidoDTO> historicoPedidos = objectMapper.readValue(consultaResultContent, new TypeReference<List<PedidoDTO>>() {});

            // Assert
            assertNotNull(historicoPedidos);
            assertEquals(3, historicoPedidos.size());
        }


        @Test
        @DisplayName("Quando Cliente Consulta Histórico de Pedidos por Status Inválido, Deve Retornar Lista Vazia")
        void quandoClienteConsultaHistoricoPedidosPorStatusInvalidoDeveRetornarListaVazia() throws Exception {
            // Arrange
            // Consulta do histórico por status
            MvcResult consultaResult = mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/historico/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idCliente", String.valueOf(cliente.getId()))
                            .param("codigoAcessoCliente", cliente.getCodigoAcesso())
                            .param("status", "CANCELADO"))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            String consultaResultContent = consultaResult.getResponse().getContentAsString();
            List<PedidoDTO> historicoPedidos = objectMapper.readValue(consultaResultContent, new TypeReference<List<PedidoDTO>>() {});

            assertNotNull(historicoPedidos);
           assertTrue(historicoPedidos.isEmpty());
        }

    }

    @Nested
    @DisplayName("Testes para notificar o cliente sobre o pedido em rota")
    class notificarCliente {
        @Test
        @DisplayName("Quando atribuo um entregador existente a um pedido existente")
        @Transactional
        public void test01() throws Exception {

            // act
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream, true, "UTF-8");
            PrintStream originalSystemOut = System.out;
            System.setOut(printStream);
            pedido.setCliente(cliente.getId());
            pedido.setEntregador(entregador);
            pedido.setStatus(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_EM_ROTA);

            String respostaJson2 = mockMvc.perform(get(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/notificarCliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoAtualizado = objectMapper.readValue(respostaJson2, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertEquals(pedidoAtualizado.getStatusPedido(), com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_EM_ROTA);
            assertNotNull(pedidoAtualizado.getEntregador());

            try {
                String resultadoPrint = outputStream.toString();

                String regex = "Hibernate: .*";

                String resultadoFiltrado = resultadoPrint.replaceAll(regex, "").trim();

                String notificacaoEsperada = """
                         - PEDIDO EM ROTA -
                        Cliente: Cliente de Exemplo
                        Entregador: Entregador Um
                        Tipo do Veiculo: Carro
                        Cor do Veiculo: Branco
                        Placa do Veiculo: ABD-1234""";
                resultadoFiltrado.concat(notificacaoEsperada);
            } finally {
                System.setOut(originalSystemOut);
            }

        }

        @Test
        @DisplayName("Quando o código de estabelecimento é inválido")
        @Transactional
        public void test02() throws Exception {

            pedido.setCliente(cliente.getId());
            pedido.setEntregador(entregador);
            pedido.setStatus(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_EM_ROTA);

            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.get(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/notificarCliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .param("codigoAcesso", "aaaaa"))
                    .andExpect(status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(resultadoStr, CustomErrorType.class);

            // Assert
            assertAll(() -> assertEquals("Codigo de acesso invalido!", resultado.getMessage()));


        }

        @Test
        @DisplayName("Quando o ID de pedido é inválido")
        @Transactional
        public void test03() throws Exception {
            pedido.setCliente(cliente.getId());
            pedido.setEntregador(entregador);
            pedido.setStatus(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_EM_ROTA);
            Long idPedidoInvalido = -1L;

            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.get(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/notificarCliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(idPedidoInvalido))
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(resultadoStr, CustomErrorType.class);
            assertAll(() -> assertEquals("Pedido Nao Encontrado", resultado.getMessage()));
        }

        @Test
        @DisplayName("Quando o id de estabelecimento é inválido")
        @Transactional
        public void test04() throws Exception {

            pedido.setCliente(cliente.getId());
            pedido.setEntregador(entregador);
            pedido.setStatus(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_EM_ROTA);

            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.get(URI_ESTABELECIMENTOS + "/" + "85" + "/notificarCliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(resultadoStr, CustomErrorType.class);

            // Assert
            assertAll(() -> assertEquals("O estabelecimento consultado nao existe!", resultado.getMessage()));


        }

        @Test
        @DisplayName("Quando o status do pedido é inválido")
        @Transactional
        public void test05() throws Exception {

            pedido.setCliente(cliente.getId());
            pedido.setEntregador(entregador);
            pedido.setStatus(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_ENTREGUE);


            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.get(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/notificarCliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(resultadoStr, CustomErrorType.class);

            // Assert
            assertAll(() -> assertEquals("Status do pedido invalido!", resultado.getMessage()));
        }

        @Test
        @DisplayName("Quando o entregador do pedido é inválido")
        @Transactional
        public void test06() throws Exception {

            Entregador entregadorUm = null;

            pedido.setCliente(cliente.getId());
            pedido.setEntregador(entregadorUm);
            pedido.setStatus(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_EM_ROTA);


            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.get(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/notificarCliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(resultadoStr, CustomErrorType.class);

            // Assert
            assertAll(() -> assertEquals("O entregador consultado nao existe!", resultado.getMessage()));

        }

    }



    @Nested
    @DisplayName("Testes para notificar o cliente quando o pedido não pode ser entregue")
    class notificarClienteQuandoPedidoNaoPodeSerEntregue {
        @Test
        @DisplayName("Quando notifico cliente sobre a indisponibilidade de entregadores")
        @Transactional
        public void testNotificarClienteIndisponibilidadeEntregadores() throws Exception {
            pedido.setCliente(cliente.getId());
            pedido.setStatus(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_EM_ROTA);

            String responseBody = mockMvc.perform(put(URL_TEMPLATE + "/{id}/notificar-indisponibilidade-entregadores", pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = "Cliente notificado sobre a indisponibilidade de entregadores.";
            assertEquals(expectedResponse, responseBody);

        }

        @Test
        @DisplayName("Quando notificar cliente sobre a indisponibilidade de entregadores - Pedido não encontrado")
        @Transactional
        public void testNotificarClienteIndisponibilidadeEntregadoresPedidoNaoEncontrado() throws Exception {
            mockMvc.perform(put(URL_TEMPLATE + "/{id}/notificar-indisponibilidade-entregadores", -1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNotFound());
        }


        @Test
        @DisplayName("Quando notificar cliente sobre a indisponibilidade de entregadores - Status do pedido não é PEDIDO_EM_ROTA")
        @Transactional
        public void testNotificarClienteIndisponibilidadeEntregadoresStatusInvalido() throws Exception {
            pedido.setStatus(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_ENTREGUE);
            mockMvc.perform(put(URL_TEMPLATE + "/{id}/notificar-indisponibilidade-entregadores", pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Quando notificar cliente sobre a indisponibilidade de entregadores - Cliente não encontrado")
        @Transactional
        public void testNotificarClienteIndisponibilidadeEntregadoresClienteNaoEncontrado() throws Exception {
            mockMvc.perform(put(URL_TEMPLATE + "/{id}/notificar-indisponibilidade-entregadores", pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("Quando notificar cliente sobre a indisponibilidade de entregadores - Pedido não está em rota")
        @Transactional
        public void testNotificarClienteIndisponibilidadeEntregadoresPedidoNaoEmRota() throws Exception {
            pedido.setStatus(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_RECEBIDO);
            mockMvc.perform(put(URL_TEMPLATE + "/{id}/notificar-indisponibilidade-entregadores", pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Quando notificar cliente sobre a indisponibilidade de entregadores - Entregador já existe")
        @Transactional
        public void testNotificarClienteIndisponibilidadeEntregadoresEntregadorExistente() throws Exception {
            pedido.setEntregador(entregador);
            mockMvc.perform(put(URL_TEMPLATE + "/{id}/notificar-indisponibilidade-entregadores", pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("Quando notificar cliente sobre a indisponibilidade de entregadores - Pedido em rota e entregador atribuído")
        @Transactional
        public void testNotificarClientePedidoEmRotaComEntregador() throws Exception {
            pedido.setStatus(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_EM_ROTA);
            pedido.setEntregador(entregador);
            mockMvc.perform(put(URL_TEMPLATE + "/{id}/notificar-indisponibilidade-entregadores", pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isOk());
        }



    }


    @Nested
    @DisplayName("Teste Us19")
    class testeConjuntoUs{

        /*
         teste 'confirmaPedidoPronto' (linha 1068)
         testa as novas funcionalidades da US19, como a atribuiçao
         do pedido ao entregador que está esperando a mais tempo
         */
        @Test
        @DisplayName("quando nao houverem entregadores disponiveis, o pedido é adicionado aos pedidos em espera" +
                "e quando uma entrega for confimada o entregador é atribuido a este pedido em espera")
        public void pedidoSemEntregadoresDisponiveis() throws Exception{

            List<Pizza> pizzas = List.of(pizzaM);

            pedidoDois = pedidoRepository.save(Pedido.builder()
                    .codigoAcesso("234567")
                    .cliente(cliente.getId())
                    .estabelecimento(estabelecimento.getId())
                    .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                    .enderecoEntrega("Rua velha,123")
                    .pizzas(pizzas)
                    .status(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_RECEBIDO)
                    .build());


            //entregador 1 se associa ao estabelecimento
            String responseJsonString1 = mockMvc.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultadoAssociaçao = objectMapper.readValue(responseJsonString1, Associacao.AssociacaoBuilder.class).build();

            //Entregador 1 é aprovado
            String responseJsonString2 = mockMvc.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("idAssociacao", String.valueOf(resultadoAssociaçao.getId())))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Entregador 1 muda sua disposiçao para 'ativo'
            String responseJsonString6 = mockMvc.perform(put(URI_ENTREGADORES + "/DisponibilidadeEntregador/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("CodigoAcesso", entregador.getCodigoAcesso())
                            .param("NovaDisponibilidade", "Ativo")
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Funcionario confirma que o pedido está pronto e o pedido é enviado e atribuido ao entregador 1 automaticamente
            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/pedido-pronto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoResultado = objectMapper.readValue(resultadoStr, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();


            /*
            Funcionario comfirma o pedido
            mas nao possui entregadores disponiveis
            entao o pedido sera adicionado na espera
             */
            String resultadoStr2 = mockMvc.perform(MockMvcRequestBuilders.put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/pedido-pronto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(pedidoDois.getId()))
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            PedidoResponseDTO pedidoResultado2 = objectMapper.readValue(resultadoStr2, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();
            Estabelecimento estab = estabelecimentoRepository.findById(pedidoDois.getEstabelecimento()).get();

            // verifica que o entregador do pedido 2 é nulo, pois nao havia entregadores disponiveis
            assertNull(pedidoResultado2.getEntregador());
            // verifica que agora o estabelecimento possui um pedido em espera, que é o pedido Dois
            assertEquals("234567", estab.getPedidosEspera().get(0).getCodigoAcesso());

            /*
            cliente confirma entrega , entregador 1 passa a ser disponivel
            e é automaticamente atribuido ao pedido 2, que agora saiu da espera
             */
            String resultadoStr3 = mockMvc.perform(MockMvcRequestBuilders.put(URL_CLIENTES + "/" + cliente.getId() + "/confirma-entrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // verifica que agora o pedido 2 possui um entregador e o seu status está como 'PEDIDO_EM_ROTA'.
            pedidoDois= pedidoRepository.findById(pedidoDois.getId()).get();
            assertEquals("Entregador Um",pedidoDois.getEntregador().getNome());
            assertEquals(com.ufcg.psoft.commerce.enums.StatusPedido.PEDIDO_EM_ROTA, pedidoDois.getStatus());

        }
        // teste completo e adaptado para a funcionalidade da US19, onde a atribuiaçao de um entregador que está a mais tempo esperando e o envio do pedido sao feitos de forma automatica
        @Test
        @DisplayName("Quando o funcionario do estabelecimento confirmar que o pedido está pronto, o status devera ser PEDIDO_PRONTO")
        public void confirmaPedidoPronto() throws Exception{

            estabelecimento.getEntregadores().add(entregador);
            estabelecimento.getEntregadores().add(entregadorDois);
            estabelecimentoRepository.save(estabelecimento);


            // Entregador dois muda sua disposiçao para 'ativo' (primeiro do que o entregador 1)
            String responseJsonString5 = mockMvc.perform(put(URI_ENTREGADORES + "/DisponibilidadeEntregador/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("CodigoAcesso", entregadorDois.getCodigoAcesso())
                            .param("NovaDisponibilidade", "Ativo")
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Thread.sleep(2000);

            //Entregador 1 muda sua disposiçao para 'ativo'
            String responseJsonString6 = mockMvc.perform(put(URI_ENTREGADORES + "/DisponibilidadeEntregador/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("CodigoAcesso", entregador.getCodigoAcesso())
                            .param("NovaDisponibilidade", "Ativo")
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            //Funcionario confirma que o pedido está pronto e o pedido é enviado e atribuido ao entregador 2 automaticamente
            String ResultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/pedido-pronto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(pedido.getId()))
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO pedidoResultado = objectMapper.readValue(ResultadoStr, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();
            assertEquals(pedidoResultado.getStatusPedido(), statusPedido.PEDIDO_EM_ROTA);
        }

        @Test
        @DisplayName("Quando o pedido a ser dado como finalizado não existir")
        public void confirmaPedidoProntoInexistente() throws Exception{
            String responseJsonString = mockMvc.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultado = objectMapper.readValue(responseJsonString, Associacao.AssociacaoBuilder.class).build();

            String responseJsonString2 = mockMvc.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("idAssociacao", String.valueOf(resultado.getId())))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String ResultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId() + "/pedido-pronto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("idPedido", String.valueOf(93l))
                            .param("codigoAcessoEstabelecimento", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultadoErro = objectMapper.readValue(ResultadoStr, CustomErrorType.class);

            assertEquals("Pedido Nao Encontrado", resultadoErro.getMessage());
        }
    }
}


