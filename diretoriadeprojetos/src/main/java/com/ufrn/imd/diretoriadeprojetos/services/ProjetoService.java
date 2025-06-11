package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.clients.ProjetoClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.ProjetoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoParceiroRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.EntityNotFound;
import com.ufrn.imd.diretoriadeprojetos.errors.HttpError;
import com.ufrn.imd.diretoriadeprojetos.errors.MissingFields;
import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Parceiro;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoParceiro;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjetoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProjetoService {
    @Autowired
    private ProjetoRepository projetoRepository;
    @Autowired
    private ProjetoMapper projetoMapper;
    @Autowired
    private CoordenadorService coordenadorService;
    @Autowired
    private ParceiroService parceiroService;
    @Autowired
    private ProjetoClient projetoClient;

    public List<Projeto> listarTodos() {
        return projetoRepository.findAll();
    }

    public Projeto buscarPorId(ProjetoId id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
    }

    public Projeto salvar(ProjetoResponse projetoDTO) {
        if (projetoDTO.getContaContrato() == null || projetoDTO.getContaContrato().isBlank()) {
            throw new MissingFields("Conta contrato é obrigatório.");
        }

        if (projetoDTO.getCoordenadorId() == null) {
            throw new MissingFields("Coordenador é obrigatório.");
        }

        if (projetoDTO.getParceirosId() == null || projetoDTO.getParceirosId().isEmpty()) {
            throw new MissingFields("Pelo menos um parceiro é obrigatório.");
        }

        // Buscar coordenador
        Coordenador coordenador = coordenadorService.findById(projetoDTO.getCoordenadorId())
                .orElseThrow(() -> new EntityNotFound("Coordenador não encontrado"));

        // Buscar e montar os parceiros
        List<ProjetoParceiro> projetoParceiros = projetoDTO.getParceirosId().stream().map(parceiroId -> {
            Parceiro parceiro = parceiroService.findById(parceiroId)
                    .orElseThrow(() -> new EntityNotFound("Parceiro com ID " + parceiroId + " não encontrado"));

            ProjetoParceiro projetoParceiro = new ProjetoParceiro();
            projetoParceiro.setParceiro(parceiro);
            return projetoParceiro;
        }).collect(Collectors.toList());

        Projeto projeto = projetoMapper.toEntity(projetoDTO, coordenador, projetoParceiros);

        projetoParceiros.forEach(pp -> pp.setProjeto(projeto));

        return projetoRepository.save(projeto);
    }

    public List<ProjetoResponse> buscarNaApi(String numeroSipac, String anoSipac) {
        List<ProjetoApiResponse> projetos = projetoClient.buscarNaApi(numeroSipac, anoSipac);

        return tratarProjeto(projetos);
    }

    private List<ProjetoResponse> tratarProjeto(List<ProjetoApiResponse> projetos) {
        List<ProjetoResponse> respostas = new ArrayList<>();

        for (ProjetoApiResponse projeto : projetos) {
            // Verifica se coordenador existe
            Optional<Coordenador> coordenadorOpt = coordenadorService.findById(projeto.getSiapeCoordenador());

            Coordenador coordenador;
            if (coordenadorOpt.isPresent()) {
                coordenador = coordenadorOpt.get();
            } else {

                Coordenador coordenadorApi = coordenadorService.buscarCoordenadorApi(projeto.getSiapeCoordenador());

                if (coordenadorApi != null) {
                    coordenador = coordenadorService.save(coordenadorApi);

                } else {
                    coordenador = coordenadorService
                            .save(new Coordenador(projeto.getSiapeCoordenador(), projeto.getNomeCoordenador()));
                }
            }

            // Buscar parceiros na API - lista
            List<ParceiroApiResponse> listaDeParceiros = projetoClient.buscarParceirosNaApi(projeto.getIdProjeto());

            List<Parceiro> parceiros = new ArrayList<>();
            for (ParceiroApiResponse parceiroApi : listaDeParceiros) {
                Optional<Parceiro> parceiroOpt = parceiroService.findByIdParticipe(parceiroApi.getIdParticipe());
                if (parceiroOpt.isEmpty()) {
                    parceiroOpt = parceiroService.findByNome(parceiroApi.getNomeParticipe());
                }

                if (parceiroOpt.isPresent()) {
                    parceiros.add(parceiroOpt.get());
                } else {
                    Parceiro novoParceiro = new Parceiro(parceiroApi.getIdParticipe(), parceiroApi.getNomeParticipe());
                    parceiros.add(parceiroService.save(novoParceiro));
                }
            }

            ProjetoResponse respostaProjeto = new ProjetoResponse();
            respostaProjeto.setContaContrato(projeto.getNumeroFormatado());
            respostaProjeto.setCoordenadorId(coordenador.getMatricula());
            respostaProjeto.setIdProjeto(projeto.getIdProjeto());
            respostaProjeto.setDescricao(projeto.getJustificativa());
            respostaProjeto.setTitulo(projeto.getTituloProjeto());
            respostaProjeto.setDataFim(projeto.getFimExecucao());
            respostaProjeto.setDataInicio(projeto.getInicioExecucao());

            List<UUID> listaParceiros = new ArrayList<>();
            for (Parceiro parceiro : parceiros) {
                listaParceiros.add(parceiro.getId());
            }
            respostaProjeto.setParceirosId(listaParceiros);
            respostaProjeto.setStatus(projeto.getDescricaoStatus());
            respostaProjeto.setValor(projeto.getValorProjeto());

            respostas.add(respostaProjeto);
        }

        System.out.println(respostas);
        return respostas;
    }
}