package com.ufrn.imd.diretoriadeprojetos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufrn.imd.diretoriadeprojetos.models.ProjetoHasParceiro;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoParceiroId;

public interface ProjetoParceiroRepository extends JpaRepository<ProjetoHasParceiro, ProjetoParceiroId> {
    Optional<ProjetoHasParceiro> findByNumeroFunpec(long numeroFunpec);
}
