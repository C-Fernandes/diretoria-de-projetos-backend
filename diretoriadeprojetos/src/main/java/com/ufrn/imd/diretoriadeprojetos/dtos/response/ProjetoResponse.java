package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoResponse {

    private String numeroSipac;
    private String anoSipac;
    private String titulo;
    private Boolean leiDeInformatica;
    private double valor;
    private Date dataInicio;
    private Date dataFim;
    private String contaContrato;
    private String status;
    private String descricao;
    private Coordenador coordenador;
    private List<UUID> parceirosId;
}
