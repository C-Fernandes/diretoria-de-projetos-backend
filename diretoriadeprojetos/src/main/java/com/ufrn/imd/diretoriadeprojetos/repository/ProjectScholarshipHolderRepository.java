package com.ufrn.imd.diretoriadeprojetos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.ProjectScholarshipHolder;
import com.ufrn.imd.diretoriadeprojetos.models.ScholarshipHolder;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectScholarshipHolderId;

@Repository
public interface ProjectScholarshipHolderRepository
        extends JpaRepository<ProjectScholarshipHolder, ProjectScholarshipHolderId> {

    List<ProjectScholarshipHolder> findById_ProjectPartnerId_ProjectId(ProjectId projectId);

    Optional<List<ProjectScholarshipHolder>> findByScholarshipHolderAndProjectPartnerFunpecNumber(
            ScholarshipHolder scholarshipHolder, long funpecNumber);

}