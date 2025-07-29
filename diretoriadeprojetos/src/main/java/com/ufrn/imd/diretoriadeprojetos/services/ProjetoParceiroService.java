package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.models.ProjetoHasParceiro;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjetoParceiroRepository;

@Service
public class ProjetoParceiroService {
    @Autowired
    ProjetoParceiroRepository projetoParceiroRepository;

    public Optional<ProjetoHasParceiro> findByNumeroFunpec(long numeroFunpec) {
        return projetoParceiroRepository.findByNumeroFunpec(numeroFunpec);
    }

}
