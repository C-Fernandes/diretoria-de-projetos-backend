package com.ufrn.imd.diretoriadeprojetos.dtos.request;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjetoRequest {
    private long numeroSipac;
    private long anoSipac;
    private String titulo;
    private Long idProjeto;
    private double valor;
    private String contaContrato;
    private Date dataInicio;
    private Date dataFim;
    private boolean leiDeInformatica;
    private boolean sebrae;
    private boolean embrapii;
    private boolean residencia;

    private String categoria;
    private Long coordenadorId;
    private List<ProjetoParceiroRequest> parceirosId;
    private String status;
    private String descricao;
}
