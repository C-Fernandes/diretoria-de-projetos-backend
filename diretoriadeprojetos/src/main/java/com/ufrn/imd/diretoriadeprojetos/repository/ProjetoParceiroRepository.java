package com.ufrn.imd.diretoriadeprojetos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufrn.imd.diretoriadeprojetos.models.ProjetoParceiro;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoParceiroId;

public interface ProjetoParceiroRepository extends JpaRepository<ProjetoParceiro, ProjetoParceiroId> {
    Optional<ProjetoParceiro> findByNumeroFunpec(String numeroFunpec);
}
