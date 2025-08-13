package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ufrn.imd.diretoriadeprojetos.enums.FundingType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String cnpj;

    @Column(unique = true)
    private Long participeId;

    private String name;

    @Enumerated(EnumType.STRING)
    private FundingType fundingType;

    @JsonIgnore
    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL)
    private List<ProjectPartner> projects = new ArrayList<>();

    public Partner(Long participeId, String name) {
        this.participeId = participeId;
        this.name = name;
    }
}