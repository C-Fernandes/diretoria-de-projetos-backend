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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ufrn.imd.diretoriadeprojetos.dtos.response.BolsistaResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Bolsista;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;
import com.ufrn.imd.diretoriadeprojetos.services.BolsistaService;

@RestController
@RequestMapping("/bolsistas")
public class BolsistaController {

    @Autowired
    private BolsistaService bolsistaService;

    @GetMapping
    public List<BolsistaResponse> listarTodos() {
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
        Bolsista atualizado = bolsistaService.atualizar(id, bolsista);
        return ResponseEntity.ok(atualizado);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        bolsistaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{numeroSipac}/{anoSipac}")
    public ResponseEntity<List<Bolsista>> findByProjeto(
            @PathVariable String numeroSipac,
            @PathVariable String anoSipac) {

        List<Bolsista> bolsistas = bolsistaService.findByProjeto(new ProjetoId(numeroSipac, anoSipac));
        return ResponseEntity.ok(bolsistas);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            bolsistaService.processarCsv(file);
            return ResponseEntity.ok("CSV importado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao processar CSV: " + e.getMessage());
        }
    }
}