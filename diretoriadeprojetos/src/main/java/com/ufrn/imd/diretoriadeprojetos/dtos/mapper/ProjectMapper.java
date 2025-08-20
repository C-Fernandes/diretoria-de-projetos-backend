package com.ufrn.imd.diretoriadeprojetos.dtos.mapper;

import org.springframework.stereotype.Component;

import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjectRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoParceiroRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjectResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.enums.FundingType;
import com.ufrn.imd.diretoriadeprojetos.models.Coordinator;
import com.ufrn.imd.diretoriadeprojetos.models.Partner;
import com.ufrn.imd.diretoriadeprojetos.models.Project;
import com.ufrn.imd.diretoriadeprojetos.models.ProjectPartner;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    /**
     * Converts a Project entity to a ProjectResponse DTO.
     */
    public ProjectResponse toResponse(Project project) {
        if (project == null) {
            return null;
        }

        ProjectResponse response = new ProjectResponse();
        response.setSipacNumber(project.getId().getSipacNumber());
        response.setSipacYear(project.getId().getSipacYear());
        response.setTitle(project.getTitle());
        response.setIsInformaticLaw(project.getIsInformaticLaw());
        response.setIsEmbrapii(project.getIsEmbrapii());
        response.setIsResidency(project.getIsResidency());
        response.setIsSebrae(project.getIsSebrae());
        response.setValue(project.getValue());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setContractAccount(project.getContractAccount());
        response.setStatus(project.getStatus());
        response.setDescription(project.getDescription());
        response.setCoordinator(project.getCoordinator());
        response.setCategory(project.getCategory());
        response.setExternalProjectId(project.getExternalProjectId());
        response.setFundingType(findFundingType(project));

        if (project.getPartners() != null) {
            List<UUID> partnerIds = project.getPartners().stream()
                    .map(projectPartner -> projectPartner.getPartner().getId())
                    .collect(Collectors.toList());
            response.setPartnerIds(partnerIds);
        }

        return response;
    }

    /**
     * Converts a ProjectRequest DTO to a Project entity.
     */
    public Project toEntity(ProjectRequest projectDto, Coordinator coordinator) {
        if (projectDto == null) {
            return null;
        }

        Project project = new Project();
        ProjectId projectId = new ProjectId(projectDto.getSipacNumber(), projectDto.getSipacYear());

        project.setId(projectId);
        project.setTitle(projectDto.getTitle());
        project.setIsInformaticLaw(projectDto.isInformaticLaw());
        project.setIsResidency(projectDto.isResidency());
        project.setIsSebrae(projectDto.isSebrae());
        project.setIsEmbrapii(projectDto.isEmbrapii());
        project.setCategory(projectDto.getCategory());
        project.setValue(projectDto.getValue());
        project.setContractAccount(projectDto.getContractAccount());
        project.setStartDate(projectDto.getStartDate());
        project.setEndDate(projectDto.getEndDate());
        project.setStatus(projectDto.getStatus());
        project.setDescription(projectDto.getDescription());
        project.setCoordinator(coordinator);
        project.setExternalProjectId(projectDto.getProjectId());
        return project;
    }

    public ProjectResponse toResponse(ProjetoApiResponse projectApi, Coordinator coordinator,
            List<Partner> partners) {
        if (projectApi == null) {
            return null;
        }

        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setContractAccount(projectApi.getNumeroFormatado());
        projectResponse.setCoordinator(coordinator);
        projectResponse.setSipacNumber(projectApi.getNumero());
        projectResponse.setSipacYear(projectApi.getAno());
        projectResponse.setDescription(projectApi.getJustificativa());
        projectResponse.setTitle(projectApi.getTituloProjeto());
        projectResponse.setEndDate(projectApi.getFimExecucao());
        projectResponse.setStartDate(projectApi.getInicioExecucao());
        projectResponse.setStatus(projectApi.getDescricaoStatus());
        projectResponse.setValue(projectApi.getValorProjeto());
        projectResponse.setExternalProjectId(projectApi.getIdProjeto());

        String projectType = projectApi.getTipoProjeto();
        String agreementType = projectApi.getAreaConhecimento();

        String newCategory;
        boolean isProjectTypeValid = projectType != null && !projectType.isBlank();
        boolean isAgreementTypeValid = agreementType != null && !agreementType.isBlank();

        if (isProjectTypeValid && isAgreementTypeValid) {
            newCategory = String.format("%s %s", projectType, agreementType);
        } else if (isProjectTypeValid) {
            newCategory = projectType;
        } else if (isAgreementTypeValid) {
            newCategory = agreementType;
        } else {
            newCategory = "Category not defined";
        }

        projectResponse.setCategory(newCategory);

        if (partners != null) {
            List<UUID> partnerIdsList = partners.stream()
                    .map(Partner::getId)
                    .collect(Collectors.toList());
            projectResponse.setPartnerIds(partnerIdsList);
        }

        return projectResponse;
    }

    public ProjectRequest toRequest(ProjectResponse response) {
        if (response == null) {
            return null;
        }

        ProjectRequest request = new ProjectRequest();
        request.setSipacNumber(response.getSipacNumber());
        request.setSipacYear(response.getSipacYear());
        request.setTitle(response.getTitle());
        request.setValue(response.getValue());
        request.setContractAccount(response.getContractAccount());
        request.setStartDate(response.getStartDate());
        request.setEndDate(response.getEndDate());
        request.setInformaticLaw(Boolean.TRUE.equals(response.getIsInformaticLaw()));
        request.setSebrae(Boolean.TRUE.equals(response.getIsSebrae()));
        request.setEmbrapii(Boolean.TRUE.equals(response.getIsEmbrapii()));
        request.setResidency(Boolean.TRUE.equals(response.getIsResidency()));
        request.setCategory(response.getCategory());
        request.setStatus(response.getStatus());
        request.setDescription(response.getDescription());
        request.setProjectId(response.getExternalProjectId());

        if (response.getCoordinator() != null) {
            request.setCoordinatorId(response.getCoordinator().getSiape());
        }

        if (response.getPartnerIds() != null && !response.getPartnerIds().isEmpty()) {
            List<ProjetoParceiroRequest> partnerRequests = response.getPartnerIds().stream()
                    .map(uuid -> {
                        ProjetoParceiroRequest ppr = new ProjetoParceiroRequest();
                        ppr.setParceiroId(uuid); // Correct method to set the partner ID
                        return ppr;
                    })
                    .collect(Collectors.toList());
            request.setPartnerIds(partnerRequests); // Assuming the setter method is named setParceiros
        }

        return request;
    }

    public FundingType findFundingType(Project project) {
        List<String> priorityOrder = Arrays.asList("EMPRESA", "SEBRAE", "UFRN", "EMBRAPII", "FUNPEC");
        Map<String, Partner> partnersByType = new HashMap<>();

        for (ProjectPartner projectPartner : project.getPartners()) {
            Partner partner = projectPartner.getPartner();
            String partnerName = partner.getName().toUpperCase();

            if (partnerName.contains("FUNPEC")) {
                partnersByType.put("FUNPEC", partner);
            } else if (partnerName.contains("EMBRAPII")) {
                partnersByType.put("EMBRAPII", partner);
            } else if (partnerName.contains("UFRN")) {
                partnersByType.put("UFRN", partner);
            } else if (partnerName.contains("SEBRAE")) {
                partnersByType.put("SEBRAE", partner);
            } else {
                partnersByType.put("EMPRESA", partner);
            }
        }

        FundingType fundingType = null;
        for (String type : priorityOrder) {
            if (partnersByType.containsKey(type)) {
                fundingType = partnersByType.get(type).getFundingType();
                break;
            }
        }

        return fundingType;
    }
}