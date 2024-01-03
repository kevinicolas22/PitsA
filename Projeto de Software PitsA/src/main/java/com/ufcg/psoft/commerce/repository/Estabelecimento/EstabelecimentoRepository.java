package com.ufcg.psoft.commerce.repository.Estabelecimento;

import com.ufcg.psoft.commerce.model.Estabelecimento.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    public Boolean existsByCodigoAcesso(String codigoAcesso);

    public Optional<Estabelecimento> findByCodigoAcesso(String codigoAcesso);



}
