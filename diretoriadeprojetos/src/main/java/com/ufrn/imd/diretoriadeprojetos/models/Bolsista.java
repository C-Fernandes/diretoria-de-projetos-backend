package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.ArrayList;
import java.util.List;

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
public class Bolsista {
    @Id
    private String cpf;
    private String nome;
    private String curso;
    private String email;
    @OneToMany(mappedBy = "bolsista", cascade = CascadeType.ALL)
    private List<ProjetoHasBolsistas> projetoBolsistas = new ArrayList<>();
}