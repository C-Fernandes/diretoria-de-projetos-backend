package com.ufrn.imd.diretoriadeprojetos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufrn.imd.diretoriadeprojetos.services.CoordenadorService;

@RestController
@RequestMapping("/coordenadores")
public class CoordenadorController {
    @Autowired
    private CoordenadorService coordenadorService;
}
