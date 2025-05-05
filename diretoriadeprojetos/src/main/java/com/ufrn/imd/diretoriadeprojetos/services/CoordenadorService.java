package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.repository.CoordenadorRepository;

@Service
public class CoordenadorService {
    @Autowired
    private CoordenadorRepository coordenadorRepository;

    public Coordenador salvar(Coordenador coordenador) {
        return coordenadorRepository.save(coordenador);
    }

    public List<Coordenador> listarTodos() {
        return coordenadorRepository.findAll();
    }

    public Optional<Coordenador> buscarPorMatricula(String matricula) {
        return coordenadorRepository.findById(matricula);
    }

    public Coordenador atualizar(String matricula, Coordenador coordenadorAtualizado) {
        return coordenadorRepository.findById(matricula).map(coordenador -> {
            coordenador.setNome(coordenadorAtualizado.getNome());
            coordenador.setEmail(coordenadorAtualizado.getEmail());
            coordenador.setUnidadeAcademica(coordenadorAtualizado.getUnidadeAcademica());
            coordenador.setProjetos(coordenadorAtualizado.getProjetos());
            return coordenadorRepository.save(coordenador);
        }).orElseThrow(() -> new RuntimeException("Coordenador não encontrado"));
    }

    public void deletar(String matricula) {
        coordenadorRepository.deleteById(matricula);
    }
}