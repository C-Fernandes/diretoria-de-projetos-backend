package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Bolsista {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = true, unique = true)
  private String cpf;

  private String nome;
  private String email;

  // Campos de estudante
  private String tipoSuperior;
  private String curso;

  // Campos de pesquisador
  private Boolean docente;
  private String formacao;

  @JsonManagedReference("bolsista-projetos") // Adicione um nome para a referência
  @OneToMany(mappedBy = "bolsista", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProjetoHasBolsista> projetos = new ArrayList<>();

  public Bolsista(String nomeMembro) {

    this.nome = nomeMembro;
  }

}
