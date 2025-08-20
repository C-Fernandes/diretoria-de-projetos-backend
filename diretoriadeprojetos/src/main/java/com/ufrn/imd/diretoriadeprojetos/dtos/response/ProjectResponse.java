package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.ufrn.imd.diretoriadeprojetos.enums.FundingType;
import com.ufrn.imd.diretoriadeprojetos.models.Coordinator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private long sipacNumber;
    private long sipacYear;
    private String title;
    private Boolean isInformaticLaw, isEmbrapii, isSebrae, isResidency;
    private double value;
    private Date startDate;
    private Date endDate;
    private String contractAccount;
    private String status;
    private Long externalProjectId;
    private String description;
    private FundingType fundingType;
    private Coordinator coordinator;
    private List<UUID> partnerIds;
    private String category;
}