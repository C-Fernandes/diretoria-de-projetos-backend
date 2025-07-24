package com.ufrn.imd.diretoriadeprojetos.controllers;

import java.util.List;

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

import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Processo;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;
import com.ufrn.imd.diretoriadeprojetos.services.ProcessoService;
import com.ufrn.imd.diretoriadeprojetos.services.ProjetoService;

@RestController
@RequestMapping("/processos")
public class ProcessosController {
    @Autowired
    private ProcessoService processoService;

    @GetMapping
    public List<ProjetoResponse> findAll() {
        return processoService.findAll();
    }

    @GetMapping(params = "externo")
    public ResponseEntity<List<ProjetoResponse>> buscarNaApi(
            @RequestParam long radical,
            @RequestParam long numProtocolo, @RequestParam long ano, @RequestParam long dv) {

        List<ProjetoResponse> fluxo = processoService
                .buscarNaApi(radical, numProtocolo, ano, dv);

        return ResponseEntity.ok(fluxo);
    }

    @DeleteMapping("/{radical}/{numProtocolo}/{ano}/{dv}")
    public ResponseEntity<List<ProjetoResponse>> delete(
            @PathVariable long radical,
            @PathVariable long numProtocolo, @PathVariable long ano, @PathVariable long dv) {

        processoService.delete(radical, numProtocolo, ano, dv);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Processo> criar(@RequestBody Processo processo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(processoService.salvar(processo));
    }

    @GetMapping("/{radical}/{numProtocolo}/{ano}/{dv}")
    public ResponseEntity<ProjetoResponse> findById(
            @PathVariable long radical, @PathVariable long numProtocolo, @PathVariable long ano,
            @PathVariable long dv) {
        ProjetoResponse projeto = processoService.findById(radical, numProtocolo, ano, dv);
        return ResponseEntity.ok(projeto);
    }

}