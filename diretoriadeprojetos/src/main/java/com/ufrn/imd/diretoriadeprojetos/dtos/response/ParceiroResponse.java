package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParceiroResponse {
    private UUID parceiroId;
    private String nome;
}
