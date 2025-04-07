package com.ufrn.imd.diretoriadeprojetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.Projeto;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, String> {

}
