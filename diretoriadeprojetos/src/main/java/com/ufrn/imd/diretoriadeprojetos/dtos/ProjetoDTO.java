package com.ufrn.imd.diretoriadeprojetos.dtos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Parceiro;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoHasBolsista;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

public record ProjetoDTO(String nSipac, String titulo, String nFunpec, Boolean sebrae, Boolean embrapii,
        Boolean leiDeInformatica,
        double valor, Date dataInicio, String coordenadorId, UUID parceiroId,
        Date dataFim, String contaContrato

) {

}
