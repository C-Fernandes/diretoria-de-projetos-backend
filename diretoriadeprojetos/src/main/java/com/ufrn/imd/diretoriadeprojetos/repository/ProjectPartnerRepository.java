package com.ufrn.imd.diretoriadeprojetos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufrn.imd.diretoriadeprojetos.models.ProjectPartner;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectPartnerId;

public interface ProjectPartnerRepository extends JpaRepository<ProjectPartner, ProjectPartnerId> {
    Optional<ProjectPartner> findByFunpecNumber(long funpecNumber);
}
