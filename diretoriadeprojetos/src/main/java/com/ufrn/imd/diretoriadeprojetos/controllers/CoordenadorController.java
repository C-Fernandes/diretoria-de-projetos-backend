package com.ufrn.imd.diretoriadeprojetos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.services.CoordenadorService;

@RestController
@RequestMapping("/coordenadores")
public class CoordenadorController {

    @Autowired
    private CoordenadorService coordenadorService;

    @PostMapping
    public ResponseEntity<Coordenador> criar(@RequestBody Coordenador coordenador) {
        Coordenador salvo = coordenadorService.save(coordenador);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Coordenador>> listarTodos() {
        List<Coordenador> coordenadores = coordenadorService.findAll();
        return ResponseEntity.ok(coordenadores);
    }

    @GetMapping("/{matricula}")
    public ResponseEntity<Coordenador> buscarPorMatricula(@PathVariable Long matricula) {
        return coordenadorService.buscarPorMatricula(matricula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{matricula}")
    public ResponseEntity<Coordenador> atualizar(@PathVariable Long matricula, @RequestBody Coordenador coordenador) {
        Coordenador atualizado = coordenadorService.update(matricula, coordenador);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{matricula}")
    public ResponseEntity<Void> deletar(@PathVariable Long matricula) {
        coordenadorService.deletar(matricula);
        return ResponseEntity.noContent().build();
    }
}