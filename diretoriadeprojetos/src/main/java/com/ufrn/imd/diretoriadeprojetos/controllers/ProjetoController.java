package com.ufrn.imd.diretoriadeprojetos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;
import com.ufrn.imd.diretoriadeprojetos.services.ProjetoService;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {
    @Autowired
    private ProjetoService projetoService;

    @GetMapping
    public List<ProjetoResponse> findAll() {
        return projetoService.findAll();
    }

    @GetMapping(params = "externo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjetoResponse> buscarNaApi(
            @RequestParam long numeroSipac,
            @RequestParam long anoSipac) {

        ProjetoResponse fluxo = projetoService
                .buscarNaApi(numeroSipac, anoSipac);

        return ResponseEntity.ok(fluxo);
    }

    @DeleteMapping("/{numeroSipac}/{anoSipac}")
    public ResponseEntity<List<ProjetoResponse>> delete(
            @PathVariable long numeroSipac,
            @PathVariable long anoSipac) {

        projetoService.delete(numeroSipac, anoSipac);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Projeto> criar(@RequestBody ProjetoRequest projeto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoService.salvar(projeto));
    }

    @GetMapping("/{numeroSipac}/{anoSipac}")
    public ResponseEntity<ProjetoResponse> findById(
            @PathVariable long numeroSipac,
            @PathVariable long anoSipac) {
        ProjetoResponse projeto = projetoService.findById(new ProjectId(numeroSipac, anoSipac));
        return ResponseEntity.ok(projeto);
    }

}