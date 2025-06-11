package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParceiroApiResponse {

    @JsonProperty("id-participe")
    private Long idParticipe;

    @JsonProperty("id-projeto")
    private Long idProjeto;

    @JsonProperty("financiador")
    private Boolean financiador;

    @JsonProperty("id-institucional-participe")
    private Long idInstitucionalParticipe;

    @JsonProperty("nome-participe")
    private String nomeParticipe;

    @JsonProperty("id-tipo-convenente")
    private Integer idTipoConvenente;

    @JsonProperty("tipo-convenente")
    private String tipoConvenente;
}
