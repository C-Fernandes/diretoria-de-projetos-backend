package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ufrn.imd.diretoriadeprojetos.repository.ProjectProcessRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.clients.ProcessClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.MovimentacaoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.ProcessoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProcessoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.MovimentacaoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.ApiError;
import com.ufrn.imd.diretoriadeprojetos.models.Coordinator;
import com.ufrn.imd.diretoriadeprojetos.models.Movement;
import com.ufrn.imd.diretoriadeprojetos.models.ProjectProcess;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProcessId;

@Service
public class ProcessService {

    private final ProjectProcessRepository processRepository;
    @Autowired
    private ProcessClient processClient;
    @Autowired
    private ProcessoMapper processoMapper;

    @Autowired
    private MovimentacaoMapper movimentacaoMapper;

    @Autowired
    private CoordinatorService coordenadorService;

    ProcessService(ProjectProcessRepository processoRepository) {
        this.processRepository = processoRepository;
    }

    public List<ProjectProcess> findAll() {
        return processRepository.findAll();
    }

    public ProjectProcess fetchFromApi(long radical, long protocolNumber, long year, long dv) {
        ProcessoApiResponse processApiResponse = processClient.findProcess(radical, protocolNumber, year, dv);

        String siapeString = processApiResponse.getSiape();
        if (siapeString == null || siapeString.isBlank()) {
            throw new ApiError("A resposta da API não continha um SIAPE para o coordenador.");
        }
        long siape = Long.parseLong(siapeString);
        Coordinator coordinator = coordenadorService.findOrCreateBySiape(siape);
        return processoMapper.toEntity(processApiResponse, coordinator);

    }

    public void delete(long radical, long protocolNumber, long year, long checkDigit) {
        ProcessId processId = new ProcessId(radical, protocolNumber, year, checkDigit);
        ProjectProcess processToDelete = processRepository.findById(processId)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o ID fornecido."));
        processRepository.delete(processToDelete);
    }

    public Optional<ProjectProcess> findById(long radical, long protocolNumber, long year, long checkDigit) {
        return processRepository.findById(new ProcessId(radical, protocolNumber, year, checkDigit));
    }

    public ProjectProcess save(ProcessoRequest processRequest) {
        ProjectProcess newProcess = processoMapper.toEntity(processRequest, processRequest.getCoordenador());
        List<Movement> movements = fetchMovements(newProcess);
        newProcess.setMovements(movements);
        return processRepository.save(newProcess);

    }

    @Transactional
    public List<ProjectProcess> updateAll() {
        List<ProjectProcess> existingProcesses = findAll();

        List<ProjectProcess> savedUpdatedProcesses = new ArrayList<>();

        for (ProjectProcess process : existingProcesses) {
            List<Movement> newMovements = fetchMovements(process);

            Set<Movement> currentMovementsInProcess = new HashSet<>(process.getMovements());
            Set<Movement> movementsReceivedFromAPI = new HashSet<>(newMovements);

            new ArrayList<>(currentMovementsInProcess).forEach(existingMovement -> {
                if (!movementsReceivedFromAPI.contains(existingMovement)) {
                    process.removeMovement(existingMovement);
                }
            });

            movementsReceivedFromAPI.forEach(newMovement -> {
                if (!currentMovementsInProcess.contains(newMovement)) {
                    process.addMovement(newMovement);
                }
            });

            savedUpdatedProcesses.add(processRepository.save(process));
        }
        return savedUpdatedProcesses;
    }

    private List<Movement> fetchMovements(ProjectProcess processo) {
        List<MovimentacaoApiResponse> movimentacoesApiResponse = processClient
                .findMovements(processo.getExternalProcessId());
        List<Movement> movimentacoesMapeadas = new ArrayList<>();
        for (MovimentacaoApiResponse movimentacaoApi : movimentacoesApiResponse) {
            Movement movimentacao = movimentacaoMapper.toEntity(movimentacaoApi, processo);
            movimentacoesMapeadas.add(movimentacao);
        }
        return movimentacoesMapeadas;
    }

}
