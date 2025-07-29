package com.ufrn.imd.diretoriadeprojetos.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.http.MediaType;

import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.ProcessoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProcessoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Processo;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;
import com.ufrn.imd.diretoriadeprojetos.services.ProcessoService;
import com.ufrn.imd.diretoriadeprojetos.services.ProjetoService;

@RestController
@RequestMapping("/processos")
public class ProcessoController {
    @Autowired
    private ProcessoService processoService;

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private ProcessoMapper processoMapper;

    @GetMapping
    public List<ProcessoResponse> findAll() {
        return processoService.findAll().stream().map(processoMapper::toResponse).collect(Collectors.toList());
    }

    @GetMapping(params = "externo")
    public ResponseEntity<ProcessoResponse> buscarNaApi(
            @RequestParam long radical,
            @RequestParam long numProtocolo, @RequestParam long ano, @RequestParam long dv) {

        ProcessoResponse processo = processoService
                .buscarNaApi(radical, numProtocolo, ano, dv);
        System.out.println("Enviando no controller: " + processo.toString());
        return ResponseEntity.ok(processo);
    }

    @GetMapping("/{radical}/{numProtocolo}/{ano}/{dv}")
    public ResponseEntity<ProcessoResponse> findById(
            @PathVariable long radical,
            @PathVariable long numProtocolo, @PathVariable long ano, @PathVariable long dv) {

        return ResponseEntity
                .ok(processoMapper.toResponse(processoService.findById(radical, numProtocolo, ano, dv).get()));
    }

    @DeleteMapping("/{radical}/{numProtocolo}/{ano}/{dv}")
    public ResponseEntity<List<ProjetoResponse>> delete(
            @PathVariable long radical,
            @PathVariable long numProtocolo, @PathVariable long ano, @PathVariable long dv) {

        processoService.delete(radical, numProtocolo, ano, dv);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ProcessoResponse> criar(@RequestBody ProcessoRequest processo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(processoService.salvar(processo));
    }

    @PostMapping("/{numeroSipac}/{anoSipac}")
    public ResponseEntity<ProjetoResponse> criarProjetoPorProcesso(@PathVariable long numeroSipac,
            @PathVariable long anoSipac) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projetoService.salvarProjetoPorProcesso(numeroSipac, anoSipac));
    }

    @GetMapping("/atualizarTodos")
    public ResponseEntity<List<ProcessoResponse>> atualizarTodos() {
        return ResponseEntity.ok(
                processoService.atualizarTodos().stream().map(processoMapper::toResponse).collect(Collectors.toList()));
    }

}