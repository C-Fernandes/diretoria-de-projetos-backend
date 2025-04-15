package com.ufrn.imd.diretoriadeprojetos.models;

import java.sql.Date;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoHasBolsistas {
    @EmbeddedId
    private ProjetoHasBolsistaId id = new ProjetoHasBolsistaId();

    @ManyToOne
    @MapsId("projetoId")
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @ManyToOne
    @MapsId("bolsistaId")
    @JoinColumn(name = "bolsista_id")
    private Bolsista bolsista;
    private int rubrica;
    private Double valorBolsa;
    private String cargo;
    private Date dataInicio;
    private Date dataFim;
    private int cHSemanal;
}
