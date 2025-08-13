package com.ufrn.imd.diretoriadeprojetos.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.Partner;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, UUID> {
    Optional<Partner> findByName(String nomeParticipe);

    Optional<Partner> findByParticipeId(Long idParticipe);

}
