package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ufrn.imd.diretoriadeprojetos.repository.ProcessoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.clients.ProcessoClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.MovimentacaoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.ProcessoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.ProjetoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProcessoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.MovimentacaoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.ApiError;
import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Movimentacao;
import com.ufrn.imd.diretoriadeprojetos.models.Processo;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProcessoId;

@Service
public class ProcessoService {

    private final ProcessoRepository processoRepository;
    @Autowired
    private ProcessoClient processoClient;

    @Autowired
    private ProcessoMapper processoMapper;

    @Autowired
    private MovimentacaoMapper movimentacaoMapper;

    @Autowired
    private CoordenadorService coordenadorService;

    ProcessoService(ProcessoRepository processoRepository) {
        this.processoRepository = processoRepository;
    }

    public List<Processo> findAll() {
        List<Processo> processos = processoRepository.findAll();
        return processos;
    }

    public ProcessoResponse buscarNaApi(long radical, long numProtocolo, long ano, long dv) {

        ProcessoApiResponse projetoApiResponse = processoClient.buscarProcesso(radical, numProtocolo, ano, dv);

        String siapeString = projetoApiResponse.getSiape();
        if (siapeString == null || siapeString.isBlank()) {
            throw new ApiError("A resposta da API não continha um SIAPE para o coordenador.");
        }
        long siape = Long.parseLong(siapeString);
        Coordenador coordenador = coordenadorService.findOrCreateBySiape(siape);
        Processo processoFinal = processoMapper.toEntity(projetoApiResponse, coordenador);
        System.out.println("Processo final: " + processoFinal.toString());
        return processoMapper.toResponse(processoFinal);
    }

    public void delete(long radical, long numProtocolo, long ano, long dv) {
        Optional<Processo> processo = findById(radical, numProtocolo, ano, dv);
        if (processo.isPresent())
            processoRepository.delete(processo.get());
        else
            throw new EntityNotFoundException("Processo não encontrados");
    }

    public Optional<Processo> findById(long radical, long numProtocolo, long ano, long dv) {
        return processoRepository.findById(new ProcessoId(radical, numProtocolo, ano, dv));
    }

    public ProcessoResponse salvar(ProcessoRequest processoRequest) {
        Processo novoProcesso = processoMapper.toEntity(processoRequest, processoRequest.getCoordenador());
        List<Movimentacao> movimentacoes = buscarMovimentacoes(novoProcesso);
        novoProcesso.setMovimentacoes(movimentacoes);
        return processoMapper.toResponse(processoRepository.save(novoProcesso));
    }

    @Transactional
    public List<Processo> atualizarTodos() {
        List<Processo> processosExistentes = findAll(); // Carrega os processos existentes do banco

        List<Processo> processosAtualizadosSalvos = new ArrayList<>();

        for (Processo processo : processosExistentes) {
            // 1. Busca as novas movimentações da API externa
            List<Movimentacao> novasMovimentacoes = buscarMovimentacoes(processo);

            // 2. Cria conjuntos para facilitar a comparação e evitar N^2
            Set<Movimentacao> movimentacoesAtuaisNoProcesso = new HashSet<>(processo.getMovimentacoes());
            Set<Movimentacao> movimentacoesRecebidasDaAPI = new HashSet<>(novasMovimentacoes);

            // 3. Remove movimentações que existem no banco mas não vieram da API (órfãs)
            // Percorre uma cópia para evitar ConcurrentModificationException
            new ArrayList<>(movimentacoesAtuaisNoProcesso).forEach(movExistente -> {
                if (!movimentacoesRecebidasDaAPI.contains(movExistente)) {
                    // Remove da coleção existente e desassocia (chama o método auxiliar em
                    // Processo)
                    processo.removeMovimentacao(movExistente);
                }
            });
            movimentacoesRecebidasDaAPI.forEach(movNova -> {
                if (!movimentacoesAtuaisNoProcesso.contains(movNova)) {
                    processo.addMovimentacao(movNova);
                }

            });

            processosAtualizadosSalvos.add(processoRepository.save(processo));
        }
        return processosAtualizadosSalvos;
    }

    private List<Movimentacao> buscarMovimentacoes(Processo processo) {
        List<MovimentacaoApiResponse> movimentacoesApiResponse = processoClient
                .buscarMovimetacoes(processo.getIdProcesso());
        List<Movimentacao> movimentacoesMapeadas = new ArrayList<>();
        for (MovimentacaoApiResponse movimentacaoApi : movimentacoesApiResponse) {
            Movimentacao movimentacao = movimentacaoMapper.toEntity(movimentacaoApi, processo);
            movimentacoesMapeadas.add(movimentacao);
        }
        return movimentacoesMapeadas;
    }

}
