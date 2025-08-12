package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Projeto {
    @EmbeddedId
    private ProjectId id;

    @Column(unique = true)
    private Long idProjeto = null;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    private String titulo;
    @Column(nullable = false)
    private Boolean residencia = false, leiDeInformatica = false, embrapii = false, sebrae = false;
    @Column(nullable = false)
    private double valor;
    @Column(nullable = false)
    private Date dataInicio;
    @ManyToOne
    @JoinColumn(name = "coordenador_matricula", nullable = false)
    private Coordinator coordenador;
    @JsonManagedReference
    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjetoHasParceiro> parceiros = new ArrayList<>();

    @Column(nullable = false)
    private Date dataFim;
    @Column(nullable = false)
    private String contaContrato;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Aditivo> aditivos = new ArrayList<>();
    private String status;

    private String categoria;

    public Projeto() {
        this.id = new ProjectId();
    }

    public Projeto(long numeroSipac, long anoSipac) {
        this.id = new ProjectId(numeroSipac, anoSipac);
    }
}
