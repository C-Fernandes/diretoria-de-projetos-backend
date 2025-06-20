package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrigemRecursoApiResponse {

    @JsonProperty("id-projeto-origem-recurso")
    private Long idProjetoOrigemRecurso;

    @JsonProperty("id-projeto")
    private Long idProjeto;

    @JsonProperty("id-financiador")
    private Long idFinanciador;

    @JsonProperty("nome-financiador")
    private String nomeFinanciador;

    @JsonProperty("id-esfera-origem-recurso")
    private Integer idEsferaOrigemRecurso;

    @JsonProperty("nome-esfera-origem-recurso")
    private String nomeEsferaOrigemRecurso;

    @JsonProperty("id-subesfera-origem-recurso")
    private Integer idSubesferaOrigemRecurso;

    @JsonProperty("nome-subesfera-origem-recurso")
    private String nomeSubesferaOrigemRecurso;

    private Double valor;
    private Double percentual;
}