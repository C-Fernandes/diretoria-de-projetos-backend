package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectScholarshipHolderId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectScholarshipHolder {

    @EmbeddedId
    private ProjectScholarshipHolderId id;

    @ManyToOne
    @MapsId("projectPartnerId")
    @JsonBackReference("project-scholarship-holders")
    @JoinColumns({
            @JoinColumn(name = "sipac_number", referencedColumnName = "sipac_number"),
            @JoinColumn(name = "sipac_year", referencedColumnName = "sipac_year"),
            @JoinColumn(name = "partner_id", referencedColumnName = "partner_id")
    })
    private ProjectPartner projectPartner;

    @ManyToOne
    @JsonBackReference("scholarship-holder-projects")
    @MapsId("scholarshipHolderUuid")
    @JoinColumn(name = "scholarship_holder_uuid")
    private ScholarshipHolder scholarshipHolder; // "bolsista" -> "scholarshipHolder"

    private int rubrica; // "rubrica" -> "fundingCode"

    private Double value; // "valor" -> "value"

    private Date startDate; // "dataInicio" -> "startDate"

    private Date endDate; // "dataFim" -> "endDate"

    private Integer ch; // "cHSemanal" -> "weeklyHours"

    public ProjectScholarshipHolder(ProjectScholarshipHolderId projectScholarshipHolderId, String fundingCodeNumber,
            Double value,
            Date startDate, Date endDate, int weeklyHours) {
        this.id = projectScholarshipHolderId;
        this.rubrica = Integer.parseInt(fundingCodeNumber);
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ch = weeklyHours;
    }
}