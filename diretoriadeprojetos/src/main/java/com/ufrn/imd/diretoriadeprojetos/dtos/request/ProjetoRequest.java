package com.ufrn.imd.diretoriadeprojetos.dtos.request;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoRequest {

    private String idProjeto;
    private String nFunpec;
    private String titulo;
    private Boolean sebrae;
    private Boolean embrapii;
    private Boolean leiDeInformatica;
    private double valor;
    private Date dataInicio;
    private Date dataFim;
    private String contaContrato;
    private String status;

    private String coordenadorMatricula;
    private List<ProjetoParceiroRequest> parceiros;
}
