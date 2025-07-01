package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.models.Bolsista;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoHasBolsista;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjetoHasBolsistaRepository;

@Service
public class ProjetoHasBolsistaService {

    @Autowired
    private ProjetoHasBolsistaRepository projetoHasBolsistaRepository;

    public List<ProjetoHasBolsista> findByIdProjetoId(ProjetoId projetoId) {
        // return projetoHasBolsistaRepository.findByIdProjetoId(projetoId);

        return null;
    }

    public void buscarPorNomeBolsista() {

    }

    public Optional<List<ProjetoHasBolsista>> findBolsistaInProjeto(Bolsista bolsista, String numeroProjeto) {
        return projetoHasBolsistaRepository.findByBolsistaAndProjetoParceiroNumeroFunpec(bolsista, numeroProjeto);
    }

    public ProjetoHasBolsista salvar(ProjetoHasBolsista projetoHasBolsista) {
        return projetoHasBolsistaRepository.save(projetoHasBolsista);
    }

    public void deletar(ProjetoHasBolsista vinculoPosterior) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletar'");
    }

}
