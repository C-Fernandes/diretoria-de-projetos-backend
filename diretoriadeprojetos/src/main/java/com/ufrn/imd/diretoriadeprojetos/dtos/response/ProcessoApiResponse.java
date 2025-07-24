package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProcessoApiResponse {

    @JsonProperty("id-processo")
    private Long idProcesso;

    private Integer radical;

    @JsonProperty("num-protocolo")
    private Long numProtocolo;

    private Integer ano;

    private Integer dv;

    @JsonProperty("origem-processo")
    private String origemProcesso;

    @JsonProperty("data-autuacao")
    private Date dataAutuacao;

    @JsonProperty("usuario-autuacao")
    private String usuarioAutuacao;

    @JsonProperty("assunto-processo")
    private String assuntoProcesso;

    @JsonProperty("assunto-detalhado")
    private String assuntoDetalhado;

    @JsonProperty("natureza-processo")
    private String naturezaProcesso;

    @JsonProperty("orgao-origem")
    private String orgaoOrigem;

    private String status;

    @JsonProperty("data-cadastro")
    private Date dataCadastro;

    private String observacao;

    @JsonProperty("id-status")
    private Integer idStatus;

    @JsonProperty("id-natureza")
    private Integer idNatureza;

    @JsonProperty("cpf-cnpj")
    private String cpfCnpj;

    private String siape;
}