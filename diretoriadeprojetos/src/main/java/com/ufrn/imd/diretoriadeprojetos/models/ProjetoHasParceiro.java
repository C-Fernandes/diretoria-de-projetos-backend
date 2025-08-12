package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoParceiroId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjetoHasParceiro {

    @EmbeddedId
    private ProjetoParceiroId id;

    @ManyToOne
    @MapsId("projetoId")
    @JoinColumns({
            @JoinColumn(name = "sipac_number", referencedColumnName = "sipac_number"),
            @JoinColumn(name = "sipac_year", referencedColumnName = "sipac_year")
    })
    @JsonBackReference
    private Projeto projeto;

    @ManyToOne
    @MapsId("parceiroId")
    @JoinColumn(name = "parceiro_id")
    private Parceiro parceiro;

    @Column(unique = true)
    private Long numeroFunpec;

    private Double valorContribuicao;

    @JsonManagedReference("projeto-bolsistas")
    @OneToMany(mappedBy = "projetoParceiro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjetoHasBolsista> bolsistas = new ArrayList<>();

}