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

    // CREATE
    public Coordenador save(Coordenador coordenador) {
        try {
            return coordenadorRepository.save(coordenador);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar coordenador: " + e.getMessage(), e);
        }
    }

    // READ ALL
    public List<Coordenador> findAll() {
        try {
            return coordenadorRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar coordenadores: " + e.getMessage(), e);
        }
    }

    // READ BY ID
    public Optional<Coordenador> findById(String matricula) {
        try {
            return coordenadorRepository.findById(matricula);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar coordenador com matricula " + matricula + ": " + e.getMessage(),
                    e);
        }
    }

    // DELETE
    public void deleteById(String matricula) {
        try {
            if (!coordenadorRepository.existsById(matricula)) {
                throw new RuntimeException("Coordenador com matricula " + matricula + " não encontrado");
            }
            coordenadorRepository.deleteById(matricula);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar coordenador com matricula " + matricula + ": " + e.getMessage(),
                    e);
        }
    }
}
