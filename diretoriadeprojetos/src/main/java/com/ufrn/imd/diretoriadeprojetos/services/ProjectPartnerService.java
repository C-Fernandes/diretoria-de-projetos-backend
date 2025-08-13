package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.models.ProjectPartner;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjectPartnerRepository;

@Service
public class ProjectPartnerService {
    @Autowired
    ProjectPartnerRepository projectPartnerRepository;

    public Optional<ProjectPartner> findByFunpecNumber(long numeroFunpec) {
        return projectPartnerRepository.findByFunpecNumber(numeroFunpec);
    }

}
