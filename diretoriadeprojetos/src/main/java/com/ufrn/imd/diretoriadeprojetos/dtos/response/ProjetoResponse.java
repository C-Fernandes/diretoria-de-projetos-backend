package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoParceiroRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProjetoResponse {

    private Long idProjeto;
    private String titulo;
    private Boolean leiDeInformatica;
    private double valor;
    private Date dataInicio;
    private Date dataFim;
    private String contaContrato;
    private String status;
    private String descricao;
    private Long coordenadorId;
    private List<UUID> parceirosId;
}
