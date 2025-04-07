package com.ufrn.imd.diretoriadeprojetos.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
}