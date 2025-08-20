package com.ufrn.imd.diretoriadeprojetos.dtos.request;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectRequest {

    private long sipacNumber;
    private long sipacYear;
    private Long projectId;
    private Long coordinatorId;

    // Core Details
    private String title;
    private double value;
    private String contractAccount;
    private Date startDate;
    private Date endDate;
    private String status;
    private String category;
    private String description;

    // Boolean Flags
    private boolean isInformaticLaw;
    private boolean isSebrae;
    private boolean isEmbrapii;
    private boolean isResidency;

    // Associations
    private List<ProjetoParceiroRequest> partnerIds;
}