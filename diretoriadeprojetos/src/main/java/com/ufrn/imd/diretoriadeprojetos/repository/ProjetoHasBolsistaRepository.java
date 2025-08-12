package com.ufrn.imd.diretoriadeprojetos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.Bolsista;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoHasBolsista;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoHasBolsistaId;

@Repository
public interface ProjetoHasBolsistaRepository extends JpaRepository<ProjetoHasBolsista, ProjetoHasBolsistaId> {

    List<ProjetoHasBolsista> findById_ProjetoParceiroId_ProjetoId(ProjectId projetoId);

    Optional<List<ProjetoHasBolsista>> findByBolsistaAndProjetoParceiroNumeroFunpec(Bolsista bolsista,
            long numeroFunpec);

}