package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ufrn.imd.diretoriadeprojetos.repository.ProcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.clients.ProcessoClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.MovimentacaoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.ProcessoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProcessoRequest;
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

    public List<ProcessoResponse> findAll() {
        List<Processo> processos = processoRepository.findAll();
        return processos.stream()
                .map(processoMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProcessoResponse buscarNaApi(long radical, long numProtocolo, long ano, long dv) {
        System.out.println(">>> Service: Buscando processo na API externa...");
        ProcessoApiResponse projetoApiResponse = processoClient.buscarProcesso(radical, numProtocolo, ano, dv);
        System.out.println(">>> Service: Resposta da API de processo recebida.");

        // Extrai o SIAPE para evitar chamadas repetidas de Long.parseLong
        String siapeString = projetoApiResponse.getSiape();
        if (siapeString == null || siapeString.isBlank()) {
            System.out.println("!!! ERRO: SIAPE não encontrado na resposta da API.");
            throw new ApiError("A resposta da API não continha um SIAPE para o coordenador.");
        }
        long siape = Long.parseLong(siapeString);
        System.out.println(">>> Service: Buscando coordenador com SIAPE: " + siape);

        Optional<Coordenador> coordenadorOpt = coordenadorService.buscarPorSiape(siape);
        Coordenador coordenador;

        if (coordenadorOpt.isEmpty()) {
            System.out.println(
                    ">>> Service: Coordenador não encontrado no banco local. Buscando na API de servidores...");
            coordenador = coordenadorService.buscarCoordenadorApi(siape);
            coordenadorService.save(coordenador);
            System.out.println(">>> Service: Coordenador obtido da API de servidores.");
        } else {

            System.out.println(">>> Service: Coordenador encontrado no banco de dados local.");
            coordenador = coordenadorOpt.get();
        }

        System.out.println(">>> Service: Mapeando ProcessoApiResponse para a entidade Processo...");
        // Supondo que você ajustou seu mapper para aceitar o coordenador
        Processo processoFinal = processoMapper.toEntity(projetoApiResponse, coordenador);

        return processoMapper.toResponse(processoFinal);
    }

    public void delete(long radical, long numProtocolo, long ano, long dv) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    public Optional<Processo> findById(long radical, long numProtocolo, long ano, long dv) {
        return processoRepository.findById(new ProcessoId(radical, numProtocolo, ano, dv));
    }

    public ProcessoResponse salvar(ProcessoRequest processoRequest) {

        Processo novoProcesso = processoMapper.toEntity(processoRequest, processoRequest.getCoordenador());

        // 2. Busca as movimentações na API externa
        List<MovimentacaoApiResponse> movimentacoesApiResponse = processoClient
                .buscarMovimetacoes(novoProcesso.getIdProcesso());

        // 3. Mapeia as movimentações e as associa ao novo processo
        List<Movimentacao> movimentacoesMapeadas = new ArrayList<>();
        for (MovimentacaoApiResponse movimentacaoApi : movimentacoesApiResponse) {
            Movimentacao movimentacao = movimentacaoMapper.toEntity(movimentacaoApi, novoProcesso);
            movimentacoesMapeadas.add(movimentacao);
        }

        novoProcesso.setMovimentacoes(movimentacoesMapeadas);

        return processoMapper.toResponse(processoRepository.save(novoProcesso));
    }

}
