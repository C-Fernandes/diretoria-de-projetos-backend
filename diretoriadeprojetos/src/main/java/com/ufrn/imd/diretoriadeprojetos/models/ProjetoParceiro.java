package com.ufrn.imd.diretoriadeprojetos.models;

import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoParceiroId;

import jakarta.persistence.Column;
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
public class ProjetoParceiro {

    @EmbeddedId
    private ProjetoParceiroId id = new ProjetoParceiroId();

    @ManyToOne
    @MapsId("projetoId")
    @JoinColumns({
            @JoinColumn(name = "numero_sipac", referencedColumnName = "numero_sipac"),
            @JoinColumn(name = "ano_sipac", referencedColumnName = "ano_sipac")
    })
    private Projeto projeto;

    @ManyToOne
    @MapsId("parceiroId")
    @JoinColumn(name = "parceiro_id")
    private Parceiro parceiro;

    @Column(unique = true)
    private String nFunpec;

    private Double valorContribuicao;
}