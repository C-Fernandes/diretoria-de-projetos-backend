package com.ufrn.imd.diretoriadeprojetos.dtos;

import java.util.Date;

public record ProjetoDTO(String numeroSipac, String anoSipac, String titulo, String nFunpec, Boolean sebrae,
                Boolean embrapii,
                Boolean leiDeInformatica,
                double valor, Date dataInicio, String coordenadorId, String parceiroCnpj,
                Date dataFim, String contaContrato

) {

}
