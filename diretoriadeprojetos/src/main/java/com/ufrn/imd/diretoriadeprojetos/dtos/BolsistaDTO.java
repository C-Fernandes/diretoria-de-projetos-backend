package com.ufrn.imd.diretoriadeprojetos.dtos;

import java.util.UUID;
import java.util.Date;

public record BolsistaDTO(
        UUID uuid,
        String nSipac,
        String cpf,
        String nome,
        String email,
        int rubrica,
        String tipoSuperior,
        String curso,
        Boolean docente,
        String formacao, Date dataInicio, Date dataFim, int cHSemanal) {

}
