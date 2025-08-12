package com.ufrn.imd.diretoriadeprojetos.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProcessId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProjectProcess {

    @EmbeddedId
    private ProcessId id;

    @Column(nullable = false)
    private String name;

    private ProjectId projectId;

    @Column
    private long externalProcessId;
    @Column(nullable = false)
    private Date startDate;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @ManyToOne
    @JoinColumn(name = "coordinator_id", nullable = false)
    private Coordinator coordinator;

    private Double amount;

    @Column(nullable = false)
    private String funder;

    private Boolean isActive;
    @JsonIgnore
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movement> movements = new ArrayList<>();

    public void addMovement(Movement movement) {
        this.movements.add(movement);
        movement.setProcess(this);
    }

    public void removeMovement(Movement movement) {
        this.movements.remove(movement);
        movement.setProcess(null);
    }
}