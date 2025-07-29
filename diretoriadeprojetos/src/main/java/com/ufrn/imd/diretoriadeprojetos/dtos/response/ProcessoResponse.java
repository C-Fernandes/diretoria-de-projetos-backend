package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.Date;
import java.util.List;

import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Movimentacao;

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
public class ProcessoResponse {
    private long radical, numProtocolo, ano, dv, idProcesso, numeroSipac, anoSipac;
    private String nome, financiador, observacao;
    private Date dataInicio;
    private Coordenador coordenador;
    private double valor;
    private List<Movimentacao> movimentacoes;

}
