package com.ufrn.imd.diretoriadeprojetos.dtos.mapper;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Component;

import com.ufrn.imd.diretoriadeprojetos.dtos.response.MovimentacaoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Movement;
import com.ufrn.imd.diretoriadeprojetos.models.ProjectProcess;

@Component
public class MovimentacaoMapper {
    public Movement toEntity(MovimentacaoApiResponse movimentacaoResponse, ProjectProcess processo) {
        Movement movimentacao = new Movement();

        movimentacao.setSentDate(movimentacaoResponse.getDataEnvioOrigem());
        movimentacao.setReceivedDate(movimentacaoResponse.getDataRecebimentoDestino());
        movimentacao.setId(movimentacaoResponse.getIdMovimentacao());
        movimentacao.setProcess(processo);
        movimentacao.setDestinationUnit(movimentacaoResponse.getUnidadeDestino());
        movimentacao.setOriginUnit(movimentacaoResponse.getUnidadeOrigem());

        return movimentacao;
    }
}
