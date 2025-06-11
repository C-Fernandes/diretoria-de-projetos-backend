package com.ufrn.imd.diretoriadeprojetos.dtos.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ProjetoParceiroRequest {
    private UUID parceiroId;
    private String nFunpec;
}