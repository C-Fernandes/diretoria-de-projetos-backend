package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.ufrn.imd.diretoriadeprojetos.enums.FundingType;
import com.ufrn.imd.diretoriadeprojetos.models.Coordinator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoResponse {

    private long numeroSipac;
    private long anoSipac;
    private String titulo;
    private Boolean leiDeInformatica, embrapii, sebrae, residencia;
    private double valor;
    private Date dataInicio;
    private Date dataFim;
    private String contaContrato;
    private String status;
    private Long idProjeto;
    private String descricao;
    private FundingType tipoFinanciamento;
    private Coordinator coordenador;
    private List<UUID> parceirosId;

    private String categoria;
}
