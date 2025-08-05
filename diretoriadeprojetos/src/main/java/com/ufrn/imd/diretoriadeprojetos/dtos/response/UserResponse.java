package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.UUID;

import org.hibernate.id.uuid.UuidGenerator;

import com.ufrn.imd.diretoriadeprojetos.enums.Role;
import com.ufrn.imd.diretoriadeprojetos.models.User;

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

    public UserResponse(User user) {
        this.id = user.getId();
        this.nome = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.aprovado = user.getIsAdminApproved();
    }

};
