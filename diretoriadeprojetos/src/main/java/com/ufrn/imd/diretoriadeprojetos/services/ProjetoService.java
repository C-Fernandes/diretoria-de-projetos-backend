package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.clients.OrigemRecursoClient;
import com.ufrn.imd.diretoriadeprojetos.clients.ProjetoClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.ProjetoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.OrigemRecursoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.enums.TipoFinanciamento;
import com.ufrn.imd.diretoriadeprojetos.errors.EntityNotFound;
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
    private CoordenadorService coordenadorService;
    @Autowired
    private ParceiroService parceiroService;
    @Autowired
    private ProjetoClient projetoClient;
    @Autowired
    private ProjetoMapper projetoMapper;
    @Autowired
    private OrigemRecursoClient origemRecursoClient;

    public List<ProjetoResponse> listarTodos() {
        return projetoRepository.findAll().stream()
                .map(projetoMapper::toResponse) // Usa o mapper para converter cada projeto
                .collect(Collectors.toList());
    }

    public Projeto findById(ProjetoId id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
    }

    // Dentro da sua classe ProjetoService

    public Projeto salvar(ProjetoRequest projetoDto) {
        System.out.println("Projeto dto: " + projetoDto);

        // --- Bloco de Validações (continua o mesmo) ---
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

        Projeto projeto = projetoMapper.toEntity(projetoDto, coordenador);

        ProjetoId projetoId = projeto.getId();
        List<ProjetoParceiro> projetoParceiros = projetoDto.getParceirosId().stream().map(parceiroReq -> {

            if (parceiroReq.getParceiroId() == null) {
                throw new MissingFields("ID do Parceiro é obrigatório.");
            }
            UUID parceiroId = parceiroReq.getParceiroId();

            Parceiro parceiro = parceiroService.findById(parceiroId)
                    .orElseThrow(() -> new EntityNotFound("Parceiro com ID " + parceiroId + " não encontrado"));

            ProjetoParceiro projetoParceiro = new ProjetoParceiro();

            projetoParceiro.setId(new ProjetoParceiroId(projetoId, parceiroId));

            projetoParceiro.setProjeto(projeto);
            projetoParceiro.setParceiro(parceiro);

            projetoParceiro.setNumeroFunpec(parceiroReq.getNumeroFunpec());

            return projetoParceiro;

        }).collect(Collectors.toList());
        projeto.setParceiros(projetoParceiros);
        return projetoRepository.save(projeto);
    }

    public List<ProjetoResponse> buscarNaApi(String numeroSipac, String anoSipac) {
        List<ProjetoApiResponse> projetos = projetoClient.buscarNaApi(numeroSipac, anoSipac);

        return tratarProjeto(projetos);
    }

    private List<ProjetoResponse> tratarProjeto(List<ProjetoApiResponse> projetos) {
        return projetos.stream().map(projetoApi -> {
            Coordenador coordenador = verificarCoordenador(projetoApi.getSiapeCoordenador(),
                    projetoApi.getNomeCoordenador());
            List<Parceiro> parceiros = verificarParceiros(
                    origemRecursoClient.buscarParceirosNaApi(projetoApi.getIdProjeto()),
                    origemRecursoClient.buscarFontesDeRenda(projetoApi.getIdProjeto()));

            return projetoMapper.toResponse(projetoApi, coordenador, parceiros);
        }).collect(Collectors.toList());
    }

    public List<Parceiro> verificarParceiros(List<ParceiroApiResponse> listaDeParceiros,
            List<OrigemRecursoApiResponse> origensRecusos) {
        List<Parceiro> parceiros = new ArrayList<>();

        for (ParceiroApiResponse parceiroApi : listaDeParceiros) {
            Parceiro parceiro;
            Optional<Parceiro> parceiroOpt = parceiroService.findByIdParticipe(parceiroApi.getIdParticipe());

            if (parceiroOpt.isPresent()) {
                parceiro = parceiroOpt.get();
            } else {
                parceiroOpt = parceiroService.findByNome(parceiroApi.getNomeParticipe());
                if (parceiroOpt.isPresent()) {
                    parceiro = parceiroOpt.get();
                } else {
                    parceiro = new Parceiro(parceiroApi.getIdParticipe(), parceiroApi.getNomeParticipe());
                }
            }

            for (OrigemRecursoApiResponse origemRecurso : origensRecusos) {
                origemRecurso.toString();
                if (parceiro.getNome() != null && parceiro.getNome().equals(origemRecurso.getNomeFinanciador())) {
                    TipoFinanciamento tipo = TipoFinanciamento.fromNome(origemRecurso.getNomeEsferaOrigemRecurso());
                    parceiro.setTipoFinanciamento(tipo);
                }
            }
            parceiros.add(parceiroService.save(parceiro));
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