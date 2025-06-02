package com.ufrn.imd.diretoriadeprojetos.controllers;

import java.util.List;

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

import com.ufrn.imd.diretoriadeprojetos.dtos.ProjetoDTO;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.services.CoordenadorService;
import com.ufrn.imd.diretoriadeprojetos.services.ParceiroService;
import com.ufrn.imd.diretoriadeprojetos.services.ProjetoService;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {
    @Autowired
    private ProjetoService projetoService;

    @GetMapping
    public List<Projeto> listarTodos() {
        return projetoService.listarTodos();
    }

    @GetMapping(params = "externo")
    public ResponseEntity<Object> buscarNaApi(
            @RequestParam String numeroSipac,
            @RequestParam String anoSipac) {
        return ResponseEntity.ok(projetoService.buscarNaApi(numeroSipac, anoSipac));
    }

    @GetMapping("/{nSipac}")
    public ResponseEntity<Projeto> buscarPorId(@PathVariable String nSipac) {
        return ResponseEntity.ok(projetoService.buscarPorId(nSipac));
    }

    @PostMapping
    public ResponseEntity<Projeto> criar(@RequestBody ProjetoDTO projetoDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(projetoService.salvar(projetoDTO));
    }

    @PutMapping("/{nSipac}")
    public ResponseEntity<Projeto> atualizar(@PathVariable String nSipac, @RequestBody Projeto projetoAtualizado) {
        return ResponseEntity.ok(projetoService.atualizar(nSipac, projetoAtualizado));
    }

    @DeleteMapping("/{nSipac}")
    public ResponseEntity<Void> deletar(@PathVariable String nSipac) {
        projetoService.deletar(nSipac);
        return ResponseEntity.noContent().build();
    }
}