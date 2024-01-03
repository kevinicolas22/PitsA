package com.ufcg.psoft.commerce.repository.Pedido;

import com.ufcg.psoft.commerce.enums.StatusPedido;
import com.ufcg.psoft.commerce.model.Pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PedidoRepository extends JpaRepository <Pedido, Long> {
    public Boolean existsByCodigoAcesso(String codigoAcesso);
    public Optional<Pedido> findByCodigoAcesso(String codigoAcesso);
    List<Pedido> findByClienteOrderByDataPedidoDescStatusAsc(long cliente);
    List<Pedido> findByClienteAndStatusOrderByDataPedidoDescStatusAsc(long cliente, StatusPedido status);
}
