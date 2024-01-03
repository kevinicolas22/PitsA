package com.ufcg.psoft.commerce.service.Entregador;

import com.ufcg.psoft.commerce.dto.Entregador.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.commerce.exception.Entregador.CodigoAcessoEntregadorException;
import com.ufcg.psoft.commerce.exception.Entregador.DadosDeDisponibiliadadeInvalidosException;
import com.ufcg.psoft.commerce.exception.Entregador.EntregadorNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Entregador.TipoDeVeiculoInvalidoException;
import com.ufcg.psoft.commerce.exception.Estabelecimento.CodigoAcessoInvalidoException;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.repository.Entregador.EntregadorRepository;
import com.ufcg.psoft.commerce.repository.Estabelecimento.EstabelecimentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EntregadorV1Service implements EntregadorService{

    @Autowired
    private EntregadorRepository entregadorRepository;


    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;
    @Bean
    private ModelMapper mapeadorEntregador(){

        return new ModelMapper();
    }

    private Entregador converteTDOParaEntidade(EntregadorPostPutRequestDTO entregadorPostPutRequestDTO){

        return mapeadorEntregador().map(entregadorPostPutRequestDTO, Entregador.class);

    }
    @Override
    public Entregador adicionarEntregador(EntregadorPostPutRequestDTO entregadorPostPutDTO){

        Entregador entregador = converteTDOParaEntidade(entregadorPostPutDTO);
        if(entregador.getCodigoAcesso().length() != 6){

            throw new CodigoAcessoInvalidoException();
        }
        return entregadorRepository.save(entregador);
        //
    }

    @Override
    public Entregador getEntregador(Long id) {
        Optional<Entregador> entregadorOptional = entregadorRepository.findById(id);
        if (entregadorOptional.isPresent()) {
            return entregadorOptional.get();
        } else {
            throw new EntregadorNaoEncontradoException();
        }
    }

    @Override
    public List<Entregador> getEntregadores(){
        return entregadorRepository.findAll();
    }

    @Override
    public Entregador updateEntregador(Long id, String codigoAcessoEntregador, EntregadorPostPutRequestDTO entregadorPostPutDTO){
        Optional<Entregador> entregadorOptional = entregadorRepository.findById(id);
        if(!entregadorOptional.isPresent()){
            throw new EntregadorNaoEncontradoException();
        }
        String codigoAcesso = entregadorPostPutDTO.getCodigoAcesso();
        Entregador entregador= entregadorOptional.get();
        if (codigoAcesso == null || codigoAcesso.isEmpty() || !isValidCodigoAcesso(codigoAcesso)) {
            throw new CodigoAcessoEntregadorException();
        }
        if(!entregador.getCodigoAcesso().equals(codigoAcessoEntregador)){
            throw new CodigoAcessoEntregadorException();
        }

         if(entregadorPostPutDTO.getTipoVeiculo().equals("carro") || entregadorPostPutDTO.getTipoVeiculo().equals("moto")){
             entregador.setNome(entregadorPostPutDTO.getNome());
             entregador.setCorVeiculo(entregadorPostPutDTO.getCorVeiculo());
             entregador.setPlacaVeiculo(entregadorPostPutDTO.getPlacaVeiculo());
             entregador.setTipoVeiculo(entregadorPostPutDTO.getTipoVeiculo());
             return entregadorRepository.save(entregador);
         }
         else{
             throw new TipoDeVeiculoInvalidoException();
         }


        
           
        
}


    @Override
    public Entregador updateStatus(Long id){
        Optional<Entregador> entregadorOptional = entregadorRepository.findById(id);
        Entregador entregador= entregadorOptional.get();
        entregador.setAprovado(true);
//
        return entregador;
    }

    @Override
    public void removerEntregador(Long id, String codigoAcessoEntregador){
        Optional<Entregador> entregadorOptional = entregadorRepository.findById(id);
        if(!entregadorOptional.isPresent()){
            throw new EntregadorNaoEncontradoException();
        }
        Entregador entregador= entregadorOptional.get();
        String codigoAcesso= entregador.getCodigoAcesso();
        
        
        if (codigoAcesso == null || codigoAcesso.isEmpty() || !isValidCodigoAcesso(codigoAcesso)) {
            throw new CodigoAcessoEntregadorException();
        }
        if(!entregador.getCodigoAcesso().equals(codigoAcessoEntregador)){
            throw new CodigoAcessoEntregadorException();
        }
        entregadorRepository.deleteById(id);
    }

    private boolean isValidCodigoAcesso(String codigoAcesso) {
        return codigoAcesso.matches("[0-9]+") && codigoAcesso.length() == 6;
    }

    public Entregador atualizaDisponibilidade(Long id, String codigoAcessoEntregador, boolean disponibilidade){
        Optional<Entregador> entregadorOptional = entregadorRepository.findById(id);
        if(!entregadorOptional.isPresent()){
            throw new EntregadorNaoEncontradoException();
        }

        Entregador entregador= entregadorOptional.get();
        String codigoAcesso= entregador.getCodigoAcesso();

        if (codigoAcesso == null || codigoAcesso.isEmpty() || !isValidCodigoAcesso(codigoAcesso)) {
            throw new CodigoAcessoEntregadorException();
        }
        if(!entregador.getCodigoAcesso().equals(codigoAcessoEntregador)){
            throw new CodigoAcessoEntregadorException();
        }

        entregador.setDisponibilidade(disponibilidade);
        entregadorRepository.save(entregador);

        return entregador;

    }


    public Entregador alterarDiponibilidade(String codigoAcesso, String disponibilidade) throws CodigoAcessoEntregadorException {
        Entregador entregador;
        if (!entregadorRepository.existsByCodigoAcesso(codigoAcesso)){
            throw new CodigoAcessoEntregadorException();
        }
        if(!(disponibilidade.equals("Descanso") || disponibilidade.equals("Ativo"))){
            throw new DadosDeDisponibiliadadeInvalidosException();

        }


          entregador = entregadorRepository.findByCodigoAcesso(codigoAcesso).get();

        LocalDateTime tempoAtual= LocalDateTime.now();
        entregador.setEstadoDeDisposicao(disponibilidade);
        entregador.setTempoDisponivel(tempoAtual);
        entregador = entregadorRepository.save(entregador);

        return entregador;
    }
}
