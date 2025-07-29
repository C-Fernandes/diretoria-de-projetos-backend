package com.ufrn.imd.diretoriadeprojetos.dtos.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProcessoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Processo;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProcessoId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;

@Component
public class ProcessoMapper {

    public Processo toEntity(ProcessoApiResponse apiResponse, Coordenador coordenador) {
        if (apiResponse == null) {
            return null;
        }

        ProcessoId processoId = new ProcessoId(
                apiResponse.getRadical(),
                apiResponse.getNumProtocolo(),
                apiResponse.getAno(),
                apiResponse.getDv());

        Processo processo = new Processo();
        processo.setId(processoId);
        processo.setIdProcesso(apiResponse.getIdProcesso());

        processo.setCoordenador(coordenador);

        return processo;
    }

    public Processo toEntity(ProcessoRequest processoRequest, Coordenador coordenador) {

        ProcessoId processoId = new ProcessoId(
                processoRequest.getRadical(),
                processoRequest.getNumProtocolo(),
                processoRequest.getAno(),
                processoRequest.getDv());

        Processo processo = new Processo();
        processo.setId(processoId);
        processo.setIdProcesso(processoRequest.getIdProcesso());
        processo.setDataInicio(processoRequest.getDataInicio());
        processo.setFinanciador(processoRequest.getFinanciador());
        processo.setNome(processoRequest.getNome());
        processo.setObservacao(processoRequest.getObservacao());
        processo.setValor(processoRequest.getValor());
        processo.setProjetoId(new ProjetoId(processoRequest.getNumeroSipac(), processoRequest.getAnoSipac()));
        processo.setCoordenador(coordenador);

        return processo;
    }

    public ProcessoResponse toResponse(Processo processo) {
        if (processo == null) {
            return null;
        }

        ProcessoResponse response = new ProcessoResponse();
        ProcessoId id = processo.getId();

        response.setRadical(id.getRadical());
        response.setNumProtocolo(id.getNumProtocolo());
        response.setAno(id.getAno());
        response.setDv(id.getDv());

        response.setNome(processo.getNome());
        response.setDataInicio(processo.getDataInicio());
        response.setObservacao(processo.getObservacao());
        response.setIdProcesso(processo.getIdProcesso());
        response.setValor(processo.getValor());
        response.setFinanciador(processo.getFinanciador());
        if (processo.getProjetoId() != null) {
            response.setAnoSipac(processo.getProjetoId().getAnoSipac());
            response.setNumeroSipac(processo.getProjetoId().getNumeroSipac());
        }
        response.setCoordenador(processo.getCoordenador());

        response.setMovimentacoes(processo.getMovimentacoes());

        return response;
    }
}