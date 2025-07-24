package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MovimentacaoApiResponse {

    @JsonProperty("id-movimentacao")
    private Long idMovimentacao;

    @JsonProperty("id-processo")
    private Long idProcesso;

    @JsonProperty("unidade-origem")
    private String unidadeOrigem;

    @JsonProperty("data-envio-origem")
    private Date dataEnvioOrigem;

    @JsonProperty("enviado-por")
    private String enviadoPor;

    @JsonProperty("unidade-destino")
    private String unidadeDestino;

    @JsonProperty("data-recebimento-destino")
    private Date dataRecebimentoDestino;

    @JsonProperty("recebido-por")
    private String recebidoPor;
}