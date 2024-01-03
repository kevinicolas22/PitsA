package com.ufcg.psoft.commerce.dto.ClienteDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.validators.CodigoAcessoConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    @CodigoAcessoConstraint
    @JsonProperty("codigoAcesso")
    @NotBlank(message = "O código de acesso não pode estar vazio")
    private String codigoAcesso;

    @JsonProperty("nome")
    @NotBlank(message = "O nome não pode estar em branco")
    private String nome;

    @JsonProperty("endereco")
    @NotBlank(message = "O endereço não pode estar em branco")
    private String endereco;

    @JsonIgnore
    public String getCodigoAcesso() {
        return codigoAcesso;
    }

}



