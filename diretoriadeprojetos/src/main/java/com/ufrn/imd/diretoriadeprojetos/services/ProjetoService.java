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
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoRequest;
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
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoParceiroId;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjetoRepository;

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

    public Projeto salvar(ProjetoRequest projetoDto) {
        System.out.println("Projeto dto: " + projetoDto);
        if (projetoDto.getNumeroSipac() == null || projetoDto.getNumeroSipac().isBlank()) {
            throw new MissingFields("O número SIPAC do projeto é obrigatório.");
        }

        if (projetoDto.getAnoSipac() == null || projetoDto.getAnoSipac().isBlank()) {
            throw new MissingFields("O ano SIPAC do projeto é obrigatório.");
        }

        if (projetoDto.getContaContrato() == null || projetoDto.getContaContrato().isBlank()) {
            throw new MissingFields("Conta contrato é obrigatório.");
        }

        if (projetoDto.getCoordenadorId() == null) {
            throw new MissingFields("Coordenador é obrigatório.");
        }

        if (projetoDto.getParceirosId() == null || projetoDto.getParceirosId().isEmpty()) {
            throw new MissingFields("Pelo menos um parceiro é obrigatório.");
        }

        Coordenador coordenador = coordenadorService.findById(projetoDto.getCoordenadorId())
                .orElseThrow(() -> new EntityNotFound("Coordenador não encontrado"));
        Projeto projeto = new Projeto();

        ProjetoId projetoId = new ProjetoId(projetoDto.getNumeroSipac(), projetoDto.getAnoSipac());
        projeto.setId(projetoId);

        projeto.setTitulo(projetoDto.getTitulo());
        projeto.setLeiDeInformatica(projetoDto.isLeiInformatica());
        projeto.setResidencia(projetoDto.isResidencia());
        projeto.setValor(projetoDto.getValor());
        projeto.setContaContrato(projetoDto.getContaContrato());
        projeto.setDataInicio(projetoDto.getDataInicio());
        projeto.setDataFim(projetoDto.getDataFim());
        projeto.setStatus(projetoDto.getStatus());
        projeto.setDescricao(projetoDto.getDescricao());
        projeto.setCoordenador(coordenador);
        List<ProjetoParceiro> projetoParceiros = projetoDto.getParceirosId().stream().map(parceiroReq -> {
            if (parceiroReq.getParceiroId() == null) {
                throw new MissingFields("ID do Parceiro é obrigatório.");
            }

            UUID parceiroId = parceiroReq.getParceiroId();
            Parceiro parceiro = parceiroService.findById(parceiroId)
                    .orElseThrow(() -> new EntityNotFound("Parceiro com ID " + parceiroId + " não encontrado"));

            ProjetoParceiro projetoParceiro = new ProjetoParceiro();

            projetoParceiro.setId(new ProjetoParceiroId(projetoId, parceiroId));
            projetoParceiro.setParceiro(parceiro);
            projetoParceiro.setProjeto(projeto); // OK! 'projeto' agora está consistente.
            projetoParceiro.setNumeroFunpec(parceiroReq.getNumeroFunpec());

            return projetoParceiro;
        }).collect(Collectors.toList());

        // Adicionar parceiros ao projeto
        projeto.setParceiros(projetoParceiros);

        // Salvar no banco
        return projetoRepository.save(projeto);
    }

    public List<ProjetoResponse> buscarNaApi(String numeroSipac, String anoSipac) {
        List<ProjetoApiResponse> projetos = projetoClient.buscarNaApi(numeroSipac, anoSipac);

        return tratarProjeto(projetos);
    }

    private List<ProjetoResponse> tratarProjeto(List<ProjetoApiResponse> projetos) {
        List<ProjetoResponse> respostas = new ArrayList<>();

        for (ProjetoApiResponse projeto : projetos) {
            Coordenador coordenador = verificarCoordenador(projeto.getSiapeCoordenador(), projeto.getNomeCoordenador());
            List<Parceiro> parceiros = verificarParceiros(projetoClient.buscarParceirosNaApi(projeto.getIdProjeto()));

            ProjetoResponse respostaProjeto = new ProjetoResponse();
            respostaProjeto.setContaContrato(projeto.getNumeroFormatado());
            respostaProjeto.setCoordenador(coordenador);
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

    public List<Parceiro> verificarParceiros(List<ParceiroApiResponse> listaDeParceiros) {
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

        return parceiros;
    }

    public Coordenador verificarCoordenador(Long siapeCoordenador, String nomeCoordenador) {
        Optional<Coordenador> coordenadorOpt = coordenadorService.findById(siapeCoordenador);

        Coordenador coordenador;
        if (coordenadorOpt.isPresent()) {
            coordenador = coordenadorOpt.get();
        } else {

            Coordenador coordenadorApi = coordenadorService.buscarCoordenadorApi(siapeCoordenador);

            if (coordenadorApi != null) {
                coordenador = coordenadorService.save(coordenadorApi);

            } else {
                coordenador = coordenadorService
                        .save(new Coordenador(siapeCoordenador, nomeCoordenador));
            }
        }

        return coordenador;
    }
}