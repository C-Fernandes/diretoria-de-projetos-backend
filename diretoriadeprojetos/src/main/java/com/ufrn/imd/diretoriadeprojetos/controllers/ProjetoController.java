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
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.services.CoordenadorService;
import com.ufrn.imd.diretoriadeprojetos.services.ParceiroService;
import com.ufrn.imd.diretoriadeprojetos.services.ProjetoService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {
    @Autowired
    private ProjetoService projetoService;

    @GetMapping
    public List<Projeto> listarTodos() {
        return projetoService.listarTodos();
    }

    @GetMapping( // ou a URI que você estiver usando
            params = "externo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProjetoResponse>> buscarNaApi(
            @RequestParam String numeroSipac,
            @RequestParam String anoSipac) {

        List<ProjetoResponse> fluxo = projetoService
                .buscarNaApi(numeroSipac, anoSipac);

        // Retornamos Flux diretamente, que o Spring vai serializar como um array JSON
        return ResponseEntity.ok(fluxo);
    }

    /*
     * @GetMapping("/{nSipac}")
     * public ResponseEntity<Projeto> buscarPorId(@PathVariable String nSipac) {
     * return ResponseEntity.ok(projetoService.buscarPorId(nSipac));
     * }
     */
    @PostMapping
    public ResponseEntity<Projeto> criar(@RequestBody ProjetoResponse projeto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(projetoService.salvar(projeto));
    }
    /*
     * @PutMapping("/{nSipac}")
     * public ResponseEntity<Projeto> atualizar(@PathVariable String
     * nSipac, @RequestBody Projeto projetoAtualizado) {
     * return ResponseEntity.ok(projetoService.atualizar(nSipac,
     * projetoAtualizado));
     * }
     * 
     * @DeleteMapping("/{nSipac}")
     * public ResponseEntity<Void> deletar(@PathVariable String nSipac) {
     * projetoService.deletar(nSipac);
     * return ResponseEntity.noContent().build();
     * }
     */
}