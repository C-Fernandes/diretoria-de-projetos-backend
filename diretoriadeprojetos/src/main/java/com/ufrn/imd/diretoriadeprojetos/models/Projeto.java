package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Projeto {
    @EmbeddedId
    private ProjetoId id;
    private String nFunpec;
    @Column(nullable = false)

    private String titulo;
    @Column(nullable = false)
    private Boolean sebrae, embrapii, leiDeInformatica;
    @Column(nullable = false)
    private double valor;
    @Column(nullable = false)
    private Date dataInicio;
    @ManyToOne
    @JoinColumn(name = "coordenador_matricula", nullable = false)
    private Coordenador coordenador;

    @ManyToOne
    @JoinColumn(name = "parceiro_id", nullable = false)
    private Parceiro parceiro;
    @Column(nullable = false)
    private Date dataFim;
    @Column(nullable = false)
    private String contaContrato;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjetoHasBolsista> bolsistas = new ArrayList<>();

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Aditivo> aditivos = new ArrayList<>();

}
