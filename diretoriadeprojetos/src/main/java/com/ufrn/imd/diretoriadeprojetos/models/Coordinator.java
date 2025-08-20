package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinator {

    @Id
    private Long siape;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "coordinator", cascade = CascadeType.ALL)
    private List<Project> projects;

    private String email;

    private String academicUnit;

    public Coordinator(Long siape, String name, String email, String academicUnit) {
        this.email = email;
        this.name = name;
        this.academicUnit = academicUnit;
        this.siape = siape;
    }

    public Coordinator(Long siape, String name) {
        this.siape = siape;
        this.name = name;
    }
}