package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class Coordenador {
    @Id
    @JsonProperty("matricula")
    private String matricula;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("unidadeAcademica")
    private String unidadeAcademica;

    @OneToMany(mappedBy = "coordenador", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Projeto> projetos;
}