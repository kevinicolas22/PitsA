package com.ufcg.psoft.commerce.service.Pizza;

import com.ufcg.psoft.commerce.dto.PizzaDTO.SaborPostPutDTO;
import com.ufcg.psoft.commerce.dto.PizzaDTO.SaborResponseDTO;
import com.ufcg.psoft.commerce.exception.Estabelecimento.CodigoAcessoEstabelecimentoException;
import com.ufcg.psoft.commerce.exception.Estabelecimento.EstabelecimentoNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Pizza.SaborPizzaExistenteException;
import com.ufcg.psoft.commerce.exception.Pizza.SaborPizzaNaoEncontradoException;
import com.ufcg.psoft.commerce.model.SaborPizza.SaborPizza;

import java.util.List;

public interface SaborService {
    SaborResponseDTO criarSabor(Long idEstabelecimento, String codigoAcessoEstabelecimento, SaborPostPutDTO sabor) throws SaborPizzaNaoEncontradoException, SaborPizzaExistenteException, EstabelecimentoNaoEncontradoException, CodigoAcessoEstabelecimentoException;

    SaborResponseDTO atualizarSaborPizza(Long idEstabelecimento, String codigoAcessoEstabelecimento,long idPizza, SaborPostPutDTO sabor) throws SaborPizzaNaoEncontradoException, EstabelecimentoNaoEncontradoException, CodigoAcessoEstabelecimentoException; // fazer o throws

    List<SaborResponseDTO> buscarTodosSaboresPizza(Long idEstabelecimento, String codigoAcessoEstabelecimento) throws CodigoAcessoEstabelecimentoException, EstabelecimentoNaoEncontradoException;


    void deletarSaborPizza(Long idEstabelecimento, String codigoAcessoEstabelecimento,long idPizza) throws SaborPizzaNaoEncontradoException, CodigoAcessoEstabelecimentoException, EstabelecimentoNaoEncontradoException;

    SaborResponseDTO buscarId(Long idEstabelecimento, String codigoAcessoEstabelecimento, long idPizza) throws EstabelecimentoNaoEncontradoException;

    SaborResponseDTO atualizarSaborPizzaDisponibilidade(Long idEstabelecimento, String codigoAcessoEstabelecimento, Long idPizza, SaborPostPutDTO sabor,Boolean disponibilidade);

    SaborPizza consultarSaborPizzaById(Long idPizza) throws SaborPizzaNaoEncontradoException;

    void salvarSaborPizzaCadastrado(SaborPizza saborPizza);
}
