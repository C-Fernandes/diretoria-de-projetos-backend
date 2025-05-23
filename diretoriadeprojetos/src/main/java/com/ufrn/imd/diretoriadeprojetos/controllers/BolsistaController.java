package com.ufrn.imd.diretoriadeprojetos.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufrn.imd.diretoriadeprojetos.models.Bolsista;
import com.ufrn.imd.diretoriadeprojetos.services.BolsistaService;

@RestController
@RequestMapping("/bolsistas")
public class BolsistaController {

    @Autowired
    private BolsistaService bolsistaService;

    @GetMapping
    public List<Bolsista> listarTodos() {
        return bolsistaService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bolsista> buscarPorId(@PathVariable UUID id) {
        return bolsistaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Bolsista> criar(@RequestBody Bolsista bolsista) {
        Bolsista novo = bolsistaService.salvar(bolsista);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bolsista> atualizar(@PathVariable UUID id, @RequestBody Bolsista bolsista) {
        try {
            Bolsista atualizado = bolsistaService.atualizar(id, bolsista);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        bolsistaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}