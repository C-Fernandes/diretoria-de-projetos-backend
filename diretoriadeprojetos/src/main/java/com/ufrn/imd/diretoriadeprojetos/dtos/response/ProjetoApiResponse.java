package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@ToString
public class ProjetoApiResponse {

    @JsonProperty("id-projeto-convenio")
    private Long idProjetoConvenio;

    @JsonProperty("id-projeto")
    private Long idProjeto;

    private Integer numero;
    private Integer ano;

    @JsonProperty("numero-cadastro")
    private Integer numeroCadastro;

    @JsonProperty("ano-cadastro")
    private Integer anoCadastro;

    @JsonProperty("numero-formatado")
    private String numeroFormatado;

    @JsonProperty("titulo-projeto")
    private String tituloProjeto;

    @JsonProperty("inicio-execucao")
    private Date inicioExecucao;

    @JsonProperty("fim-execucao")
    private Date fimExecucao;

    @JsonProperty("id-status-projeto")
    private Integer idStatusProjeto;

    @JsonProperty("descricao-status")
    private String descricaoStatus;

    @JsonProperty("data-ultima-alteracao")
    private Date dataUltimaAlteracao;

    @JsonProperty("id-tipo-projeto")
    private Integer idTipoProjeto;

    @JsonProperty("tipo-projeto")
    private String tipoProjeto;

    @JsonProperty("id-tipo-convenio")
    private Integer idTipoConvenio;

    @JsonProperty("tipo-convenio")
    private String tipoConvenio;

    @JsonProperty("projeto-inovacao")
    private boolean projetoInovacao;

    @JsonProperty("id-classificacao")
    private Integer idClassificacao;

    private String classificacao;

    @JsonProperty("nome-coordenador")
    private String nomeCoordenador;

    @JsonProperty("siape-coordenador")
    private Long siapeCoordenador;

    @JsonProperty("nome-proponente")
    private String nomeProponente;

    @JsonProperty("cpf-cnpj-proponente")
    private String cpfCnpjProponente;

    @JsonProperty("data-assinatura")
    private Instant dataAssinatura;

    @JsonProperty("valor-projeto")
    private Double valorProjeto;

    @JsonProperty("id-grande-area-conhecimento")
    private Integer idGrandeAreaConhecimento;

    @JsonProperty("grande-area-conhecimento")
    private String grandeAreaConhecimento;

    @JsonProperty("id-area-conhecimento")
    private Integer idAreaConhecimento;

    @JsonProperty("area-conhecimento")
    private String areaConhecimento;

    @JsonProperty("id-sub-area-conhecimento")
    private Integer idSubAreaConhecimento;

    @JsonProperty("sub-area-conhecimento")
    private String subAreaConhecimento;

    @JsonProperty("id-especialidade")
    private Integer idEspecialidade;

    private String especialidade;

    private String objetivo;

    @JsonProperty("objetivos-especificos")
    private String objetivosEspecificos;

    private String justificativa;
}
