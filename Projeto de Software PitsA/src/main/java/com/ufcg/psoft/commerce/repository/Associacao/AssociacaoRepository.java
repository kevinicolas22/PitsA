package com.ufcg.psoft.commerce.repository.Associacao;

import com.ufcg.psoft.commerce.model.Associacao.Associacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssociacaoRepository extends JpaRepository<Associacao, Long> {


    public Boolean existsByEntregadorId(Long id);

    public Boolean existsByEstabelecimentoId(Long id);

    public Optional<Associacao> findByEntregadorIdAndEstabelecimentoId(
            Long entregadorId,
            Long estabelecimentoId
    );

}
