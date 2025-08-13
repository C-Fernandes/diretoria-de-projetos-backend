package com.ufrn.imd.diretoriadeprojetos.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufrn.imd.diretoriadeprojetos.annotations.IsAdmin;
import com.ufrn.imd.diretoriadeprojetos.annotations.IsSelfOrAdmin;
import com.ufrn.imd.diretoriadeprojetos.annotations.IsGuest;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.LoginRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.RegisterRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.LoginResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.UserResponse;
import com.ufrn.imd.diretoriadeprojetos.enums.Role;
import com.ufrn.imd.diretoriadeprojetos.models.User;
import com.ufrn.imd.diretoriadeprojetos.services.AuthService;
import com.ufrn.imd.diretoriadeprojetos.services.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@Valid @RequestBody RegisterRequest request) {
        authService.registrar(request);
        return ResponseEntity.created(null).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse user = authService.login(request.getEmail(), request.getSenha());
        return ResponseEntity.ok(user);
    }

    @IsGuest
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            @AuthenticationPrincipal User user) {
        UserResponse response = new UserResponse(user);
        return ResponseEntity.ok(response);
    };

    @IsAdmin
    @GetMapping()
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream().map(UserResponse::new).collect(Collectors.toList()));
    }

    @IsAdmin
    @PatchMapping("/{user}/{approval}")
    public ResponseEntity<?> updateApprovalStatus(
            @PathVariable UUID user,
            @PathVariable boolean approval) {
        userService.updateApprovalStatus(user, approval);
        return ResponseEntity.ok().build();

    }

    @IsAdmin
    @PatchMapping("/{user}/role/{roleName}")
    public ResponseEntity<?> updateUserRole(
            @PathVariable UUID user,
            @PathVariable String roleName) {

        Role newRole = Role.valueOf(roleName.toUpperCase());

        userService.updateUserRole(user, newRole);

        return ResponseEntity.ok().build();
    }
}
