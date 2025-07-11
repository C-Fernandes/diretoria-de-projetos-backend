package com.ufrn.imd.diretoriadeprojetos.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Parceiro;
import com.ufrn.imd.diretoriadeprojetos.services.ParceiroService;

@RestController
@RequestMapping("/parceiros")
public class ParceiroController {
    @Autowired
    private ParceiroService parceiroService;

    @GetMapping
    public List<ParceiroResponse> listar() {
        return parceiroService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parceiro> buscarPorId(@PathVariable UUID id) {
        return parceiroService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Parceiro> create(@RequestBody Parceiro parceiro) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parceiroService.save(parceiro));
    }
    /*
     * @PutMapping("/{id}")
     * public ResponseEntity<Parceiro> update(@PathVariable UUID id, @RequestBody
     * Parceiro parceiro) {
     * 
     * return ResponseEntity.ok(parceiroService.update(id, parceiro));
     * 
     * }
     * 
     * @DeleteMapping("/{id}")
     * public ResponseEntity<Void> delete(@PathVariable UUID id) {
     * parceiroService.delete(id);
     * return ResponseEntity.noContent().build();
     * }
     */
}
