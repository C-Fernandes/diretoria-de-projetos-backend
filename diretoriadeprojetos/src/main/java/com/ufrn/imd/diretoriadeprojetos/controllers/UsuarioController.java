package com.ufrn.imd.diretoriadeprojetos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufrn.imd.diretoriadeprojetos.dtos.request.LoginRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.RegisterRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.LoginResponse;
import com.ufrn.imd.diretoriadeprojetos.services.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioService authService;

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@Valid @RequestBody RegisterRequest request) {
        authService.registrar(request);
        return ResponseEntity.created(null).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }
};