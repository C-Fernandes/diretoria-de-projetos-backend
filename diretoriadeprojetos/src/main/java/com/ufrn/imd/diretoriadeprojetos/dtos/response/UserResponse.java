package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.UUID;

import org.hibernate.id.uuid.UuidGenerator;

import com.ufrn.imd.diretoriadeprojetos.enums.Role;
import com.ufrn.imd.diretoriadeprojetos.models.Usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String nome;
    private String email;
    private Role role;
    private boolean aprovado;

    public UserResponse(Usuario user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.aprovado = user.getAprovadoPeloAdmin();
    }

};
