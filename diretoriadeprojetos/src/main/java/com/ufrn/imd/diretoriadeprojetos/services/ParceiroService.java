package com.ufrn.imd.diretoriadeprojetos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.repository.ParceiroRepository;

@Service
public class ParceiroService {
    @Autowired
    private ParceiroRepository parceiroRepository;
}
