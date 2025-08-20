package com.ufrn.imd.diretoriadeprojetos.dtos.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProcessoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Coordinator;
import com.ufrn.imd.diretoriadeprojetos.models.ProjectProcess;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProcessId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;

@Component
public class ProcessoMapper {

    public ProjectProcess toEntity(ProcessoApiResponse apiResponse, Coordinator coordenador) {
        if (apiResponse == null) {
            return null;
        }

        ProcessId processoId = new ProcessId(
                apiResponse.getRadical(),
                apiResponse.getNumProtocolo(),
                apiResponse.getAno(),
                apiResponse.getDv());

        ProjectProcess processo = new ProjectProcess();
        processo.setId(processoId);
        processo.setExternalProcessId(apiResponse.getIdProcesso());

        processo.setCoordinator(coordenador);

        return processo;
    }

    public ProjectProcess toEntity(ProcessoRequest processoRequest, Coordinator coordenador) {

        ProcessId processoId = new ProcessId(
                processoRequest.getRadical(),
                processoRequest.getNumProtocolo(),
                processoRequest.getAno(),
                processoRequest.getDv());

        ProjectProcess processo = new ProjectProcess();
        processo.setId(processoId);
        processo.setExternalProcessId(processoRequest.getIdProcesso());
        processo.setStartDate(processoRequest.getDataInicio());
        processo.setFunder(processoRequest.getFinanciador());
        processo.setName(processoRequest.getNome());
        processo.setObservation(processoRequest.getObservacao());
        processo.setAmount(processoRequest.getValor());
        processo.setProjectId(new ProjectId(processoRequest.getNumeroSipac(), processoRequest.getAnoSipac()));
        processo.setCoordinator(coordenador);
        processo.setIsActive(processoRequest.getTramitando());
        return processo;
    }

    public ProcessoResponse toResponse(ProjectProcess processo) {
        if (processo == null) {
            return null;
        }

        ProcessoResponse response = new ProcessoResponse();
        ProcessId id = processo.getId();

        response.setRadical(id.getRadical());
        response.setNumProtocolo(id.getProtocolNumber());
        response.setAno(id.getYear());
        response.setDv(id.getDv());

        response.setNome(processo.getName());
        response.setDataInicio(processo.getStartDate());
        response.setObservacao(processo.getObservation());
        response.setIdProcesso(processo.getExternalProcessId());
        if (processo.getAmount() != null)
            response.setValor(processo.getAmount());
        else
            response.setValor(0);
        response.setFinanciador(processo.getFunder());
        if (processo.getProjectId() != null) {
            response.setAnoSipac(processo.getProjectId().getSipacYear());
            response.setNumeroSipac(processo.getProjectId().getSipacNumber());
        }
        response.setCoordenador(processo.getCoordinator());
        response.setTramitando(processo.getIsActive());
        response.setMovimentacoes(processo.getMovements());

        return response;
    }
}