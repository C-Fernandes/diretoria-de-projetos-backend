package com.ufrn.imd.diretoriadeprojetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.imd.diretoriadeprojetos.models.Project;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;

@Repository
public interface ProjectRepository extends JpaRepository<Project, ProjectId> {

}
