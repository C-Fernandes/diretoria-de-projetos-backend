package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.Date;

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

    // 1) Associação com Projeto (ID composto)
    @ManyToOne
    @MapsId("projetoId")
    @JoinColumns({
            @JoinColumn(name = "numero_sipac", referencedColumnName = "numero_sipac"),
            @JoinColumn(name = "ano_sipac", referencedColumnName = "ano_sipac")
    })
    private Projeto projeto;
    @ManyToOne
    @MapsId("bolsistaUuid")
    @JoinColumn(name = "bolsista_uuid")
    private Bolsista bolsista;
    private int rubrica;
    private Double valor;
    private Date dataInicio;
    private Date dataFim;
    private Integer cHSemanal;

}