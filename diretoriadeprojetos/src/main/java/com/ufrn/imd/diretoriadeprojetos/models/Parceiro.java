package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ufrn.imd.diretoriadeprojetos.enums.TipoFinanciamento;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Parceiro {
    @Id
    private String cnpj;

    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoFinanciamento tipoFinanciamento;
    @JsonIgnore
    @OneToMany(mappedBy = "parceiro", cascade = CascadeType.ALL)
    private List<Projeto> projetos;
}