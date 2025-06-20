package com.ufrn.imd.diretoriadeprojetos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.ProjetoHasBolsista;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoHasBolsistaId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;

@Repository
public interface ProjetoHasBolsistaRepository extends JpaRepository<ProjetoHasBolsista, ProjetoHasBolsistaId> {

    List<ProjetoHasBolsista> findByIdProjetoId(ProjetoId projetoId);
}