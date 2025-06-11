package com.ufrn.imd.diretoriadeprojetos.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.Parceiro;

@Repository
public interface ParceiroRepository extends JpaRepository<Parceiro, UUID> {

    Optional<Parceiro> findByNome(String nomeParticipe);

    Optional<Parceiro> findByIdParticipe(Long idParticipe);

}
