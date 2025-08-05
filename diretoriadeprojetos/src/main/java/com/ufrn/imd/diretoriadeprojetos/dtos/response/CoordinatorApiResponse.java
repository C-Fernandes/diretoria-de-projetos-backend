package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter

@ToString
public class CoordinatorApiResponse {
    @JsonProperty("id-docente")
    private Long idDocente;
    @JsonProperty("nome")
    private String nome;
    @JsonProperty("cpf")
    private String cpf;
    private String sexo;

    @JsonProperty("nome-identificacao")
    private String nomeIdentificacao;
    @JsonProperty("email")
    private String email;
    @JsonProperty("siape")
    private Long siape;

    @JsonProperty("digito-siape")
    private String digitoSiape;

    @JsonProperty("id-ativo")
    private Integer idAtivo;

    @JsonProperty("id-situacao")
    private Integer idSituacao;

    @JsonProperty("id-unidade")
    private Integer idUnidade;

    private String unidade;

    @JsonProperty("id-cargo")
    private Integer idCargo;

    private String cargo;

    @JsonProperty("regime-trabalho")
    private Integer regimeTrabalho;

    @JsonProperty("data-admissao")
    private Date dataAdmissao;

    @JsonProperty("id-institucional")
    private Long idInstitucional;

}
