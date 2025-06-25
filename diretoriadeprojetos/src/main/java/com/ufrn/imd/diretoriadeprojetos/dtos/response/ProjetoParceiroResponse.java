package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjetoParceiroResponse {
    private ParceiroResponse parceiro;

    private String numeroFunpec;
}
