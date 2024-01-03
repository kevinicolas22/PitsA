package com.ufcg.psoft.commerce.service.Associacao;

import com.ufcg.psoft.commerce.exception.Associacao.AssociacaoNaoEncontradaException;
import com.ufcg.psoft.commerce.exception.Associacao.EntregadorCodigoAcessoNaoEntradoException;
import com.ufcg.psoft.commerce.exception.Associacao.EntregadorIdNaoEncontradoException;
import com.ufcg.psoft.commerce.exception.Associacao.EstabelecimentoIdNaoEncontradoException;
import com.ufcg.psoft.commerce.model.Associacao.Associacao;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import com.ufcg.psoft.commerce.model.Estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.repository.Associacao.AssociacaoRepository;
import com.ufcg.psoft.commerce.repository.Entregador.EntregadorRepository;
import com.ufcg.psoft.commerce.repository.Estabelecimento.EstabelecimentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssociacaoService {

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    EntregadorRepository entregadorRepository;
    @Transactional
    public Associacao criarAssociacao(String idEntregador, String codigoAcessoEntregador,
                                      String idEstabelecimento){

        Long idNumericoEntregador = Long.parseLong(idEntregador);
        Long idNumericoEstabelecimento = Long.parseLong(idEstabelecimento);


        if (!estabelecimentoRepository.existsById(idNumericoEstabelecimento)){

            throw new EstabelecimentoIdNaoEncontradoException();

        }else if (!entregadorRepository.existsById(idNumericoEntregador)){

            throw new EntregadorIdNaoEncontradoException();

        }else if (!entregadorRepository.existsByCodigoAcesso(codigoAcessoEntregador)){

            throw new EntregadorCodigoAcessoNaoEntradoException();

        }

        Associacao associacao = Associacao.builder()
                .entregadorId(idNumericoEntregador)
                .estabelecimentoId(idNumericoEstabelecimento)
                .codigoAcesso(codigoAcessoEntregador)
                .build();

        return associacaoRepository.save(associacao);

    }

    public Associacao aprovarEntregador( String codigoAcesso, Long idAssociacao){
        if(!associacaoRepository.existsById(idAssociacao)){
            throw new AssociacaoNaoEncontradaException();
        }
        Associacao resultado= associacaoRepository.findById(idAssociacao).get();
        Long idNumericoEntregador = resultado.getEntregadorId();
        Long idNumericoEstatabelecimento =resultado.getEstabelecimentoId();
        Entregador entregador= entregadorRepository.findById(idNumericoEntregador).get();


        Estabelecimento estabelecimento = estabelecimentoRepository.findById(idNumericoEstatabelecimento).get();
        if(!associacaoRepository.existsByEstabelecimentoId(idNumericoEstatabelecimento)){

            throw new EstabelecimentoIdNaoEncontradoException();

        }else if (!associacaoRepository.existsByEntregadorId(idNumericoEntregador)){

            throw new EntregadorIdNaoEncontradoException();

        }else if (!estabelecimentoRepository.existsByCodigoAcesso(codigoAcesso)){

            throw new EntregadorCodigoAcessoNaoEntradoException();

        }else{

            if (!estabelecimento.getCodigoAcesso().equals(codigoAcesso)) throw new EntregadorCodigoAcessoNaoEntradoException();

        }

        resultado.setStatus(true);
        estabelecimento.getEntregadores().add(entregador);
        estabelecimentoRepository.save(estabelecimento);
        return associacaoRepository.save(resultado);

    }



}
