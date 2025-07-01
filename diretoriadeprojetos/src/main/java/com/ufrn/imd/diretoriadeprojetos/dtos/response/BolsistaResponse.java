package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BolsistaResponse {

    private UUID id;
    private String cpf;
    private String nome;
    private String email;
    private String tipoSuperior;
    private String curso;
    private Boolean docente;
    private String formacao;
    private String numeroFunpec, numeroSipac, anoSipac;
    private Date dataInicio, dataFim;
    private int rubrica;
    private Double valor;
    private Integer cHSemanal;
}
