package com.ufrn.imd.diretoriadeprojetos.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

import java.util.Date;

@Entity
public class ProjetoHasBolsista {
    @EmbeddedId
    private ProjetoHasBolsistaId id;

    @ManyToOne
    @MapsId("nSipac")
    @JoinColumn(name = "n_sipac")
    private Projeto projeto;

    @ManyToOne
    @MapsId("bolsistaUuid")
    @JoinColumn(name = "bolsista_uuid")
    private Bolsista bolsista;

    private Double valor;
    private Date dataInicio;
    private Date dataFim;
    private Integer cHSemanal;
}
