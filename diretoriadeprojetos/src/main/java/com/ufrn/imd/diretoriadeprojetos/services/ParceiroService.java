package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.models.Parceiro;
import com.ufrn.imd.diretoriadeprojetos.repository.ParceiroRepository;

@Service
public class ParceiroService {

    @Autowired
    private ParceiroRepository parceiroRepository;

    public List<Parceiro> findAll() {
        return parceiroRepository.findAll();
    }

    public Optional<Parceiro> findById(String cnpj) {
        return parceiroRepository.findById(cnpj);
    }

    public Parceiro save(Parceiro parceiro) {
        return parceiroRepository.save(parceiro);
    }

    public Parceiro update(String cnpj, Parceiro parceiroAtualizado) {
        return parceiroRepository.findById(cnpj).map(parceiro -> {
            parceiro.setNome(parceiroAtualizado.getNome());
            parceiro.setTipoFinanciamento(parceiroAtualizado.getTipoFinanciamento());
            parceiro.setProjetos(parceiroAtualizado.getProjetos());
            return parceiroRepository.save(parceiro);
        }).orElseThrow(() -> new RuntimeException("Parceiro não encontrado"));
    }

    public void delete(String cnpj) {
        parceiroRepository.deleteById(cnpj);
    }
}
