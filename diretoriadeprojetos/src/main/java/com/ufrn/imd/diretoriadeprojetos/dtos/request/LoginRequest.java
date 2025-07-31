package com.ufrn.imd.diretoriadeprojetos.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "O e-mail é obrigatório.")
    private String email;
    @NotBlank(message = "A senha é obrigatória.")
    private String senha;
}