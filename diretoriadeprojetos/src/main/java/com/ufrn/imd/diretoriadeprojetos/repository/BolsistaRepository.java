package com.ufrn.imd.diretoriadeprojetos.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.Bolsista;

@Repository
public interface BolsistaRepository extends JpaRepository<Bolsista, UUID> {
    Optional<Bolsista> findByNome(String nome);

}
