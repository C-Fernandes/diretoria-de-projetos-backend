package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.models.ScholarshipHolder;
import com.ufrn.imd.diretoriadeprojetos.models.ProjectScholarshipHolder;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjectScholarshipHolderRepository;

@Service
public class ProjectScholarshipHolderService {

    @Autowired
    private ProjectScholarshipHolderRepository projetoHasBolsistaRepository;

    public List<ProjectScholarshipHolder> findByIdProjetoId(ProjectId projetoId) {
        // return projetoHasBolsistaRepository.findByIdProjetoId(projetoId);

        return null;
    }

    public void buscarPorNomeBolsista() {

    }

    public Optional<List<ProjectScholarshipHolder>> findBolsistaInProjeto(ScholarshipHolder bolsista,
            long numeroProjeto) {
        return projetoHasBolsistaRepository.findByScholarshipHolderAndProjectPartnerFunpecNumber(bolsista,
                numeroProjeto);
    }

    public ProjectScholarshipHolder salvar(ProjectScholarshipHolder projetoHasBolsista) {
        return projetoHasBolsistaRepository.save(projetoHasBolsista);
    }

    public void deletar(ProjectScholarshipHolder vinculoPosterior) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletar'");
    }

}
