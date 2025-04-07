package com.ufrn.imd.diretoriadeprojetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;

@Repository
public interface CoordenadorRepository extends JpaRepository<Coordenador, String> {

}
