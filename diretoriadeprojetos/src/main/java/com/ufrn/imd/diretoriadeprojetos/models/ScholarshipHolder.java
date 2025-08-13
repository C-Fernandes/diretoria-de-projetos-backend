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
public class ScholarshipHolder { // "Bolsista" -> "ScholarshipHolder"

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = true, unique = true)
  private String cpf;

  private String name;
  private String email;

  private String educationLevel; // "tipoSuperior" -> "educationLevel" (e.g., Undergraduate, PhD)
  private String course; // "curso" -> "course"

  // Researcher fields
  private Boolean isTeacher; // "docente" -> "isFaculty"
  private String degree; // "formacao" -> "degree" or "fieldOfStudy"

  @JsonManagedReference("scholarship-holder-projects") // "bolsista-projetos" -> "scholarship-holder-projects"
  @OneToMany(mappedBy = "scholarshipHolder", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProjectScholarshipHolder> projects = new ArrayList<>(); // "projetos" -> "projects"

  public ScholarshipHolder(String memberName) { // "nomeMembro" -> "memberName"
    this.name = memberName;
  }
}