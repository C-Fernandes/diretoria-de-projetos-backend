package com.ufrn.imd.diretoriadeprojetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.ProjectProcess;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProcessId;

@Repository
public interface ProjectProcessRepository extends JpaRepository<ProjectProcess, ProcessId> {

}
