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

import com.ufrn.imd.diretoriadeprojetos.models.Coordinator;
import com.ufrn.imd.diretoriadeprojetos.services.CoordinatorService;

@RestController
@RequestMapping("/coordenadores")
public class CoordinatorController {

    @Autowired
    private CoordinatorService coordenadorService;

    @PostMapping
    public ResponseEntity<Coordinator> criar(@RequestBody Coordinator coordenador) {
        Coordinator salvo = coordenadorService.save(coordenador);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Coordinator>> listarTodos() {
        List<Coordinator> coordenadores = coordenadorService.findAll();
        return ResponseEntity.ok(coordenadores);
    }

    @GetMapping("/{siape}")
    public ResponseEntity<Coordinator> buscarPorSiape(@PathVariable Long siape) {
        return coordenadorService.buscarPorSiape(siape)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{siape}")
    public ResponseEntity<Coordinator> atualizar(@PathVariable Long siape, @RequestBody Coordinator coordenador) {
        Coordinator atualizado = coordenadorService.update(siape, coordenador);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{siape}")
    public ResponseEntity<Void> deletar(@PathVariable Long siape) {
        coordenadorService.deletar(siape);
        return ResponseEntity.noContent().build();
    }
}