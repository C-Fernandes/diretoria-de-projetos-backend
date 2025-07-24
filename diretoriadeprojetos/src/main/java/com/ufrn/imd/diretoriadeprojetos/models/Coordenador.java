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
public class Coordenador {

    @Id
    private Long siape;

    private String nome;
    @JsonIgnore
    @OneToMany(mappedBy = "coordenador", cascade = CascadeType.ALL)
    private List<Projeto> projetos;
    private String email;
    private String UnidadeAcademica;

    public Coordenador(Long idDocente, String nome, String email, String unidade) {
        this.email = email;
        this.nome = nome;
        this.UnidadeAcademica = unidade;
        this.siape = idDocente;
    }

    public Coordenador(Long siapeCoordenador, String nomeCoordenador) {
        this.siape = siapeCoordenador;
        this.nome = nomeCoordenador;
    }
}