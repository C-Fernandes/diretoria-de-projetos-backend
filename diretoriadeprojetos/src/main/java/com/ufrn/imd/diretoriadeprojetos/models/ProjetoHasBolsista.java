package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoHasBolsistaId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
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
public class ProjetoHasBolsista {

    @EmbeddedId
    private ProjetoHasBolsistaId id;

    @ManyToOne
    @MapsId("projetoParceiroId")
    @JsonBackReference("projeto-bolsistas")
    @JoinColumns({
            @JoinColumn(name = "sipac_number", referencedColumnName = "sipac_number"),
            @JoinColumn(name = "sipac_year", referencedColumnName = "sipac_year"),
            @JoinColumn(name = "parceiro_id", referencedColumnName = "parceiro_id")
    })
    private ProjetoHasParceiro projetoParceiro;
    @ManyToOne
    @JsonBackReference("bolsista-projetos")
    @MapsId("bolsistaUuid")
    @JoinColumn(name = "bolsista_uuid")
    private Bolsista bolsista;
    private int rubrica;
    private Double valor;
    private Date dataInicio;
    private Date dataFim;
    private Integer cHSemanal;

    public ProjetoHasBolsista(ProjetoHasBolsistaId projetoHasBolsistaId, String numeroRubrica, Double valor,
            Date dataInicio, Date dataFim, int cargaHoraria) {
        this.id = projetoHasBolsistaId;
        this.rubrica = Integer.parseInt(numeroRubrica);
        this.valor = valor;

        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.cHSemanal = cargaHoraria;
    }
}