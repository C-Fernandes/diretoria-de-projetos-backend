package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.UUID;

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
    private UUID uuid;
    private String name;

    public UserResponse(Usuario user) {
        this.uuid = user.getId();
        this.name = user.getNome();
    };
};
