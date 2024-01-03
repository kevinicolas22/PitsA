package com.ufcg.psoft.commerce.repository.Entregador;

import com.ufcg.psoft.commerce.model.Entregador.Entregador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntregadorRepository extends JpaRepository<Entregador, Long> {

    public Boolean existsByCodigoAcesso(String codigoAcesso);


    public Optional<Entregador> findByCodigoAcesso(String codigoAcesso);
}
