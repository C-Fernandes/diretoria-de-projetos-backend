package com.ufrn.imd.diretoriadeprojetos.dtos.mapper;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Component;

import com.ufrn.imd.diretoriadeprojetos.dtos.response.MovimentacaoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Movimentacao;
import com.ufrn.imd.diretoriadeprojetos.models.Processo;

@Component
public class MovimentacaoMapper {
    public Movimentacao toEntity(MovimentacaoApiResponse movimentacaoResponse, Processo processo) {
        Movimentacao movimentacao = new Movimentacao();

        movimentacao.setDataEnvioOrigem(movimentacaoResponse.getDataEnvioOrigem());
        movimentacao.setDataRecebimentoDestino(movimentacaoResponse.getDataRecebimentoDestino());
        movimentacao.setId(movimentacaoResponse.getIdMovimentacao());
        movimentacao.setProcesso(processo);
        movimentacao.setUnidadeDestino(movimentacaoResponse.getUnidadeDestino());
        movimentacao.setUnidadeOrigem(movimentacaoResponse.getUnidadeOrigem());

        return movimentacao;
    }
}
