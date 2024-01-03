package com.ufcg.psoft.commerce.service.Pizza;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import com.ufcg.psoft.commerce.exception.Pizza.DisponibilidadeSaborException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.commerce.dto.PizzaDTO.SaborPostPutDTO;
import com.ufcg.psoft.commerce.dto.PizzaDTO.SaborResponseDTO;
import com.ufcg.psoft.commerce.exception.Estabelecimento.CodigoAcessoEstabelecimentoException;
import com.ufcg.psoft.commerce.exception.Estabelecimento.EstabelecimentoNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Pizza.SaborPizzaExistenteException;
import com.ufcg.psoft.commerce.exception.Pizza.SaborPizzaNaoEncontradoException;
import com.ufcg.psoft.commerce.model.Estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.SaborPizza.SaborPizza;
import com.ufcg.psoft.commerce.repository.Estabelecimento.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.repository.Pizza.SaborRepository;

@Service
public class SaborV1Service implements SaborService {
    @Autowired
    private SaborRepository saborRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;


    @Autowired
    private ModelMapper modelMapper;
    @Override
    public SaborResponseDTO criarSabor(Long idEstabelecimento, String codigoAcessoEstabelecimento,SaborPostPutDTO saborDTO) throws SaborPizzaNaoEncontradoException, SaborPizzaExistenteException, EstabelecimentoNaoEncontradoException, CodigoAcessoEstabelecimentoException {

        Estabelecimento estabelecimento = obterEstabelecimento(idEstabelecimento, codigoAcessoEstabelecimento);

        SaborPizza saborPizza = SaborPizza.builder()
                .saborDaPizza(saborDTO.getSaborDaPizza())
                .tipoDeSabor(saborDTO.getTipoDeSabor())
                .valorMedia(saborDTO.getValorMedia())
                .valorGrande(saborDTO.getValorGrande())
                .disponibilidadeSabor(saborDTO.getDisponibilidadeSabor())
                .build();

        saborRepository.save(saborPizza);
        return modelMapper.map(saborPizza, SaborResponseDTO.class);
    }



    @Override
    public SaborResponseDTO atualizarSaborPizza(Long idEstabelecimento, String codigoAcessoEstabelecimento,long idPizza, SaborPostPutDTO saborDTO) throws SaborPizzaNaoEncontradoException, EstabelecimentoNaoEncontradoException, CodigoAcessoEstabelecimentoException {

        Estabelecimento estabelecimento = obterEstabelecimento(idEstabelecimento, codigoAcessoEstabelecimento);

        Optional<SaborPizza> saborOptional = saborRepository.findById(idPizza);

        if(!saborOptional.isPresent()){
            throw new SaborPizzaNaoEncontradoException();
        }

        SaborPizza sabor = saborOptional.get();
        sabor.setSaborDaPizza(saborDTO.getSaborDaPizza());
        sabor.setValorMedia(saborDTO.getValorMedia());
        sabor.setValorGrande(saborDTO.getValorGrande());
        sabor.setTipoDeSabor(saborDTO.getTipoDeSabor());
        sabor.setDisponibilidadeSabor(saborDTO.getDisponibilidadeSabor());

        saborRepository.save(sabor);


        return SaborResponseDTO.builder()
                .idPizza(sabor.getIdPizza())
                .saborDaPizza(sabor.getSaborDaPizza())
                .tipoDeSabor(sabor.getTipoDeSabor())
                .valorMedia(sabor.getValorMedia())
                .valorGrande(sabor.getValorGrande())
                .disponibilidadeSabor(sabor.getDisponibilidadeSabor())
                .build();

    }

    @Override
    public List<SaborResponseDTO> buscarTodosSaboresPizza(Long idEstabelecimento, String codigoAcessoEstabelecimento) throws CodigoAcessoEstabelecimentoException, EstabelecimentoNaoEncontradoException {

        Estabelecimento estabelecimento = obterEstabelecimento(idEstabelecimento, codigoAcessoEstabelecimento);

        List<SaborResponseDTO> retorno = new ArrayList<>();

        for (SaborPizza sabor : estabelecimento.getSaboresPizza()) {
            retorno.add(modelMapper.map(sabor, SaborResponseDTO.class));
        }

        return retorno;
    }

    @Override
    public void deletarSaborPizza(Long idEstabelecimento, String codigoAcessoEstabelecimento,long idPizza) throws SaborPizzaNaoEncontradoException, CodigoAcessoEstabelecimentoException, EstabelecimentoNaoEncontradoException {

        Estabelecimento estabelecimento = obterEstabelecimento(idEstabelecimento, codigoAcessoEstabelecimento);
        if(saborRepository.findById(idPizza).isPresent()){
            saborRepository.deleteById(idPizza);
        } else{
            throw new SaborPizzaNaoEncontradoException();
        }
    }

    @Override
    public SaborResponseDTO buscarId(Long idEstabelecimento, String codigoAcessoEstabelecimento, long idPizza) throws EstabelecimentoNaoEncontradoException {

        Estabelecimento estabelecimento = obterEstabelecimento(idEstabelecimento, codigoAcessoEstabelecimento);


        if(saborRepository.findById(idPizza).isPresent()){
            SaborPizza saborPizza = saborRepository.findById(idPizza).get();
            return SaborResponseDTO.builder()
                    .idPizza(saborPizza.getIdPizza())
                    .saborDaPizza(saborPizza.getSaborDaPizza())
                    .tipoDeSabor(saborPizza.getTipoDeSabor())
                    .valorMedia(saborPizza.getValorMedia())
                    .valorGrande(saborPizza.getValorGrande())
                    .disponibilidadeSabor(saborPizza.getDisponibilidadeSabor())
                    .build();
        } else{
            throw new SaborPizzaNaoEncontradoException();
        }

    }

    @Override
    public SaborResponseDTO atualizarSaborPizzaDisponibilidade(Long idEstabelecimento, String codigoAcessoEstabelecimento, Long idPizza, SaborPostPutDTO saborDTO, Boolean disponibilidade) {

        Estabelecimento estabelecimento = obterEstabelecimento(idEstabelecimento, codigoAcessoEstabelecimento);

        Optional<SaborPizza> saborOptional = saborRepository.findById(idPizza);

        if(!saborOptional.isPresent()){
            throw new SaborPizzaNaoEncontradoException();
        }
        if(saborDTO.getDisponibilidadeSabor().equals(disponibilidade)){
            throw new DisponibilidadeSaborException();
        }
        SaborPizza saborPizza = saborOptional.get();
        saborPizza.setDisponibilidadeSabor(saborDTO.getDisponibilidadeSabor());

        saborRepository.save(saborPizza);

        return SaborResponseDTO.builder()
                .idPizza(saborDTO.getIdPizza())
                .disponibilidadeSabor(saborDTO.getDisponibilidadeSabor())
                .build();
    }



    // metodo auxiliar
    private Estabelecimento obterEstabelecimento(Long idEstabelecimento, String codigoAcessoEstabelecimento) {
        Optional<Estabelecimento> estabelecimentoOptinial = estabelecimentoRepository.findById(idEstabelecimento);

        if(!estabelecimentoOptinial.isPresent()){
            throw new EstabelecimentoNaoEncontradoException();
        }

        Estabelecimento estabelecimento = estabelecimentoOptinial.get();

        if(!estabelecimento.getCodigoAcesso().equals(codigoAcessoEstabelecimento)){
            throw new CodigoAcessoEstabelecimentoException();
        }
        return estabelecimento;
    }

    @Override
    public SaborPizza consultarSaborPizzaById(Long idPizza) throws SaborPizzaNaoEncontradoException {
        return saborRepository.findById(idPizza)
                .orElseThrow(SaborPizzaNaoEncontradoException::new);

    }

    @Override
    public void salvarSaborPizzaCadastrado(SaborPizza saborPizza) {
        saborRepository.save(saborPizza);
    }

}
