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
public class Project {
    @EmbeddedId
    private ProjectId id;

    @Column(unique = true)
    private Long externalProjectId = null;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String title;

    @Column(nullable = false)
    private Boolean isResidency = false, isInformaticLaw = false, isEmbrapii = false, isSebrae = false;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private Date startDate;

    @ManyToOne
    @JoinColumn(name = "coordinator_siape", nullable = false) // Coluna de junção renomeada
    private Coordinator coordinator;

    @JsonManagedReference
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectPartner> partners = new ArrayList<>(); // Variável renomeada

    @Column(nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private String contractAccount;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Aditivo> aditivos = new ArrayList<>();

    private String status;

    private String category;

    public Project() {
        this.id = new ProjectId();
    }

    public Project(long numeroSipac, long anoSipac) {
        this.id = new ProjectId(numeroSipac, anoSipac);
    }
}
