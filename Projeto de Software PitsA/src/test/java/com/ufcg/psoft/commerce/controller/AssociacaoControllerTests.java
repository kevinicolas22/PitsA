
package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Associacao.Associacao;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.Estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.repository.Associacao.AssociacaoRepository;
import com.ufcg.psoft.commerce.repository.Entregador.EntregadorRepository;
import com.ufcg.psoft.commerce.repository.Estabelecimento.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.service.Entregador.EntregadorService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Associação")
class AssociacaoControllerTests {

    final String URI_ASSOCIACAO = "/associacao";

    @Autowired
    MockMvc driver;

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    EntregadorService entregadorService;

    @Autowired
    EstabelecimentoRepository estabelecimentoV1Repository;


    Entregador entregador;

    Estabelecimento estabelecimento;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        entregador = entregadorRepository.save(Entregador.builder()
                .nome("Entregador Um")
                .placaVeiculo("ABC-1234")
                .corVeiculo("Branco")
                .tipoVeiculo("Carro")
                .codigoAcesso("123456")
                .isDisponibilidade(true)
                .build()
        );


        estabelecimento = estabelecimentoV1Repository.save(Estabelecimento.builder()
                .codigoAcesso("654321")
                .build()
        );
    }

    @AfterEach
    void tearDown() {
        entregadorRepository.deleteAll();
        estabelecimentoV1Repository.deleteAll();
        associacaoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de criacao de associacao")
    class ClienteCriacaoAssociacao {

        @Test
        @DisplayName("Quando criamos uma associacao com sucesso")
        void testCriarAssociacaoComSucesso() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultado = objectMapper.readValue(responseJsonString, Associacao.AssociacaoBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(1, associacaoRepository.count()),
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(entregador.getId(), resultado.getEntregadorId()),
                    () -> assertEquals(estabelecimento.getId(), resultado.getEstabelecimentoId())
            );
        }

        @Test
        @DisplayName("Quando criamos uma associacao com entregador inexistente")
        void testCriarAssociacaoComEntregadorInexistente() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", "9999")
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, associacaoRepository.count()),
                    () -> assertEquals("O entregador consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos uma associacao com estabelecimento inexistente")
        void testCriarAssociacaoComEstabelecimentoInexistente() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", "9999"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, associacaoRepository.count()),
                    () -> assertEquals("O estabelecimento consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos uma associacao passando codigo de acesso invalido")
        void testCriarAssociacaoComCodigoDeAcessoInvalido() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", "654321")
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, associacaoRepository.count()),
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de aprovação de associacao")
    class ClienteAprovacaoAssociacao {

        @BeforeEach
        void setUp() {
            associacaoRepository.save(Associacao.builder()
                    .entregadorId(entregador.getId())
                    .estabelecimentoId(estabelecimento.getId())
                            .codigoAcesso(entregador.getCodigoAcesso())
                    .status(false)
                    .build()
            );
        }



        @Test
        @DisplayName("Quando aprovamos uma associacao com estabelecimento inexistente")
        void quandoAprovamosAssociacaoInexistente() throws Exception {
            // Arrange
            // Act
            String responseJsonString = driver.perform(put(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("idAssociacao", String.valueOf(10)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(1, associacaoRepository.count()),
                    () -> assertEquals("Associacao inexistente!", resultado.getMessage())
            );
        }


    }
}

