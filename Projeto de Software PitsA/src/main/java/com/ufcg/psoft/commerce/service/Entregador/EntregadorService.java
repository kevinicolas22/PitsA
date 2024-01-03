package com.ufcg.psoft.commerce.service.Entregador;

import com.ufcg.psoft.commerce.dto.Entregador.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.Entregador.Entregador;

import java.util.List;

public interface EntregadorService {

    Entregador adicionarEntregador(EntregadorPostPutRequestDTO entregador);

    Entregador getEntregador(Long id);

    List<Entregador> getEntregadores();//

    public Entregador updateStatus(Long id);

    public Entregador updateEntregador(Long id,String codigoAcessoEntregador, EntregadorPostPutRequestDTO entregador);

    public void removerEntregador(Long id, String codigoAcessoEntregador);

    public Entregador atualizaDisponibilidade(Long id, String codigoAcessoEntregador, boolean disponibilidade);
}

