package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectPartnerId;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectPartner {

    @EmbeddedId
    private ProjectPartnerId id;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumns({
            @JoinColumn(name = "sipac_number", referencedColumnName = "sipac_number"),
            @JoinColumn(name = "sipac_year", referencedColumnName = "sipac_year")
    })
    @JsonBackReference
    private Project project;

    @ManyToOne
    @MapsId("partnerId")
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @Column(unique = true)
    private Long funpecNumber;

    private Double contributionValue;

    @JsonManagedReference("project-scholarship-holders")
    @OneToMany(mappedBy = "projectPartner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectScholarshipHolder> scholarshipHolders = new ArrayList<>();

}