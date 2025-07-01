package com.ufrn.imd.diretoriadeprojetos.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class DadosPagamento {
    private String numeroProjeto;
    private int numeroRubrica;
    private String nomeMembro;
    private String nivel;
    private String tipo;
    private int cargaHoraria;
    private Double valor;
    private String competencia;

}