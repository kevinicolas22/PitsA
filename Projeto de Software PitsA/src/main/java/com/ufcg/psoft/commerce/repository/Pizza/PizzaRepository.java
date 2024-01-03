package com.ufcg.psoft.commerce.repository.Pizza;

import com.ufcg.psoft.commerce.model.SaborPizza.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {

}

