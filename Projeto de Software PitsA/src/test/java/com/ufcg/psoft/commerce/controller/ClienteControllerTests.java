package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.ClienteDTO.ClienteDTO;
import com.ufcg.psoft.commerce.dto.PedidoDTO.PedidoDTO;
import com.ufcg.psoft.commerce.dto.PizzaDTO.SaborPostPutDTO;
import com.ufcg.psoft.commerce.enums.TamanhoPizza;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Cliente.Cliente;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.Estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.SaborPizza.Pizza;
import com.ufcg.psoft.commerce.model.SaborPizza.SaborPizza;
import com.ufcg.psoft.commerce.repository.Cliente.ClienteRepository;
import com.ufcg.psoft.commerce.repository.Estabelecimento.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.repository.Pizza.SaborRepository;
import com.ufcg.psoft.commerce.service.Cliente.ClienteService;
import com.ufcg.psoft.commerce.service.Pizza.SaborService;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes da entidade Clientes")
public class ClienteControllerTests {

    final String URL_TEMPLATE = "/v1/clientes";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ClienteRepository clienteRepository;

    ClienteDTO clienteDTO;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ClienteService clienteService;
    @Autowired
    SaborService saborService;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    PedidoDTO pedidoDTO;

    Estabelecimento estabelecimento;

    SaborPostPutDTO saborPostPutDTO;

    SaborPizza sabor;

    Cliente cliente;

    Pizza pizzaM;


    @BeforeEach
    void setup() {


        Set<SaborPizza> cardapio = new HashSet<>();
        Set<Entregador> entregadores = new HashSet<>();

        objectMapper.registerModule(new JavaTimeModule());
        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .nome("bia")
                .saboresPizza(cardapio)
                .entregadores(entregadores)
                .codigoAcesso("65431")
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
                .build();clienteRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação Cliente")
    class ClienteVerificacaoNome {

        @Test
        @DisplayName("quando Criar Cliente com Dados Válidos então o cliente é criado e retornado")
        void quandoCriarClienteComDadosValidos() throws Exception {
            // Arrange
            clienteDTO = ClienteDTO.builder()
                    .nome("Novo Cliente")
                    .endereco("Rua Nova, 123")
                    .codigoAcesso("123456")
                    .build();
            // Act
            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente clienteResultado = objectMapper.readValue(resultadoStr, Cliente.class);

            // Assert
            assertNotNull(clienteResultado.getId());
            assertTrue(clienteResultado.getId() > 0);
            assertNotNull(clienteResultado.getNome());
            assertEquals(clienteDTO.getNome(), clienteResultado.getNome());
            assertNotNull(clienteResultado.getEndereco());
            assertEquals(clienteDTO.getEndereco(), clienteResultado.getEndereco());
            assertNotNull(clienteResultado.getCodigoAcesso());
            assertEquals(clienteDTO.getCodigoAcesso(), clienteResultado.getCodigoAcesso());

        }

        @Test
        @DisplayName("quando Criar Cliente Com Dados Inválidos retorna badRequest com detalhes corretos")
        void quandoCriarClienteComDadosInvalidos() throws Exception {
            // Arrange
            ClienteDTO clienteDTO = ClienteDTO.builder()
                    .codigoAcesso("")
                    .nome("")
                    .endereco("")
                    .build();

            // Act
            String resultadoStr = mockMvc.perform(post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(clienteDTO)))
                    .andExpect(status().isBadRequest()) // 400
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType customErrorType = objectMapper.readValue(resultadoStr, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", customErrorType.getMessage()),
                    () -> assertEquals(4, customErrorType.getErrors().size()),
                    () -> assertTrue(
                            customErrorType.getErrors().stream().anyMatch(
                                    (msg) -> msg.toUpperCase().contains("O CÓDIGO DE ACESSO NÃO PODE ESTAR VAZIO")
                            )
                    ),
                    () -> assertTrue(
                            customErrorType.getErrors().stream().anyMatch(
                                    (msg) -> msg.toUpperCase().contains("O NOME NÃO PODE ESTAR EM BRANCO")
                            )
                    ),
                    () -> assertTrue(
                            customErrorType.getErrors().stream().anyMatch(
                                    (msg) -> msg.toUpperCase().contains("O ENDEREÇO NÃO PODE ESTAR EM BRANCO")
                            )
                    )
            );
        }

        @Test
        @DisplayName("quando Criar Cliente com Código de Acesso Inválido, então lança exceção ClienteCodigoAcessoInvalidoException")
        void quandoCriarClienteComCodigoAcessoInvalido() throws Exception {
            // Arrange: Código de acesso com menos de 6 dígitos
            ClienteDTO clienteDTO = ClienteDTO.builder()
                    .nome("Novo Cliente")
                    .endereco("Rua Nova, 123")
                    .codigoAcesso("12345") // Código de acesso inválido
                    .build();

            // Act e Assert: Deve lançar uma exceção ClienteCodigoAcessoInvalidoException
            mockMvc.perform(post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteDTO)))
                    .andExpect(status().isBadRequest()) ;// 400
        }

    }

    @Nested
    @DisplayName("Atualização de Clientes")
    class AtualizacaoDeClientes {

        @Test
        @DisplayName("quando Atualizar Cliente Com Dados Válidos então o cliente é atualizado com sucesso")
        void quandoAtualizarClienteComDadosValidos() throws Exception {
            // Arrange
            ClienteDTO clienteDTO = ClienteDTO.builder()
                    .codigoAcesso("123456")
                    .nome("João")
                    .endereco("Rua ABC")
                    .build();

            Cliente clienteBase = clienteRepository.save(objectMapper.convertValue(clienteDTO, Cliente.class));

            // Atualizar os detalhes do cliente (exceto o código de acesso)
            clienteDTO.setNome("Maria");
            clienteDTO.setEndereco("Avenida XYZ");

            // Act
            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URL_TEMPLATE + "/" + clienteBase.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(clienteDTO)))
                    .andExpect(status().isOk()) // 200
                    .andReturn().getResponse().getContentAsString();

            Cliente clienteAtualizado = objectMapper.readValue(resultadoStr, Cliente.class);

            // Assert
            assertEquals(clienteBase.getId(), clienteAtualizado.getId());
            assertEquals(clienteBase.getCodigoAcesso(), clienteAtualizado.getCodigoAcesso()); // Código de acesso não deve ser atualizado
            assertEquals(clienteDTO.getNome(), clienteAtualizado.getNome());
            assertEquals(clienteDTO.getEndereco(), clienteAtualizado.getEndereco());
        }

        @Test
        @DisplayName("quando Atualizar Cliente Com Dados Inválidos retorna badRequest com detalhes corretos")
        void quandoAtualizarClienteComDadosInvalidos() throws Exception {
            // Arrange
            ClienteDTO clienteDTO = ClienteDTO.builder()
                    .codigoAcesso("12345")
                    .nome("João")
                    .endereco("Rua ABC")
                    .build();

            Cliente clienteBase = clienteRepository.save(objectMapper.convertValue(clienteDTO, Cliente.class));

            // Atualizar os detalhes do cliente com dados inválidos
            clienteDTO.setCodigoAcesso("");
            clienteDTO.setNome("");
            clienteDTO.setEndereco("");

            // Act
            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.put(URL_TEMPLATE + "/" + clienteBase.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(clienteDTO)))
                    .andExpect(status().isBadRequest()) // 400
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType customErrorType = objectMapper.readValue(resultadoStr, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", customErrorType.getMessage()),
                    () -> assertEquals(4, customErrorType.getErrors().size()),
                    () -> assertTrue(
                            customErrorType.getErrors().stream().anyMatch(
                                    (msg) -> msg.toUpperCase().contains("O CÓDIGO DE ACESSO NÃO PODE ESTAR VAZIO")
                            )
                    ),
                    () -> assertTrue(
                            customErrorType.getErrors().stream().anyMatch(
                                    (msg) -> msg.toUpperCase().contains("O NOME NÃO PODE ESTAR EM BRANCO")
                            )
                    ),
                    () -> assertTrue(
                            customErrorType.getErrors().stream().anyMatch(
                                    (msg) -> msg.toUpperCase().contains("O ENDEREÇO NÃO PODE ESTAR EM BRANCO")
                            )
                    )
            );
        }

        @Test
        @DisplayName("quando Atualizar Cliente com Código de Acesso Inválido, então lança exceção ClienteCodigoAcessoInvalidoException")
        void quandoAtualizarClienteComCodigoAcessoInvalido() throws Exception {
            // Arrange: Criar um cliente com código de acesso válido
            ClienteDTO clienteDTO = ClienteDTO.builder()
                    .nome("Novo Cliente")
                    .endereco("Rua Nova, 123")
                    .codigoAcesso("123456") // Código de acesso válido
                    .build();

            String resultadoStr = mockMvc.perform(post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteDTO)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            Cliente clienteCriado = objectMapper.readValue(resultadoStr, Cliente.class);

            // Atualizar o cliente com código de acesso inválido
            clienteDTO.setCodigoAcesso("12345"); // Código de acesso inválido

            // Act e Assert: Deve lançar uma exceção ClienteCodigoAcessoInvalidoException
            mockMvc.perform(put(URL_TEMPLATE + "/" + clienteCriado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteDTO)))
                    .andExpect(status().isBadRequest()); // 400
        }
    }

    @Nested
    @DisplayName("Exclusão de Clientes")
    class ExclusaoDeClientes {

        @Test
        @DisplayName("quando Excluir Cliente Existente então o cliente é removido com sucesso")
        void quandoExcluirClienteExistente() throws Exception {
            // Arrange
            ClienteDTO clienteDTO = ClienteDTO.builder()
                    .codigoAcesso("12345")
                    .nome("João")
                    .endereco("Rua ABC")
                    .build();

            Cliente clienteBase = clienteRepository.save(objectMapper.convertValue(clienteDTO, Cliente.class));

            // Act
            mockMvc.perform(MockMvcRequestBuilders.delete(URL_TEMPLATE + "/" + clienteBase.getId()))
                    .andExpect(status().isNoContent()); // 204

            // Assert
            assertFalse(clienteRepository.existsById(clienteBase.getId()));
        }

        @Test
        @DisplayName("quando Excluir Cliente Inexistente retorna notFound")
        void quandoExcluirClienteInexistente() throws Exception {
            // Arrange: Cliente com ID inexistente

            // Act
            mockMvc.perform(MockMvcRequestBuilders.delete(URL_TEMPLATE + "/999"))
                    .andExpect(status().isNotFound()); // 404
        }
    }

    @Nested
    @DisplayName("Busca de Clientes")
    class BuscaDeClientes {

        @Test
        @DisplayName("quando Buscar Cliente Existente então o cliente é retornado")
        void quandoBuscarClienteExistente() throws Exception {
            // Arrange
            ClienteDTO clienteDTO = ClienteDTO.builder()
                    .codigoAcesso("12345")
                    .nome("João")
                    .endereco("Rua ABC")
                    .build();

            Cliente clienteBase = clienteRepository.save(objectMapper.convertValue(clienteDTO, Cliente.class));

            // Act
            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/" + clienteBase.getId()))
                    .andExpect(status().isOk()) // 200
                    .andReturn().getResponse().getContentAsString();

            Cliente clienteEncontrado = objectMapper.readValue(resultadoStr, Cliente.class);

            // Assert
            assertEquals(clienteBase.getId(), clienteEncontrado.getId());
            assertEquals(clienteDTO.getCodigoAcesso(), clienteEncontrado.getCodigoAcesso());
            assertEquals(clienteDTO.getNome(), clienteEncontrado.getNome());
            assertEquals(clienteDTO.getEndereco(), clienteEncontrado.getEndereco());
        }

        @Test
        @DisplayName("quando Buscar Cliente Inexistente retorna notFound")
        void quandoBuscarClienteInexistente() throws Exception {
            // Arrange: Cliente com ID inexistente

            // Act
            mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/999"))
                    .andExpect(status().isNotFound()); // 404
        }

        @Test
        @DisplayName("quando buscar por varios Cliente Com Id válido então todos os Cliente são retornado")
        void quandoBuscararVariosClienteComIdValido() throws Exception {
            //Arrange
            Cliente clienteUm = Cliente.builder()
                    .nome("Cliente1")
                    .endereco("Rua Nova, 123")
                    .codigoAcesso("123456")
                    .build();

            Cliente clienteDois = Cliente.builder()
                    .nome("Cliente1")
                    .endereco("Rua Nova, 123")
                    .codigoAcesso("123456")
                    .build();

            clienteUm = clienteRepository.save(clienteUm);
            clienteDois = clienteRepository.save(clienteDois);

            //Act

            String resultadoStr = mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)) //Header
                    .andExpect(status().isOk()) //200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cliente> clientes = objectMapper.readValue(resultadoStr, new TypeReference<List<Cliente>>(){});
            //Assert
            Cliente finalClienteUm = clienteUm;
            Cliente finalClienteDois = clienteDois;

            assertAll(
                    () -> assertTrue(clientes.contains(finalClienteUm)),
                    () -> assertTrue(clientes.contains(finalClienteDois)),
                    () -> assertEquals(2,clientes.size()),
                    () -> Assertions.assertNotNull(clientes)
            );
        }

        @Test
        @DisplayName("quando buscar por varios Cliente Com Id Inválidos então todos os Cliente são retornado")
        void quandoBuscararVariosClienteComIdInalidos() throws Exception {
            //Arrange
            Cliente clienteUm = Cliente.builder()
                    .nome("Cliente1")
                    .endereco("Rua Nova, 123")
                    .codigoAcesso("123456")
                    .build();

            Cliente clienteDois = Cliente.builder()
                    .nome("Cliente1")
                    .endereco("Rua Nova, 123")
                    .codigoAcesso("123456")
                    .build();

            clienteUm = clienteRepository.save(clienteUm);
            clienteDois = clienteRepository.save(clienteDois);

            //Act

            mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE + "/999"))
                    .andExpect(status().isNotFound()); // 404

        }

    }

}
