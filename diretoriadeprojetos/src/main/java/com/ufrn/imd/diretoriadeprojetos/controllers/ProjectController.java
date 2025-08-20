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

import com.ufrn.imd.diretoriadeprojetos.annotations.IsUser;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjectRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjectResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Project;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;
import com.ufrn.imd.diretoriadeprojetos.services.ProjectService;

@RestController
@RequestMapping("/projetos")
public class ProjectController {
    @Autowired
    private ProjectService projetoService;

    @GetMapping
    public List<ProjectResponse> findAll() {
        return projetoService.findAll();
    }

    @IsUser
    @GetMapping(params = "externo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectResponse> buscarNaApi(
            @RequestParam long numeroSipac,
            @RequestParam long anoSipac) {

        ProjectResponse fluxo = projetoService
                .buscarNaApi(numeroSipac, anoSipac);

        return ResponseEntity.ok(fluxo);
    }

    @IsUser
    @DeleteMapping("/{numeroSipac}/{anoSipac}")
    public ResponseEntity<List<ProjectResponse>> delete(
            @PathVariable long numeroSipac,
            @PathVariable long anoSipac) {

        projetoService.delete(numeroSipac, anoSipac);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @IsUser
    public ResponseEntity<Project> criar(@RequestBody ProjectRequest projeto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoService.salvar(projeto));
    }

    @GetMapping("/{numeroSipac}/{anoSipac}")
    public ResponseEntity<ProjectResponse> findById(
            @PathVariable long numeroSipac,
            @PathVariable long anoSipac) {
        ProjectResponse projeto = projetoService.findById(new ProjectId(numeroSipac, anoSipac));
        return ResponseEntity.ok(projeto);
    }

}