package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.models.Bolsista;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoHasBolsista;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;
import com.ufrn.imd.diretoriadeprojetos.repository.BolsistaRepository;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjetoHasBolsistaRepository;

@Service
public class BolsistaService {

    @Autowired
    private BolsistaRepository bolsistaRepository;

    @Autowired
    private ProjetoHasBolsistaRepository projetoHasBolsistaRepository;

    public List<Bolsista> listarTodos() {
        return bolsistaRepository.findAll();
    }

    public Optional<Bolsista> buscarPorId(UUID id) {
        return bolsistaRepository.findById(id);
    }

    public Bolsista salvar(Bolsista bolsista) {
        return bolsistaRepository.save(bolsista);
    }

    public Bolsista atualizar(UUID id, Bolsista novoBolsista) {
        return bolsistaRepository.findById(id).map(b -> {
            b.setCpf(novoBolsista.getCpf());
            b.setNome(novoBolsista.getNome());
            b.setEmail(novoBolsista.getEmail());
            b.setRubrica(novoBolsista.getRubrica());
            b.setTipoSuperior(novoBolsista.getTipoSuperior());
            b.setCurso(novoBolsista.getCurso());
            b.setDocente(novoBolsista.getDocente());
            b.setFormacao(novoBolsista.getFormacao());
            return bolsistaRepository.save(b);
        }).orElseThrow(() -> new RuntimeException("Bolsista não encontrado"));
    }

    public void deletar(UUID id) {
        bolsistaRepository.deleteById(id);
    }

    public List<Bolsista> findByProjeto(ProjetoId projetoId) {
        // 1. Busca todas as associações ProjetoHasBolsista para o projetoId
        List<ProjetoHasBolsista> associacoes = projetoHasBolsistaRepository.findByIdProjetoId(projetoId);

        // 2. Mapeia cada associação para o objeto Bolsista correspondente
        return associacoes.stream()
                .map(ProjetoHasBolsista::getBolsista) // Pega o objeto Bolsista de cada associação
                .collect(Collectors.toList()); // Coleta em uma nova lista
    }
}