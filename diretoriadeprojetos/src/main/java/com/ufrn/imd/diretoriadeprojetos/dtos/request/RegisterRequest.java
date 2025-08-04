package com.ufrn.imd.diretoriadeprojetos.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "O nome é obrigatório!")
    @Size(max = 45, message = "O nome não pode ter mais de 45 caracteres!")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório!")
    @Email(message = "O e-mail deve ser válido!")
    private String email;

    @NotBlank(message = "A senha é obrigatória!")
    @Size(min = 6, max = 20, message = "A senha deve ter entre 6 e 20 caracteres!")
    private String senha;

}