package com.ufrn.imd.diretoriadeprojetos.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.ScholarshipHolder;

@Repository
public interface ScholarshipHolderRepository extends JpaRepository<ScholarshipHolder, UUID> {
    Optional<ScholarshipHolder> findByName(String nome);

}
