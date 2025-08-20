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
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjectRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjectResponse;
import com.ufrn.imd.diretoriadeprojetos.models.ProjectProcess;
import com.ufrn.imd.diretoriadeprojetos.models.Project;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;
import com.ufrn.imd.diretoriadeprojetos.services.ProcessService;
import com.ufrn.imd.diretoriadeprojetos.services.ProjectService;

@RestController
@RequestMapping("/processos")
public class ProcessoController {
    @Autowired
    private ProcessService processoService;

    @Autowired
    private ProjectService projectService;

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

        ProjectProcess processo = processoService
                .fetchFromApi(radical, numProtocolo, ano, dv);

        return ResponseEntity.ok(processoMapper.toResponse(processo));
    }

    @GetMapping("/{radical}/{numProtocolo}/{ano}/{dv}")
    public ResponseEntity<ProcessoResponse> findById(
            @PathVariable long radical,
            @PathVariable long numProtocolo, @PathVariable long ano, @PathVariable long dv) {

        return ResponseEntity
                .ok(processoMapper.toResponse(processoService.findById(radical, numProtocolo, ano, dv).get()));
    }

    @DeleteMapping("/{radical}/{numProtocolo}/{ano}/{dv}")
    public ResponseEntity<List<ProjectResponse>> delete(
            @PathVariable long radical,
            @PathVariable long numProtocolo, @PathVariable long ano, @PathVariable long dv) {

        processoService.delete(radical, numProtocolo, ano, dv);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ProcessoResponse> criar(@RequestBody ProcessoRequest processo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(processoMapper.toResponse(processoService.save(processo)));
    }

    @PostMapping("/{numeroSipac}/{anoSipac}")
    public ResponseEntity<ProjectResponse> criarProjetoPorProcesso(@PathVariable long numeroSipac,
            @PathVariable long anoSipac) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.salvarProjetoPorProcesso(numeroSipac, anoSipac));
    }

    @GetMapping("/atualizarTodos")
    public ResponseEntity<List<ProcessoResponse>> atualizarTodos() {
        return ResponseEntity.ok(
                processoService.updateAll().stream().map(processoMapper::toResponse).collect(Collectors.toList()));
    }

}