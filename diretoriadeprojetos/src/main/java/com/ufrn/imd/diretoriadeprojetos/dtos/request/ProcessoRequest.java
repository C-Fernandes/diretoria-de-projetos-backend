package com.ufrn.imd.diretoriadeprojetos.dtos.request;

import java.util.Date;
import java.util.List;

import com.ufrn.imd.diretoriadeprojetos.models.Coordinator;
import com.ufrn.imd.diretoriadeprojetos.models.Coordinator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessoRequest {
    private long radical, numProtocolo, ano, dv, idProcesso, numeroSipac, anoSipac;
    private String nome, financiador, observacao;
    private Date dataInicio;
    private Coordinator coordenador;
    private double valor;

}
