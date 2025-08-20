package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.clients.ProjectClient;
import com.ufrn.imd.diretoriadeprojetos.clients.ResourceOriginClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.ProjectMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoParceiroRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjectRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.OrigemRecursoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjectResponse;
import com.ufrn.imd.diretoriadeprojetos.enums.FundingType;
import com.ufrn.imd.diretoriadeprojetos.enums.FundingType;
import com.ufrn.imd.diretoriadeprojetos.errors.EntityNotFound;
import com.ufrn.imd.diretoriadeprojetos.errors.MissingFields;
import com.ufrn.imd.diretoriadeprojetos.errors.ResourceConflict;
import com.ufrn.imd.diretoriadeprojetos.models.Coordinator;
import com.ufrn.imd.diretoriadeprojetos.models.Partner;
import com.ufrn.imd.diretoriadeprojetos.models.Project;
import com.ufrn.imd.diretoriadeprojetos.models.ProjectPartner;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectPartnerId;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjectRepository;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projetoRepository;

    @Autowired
    private CoordinatorService coordenadorService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private ProjectClient projectClient;
    @Autowired
    private ProjectMapper projetoMapper;
    @Autowired
    private ResourceOriginClient resourceOriginClient;

    public List<ProjectResponse> findAll() {
        return projetoRepository.findAll().stream()
                .map(projetoMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse findById(ProjectId id) {
        Project projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project não encontrado"));
        return projetoMapper.toResponse(projeto);
    }

    public Project salvar(ProjectRequest projetoDto) {
        System.out.println("Project dto: " + projetoDto);

        if (projetoDto.getSipacNumber() <= 0) {
            throw new MissingFields("O número SIPAC do projeto é obrigatório.");
        }
        if (projetoDto.getSipacYear() <= 0) {
            throw new MissingFields("O ano SIPAC do projeto é obrigatório.");
        }
        if (projetoDto.getContractAccount() == null || projetoDto.getContractAccount().isBlank()) {
            throw new MissingFields("Conta contrato é obrigatório.");
        }
        if (projetoDto.getCoordinatorId() == null) {
            throw new MissingFields("Coordenador é obrigatório.");
        }
        if (projetoDto.getPartnerIds() == null || projetoDto.getPartnerIds().isEmpty()) {
            throw new MissingFields("Pelo menos um parceiro é obrigatório.");
        }
        Coordinator coordenador = coordenadorService.findById(projetoDto.getCoordinatorId())
                .orElseThrow(() -> new EntityNotFound("Coordenador não encontrado"));

        Project projeto = projetoMapper.toEntity(projetoDto, coordenador);

        ProjectId projetoId = projeto.getId();
        List<ProjectPartner> projetoParceiros = projetoDto.getPartnerIds().stream().map(parceiroReq -> {

            if (parceiroReq.getParceiroId() == null) {
                throw new MissingFields("ID do Partner é obrigatório.");
            }
            UUID parceiroId = parceiroReq.getParceiroId();

            Partner parceiro = partnerService.findById(parceiroId)
                    .orElseThrow(() -> new EntityNotFound("Partner com ID " + parceiroId + " não encontrado"));

            ProjectPartner projetoParceiro = new ProjectPartner();
            projetoParceiro.setId(new ProjectPartnerId(projetoId, parceiroId));
            projetoParceiro.setProject(projeto);
            projetoParceiro.setPartner(parceiro);
            if (parceiroReq.getNumeroFunpec() != null)
                projetoParceiro.setFunpecNumber(parceiroReq.getNumeroFunpec());

            return projetoParceiro;

        }).collect(Collectors.toList());
        projeto.setPartners(projetoParceiros);
        return projetoRepository.save(projeto);
    }

    public ProjectResponse buscarNaApi(long numeroSipac, long anoSipac) {
        ProjetoApiResponse projetos = projectClient.findByNumberAndYear(numeroSipac, anoSipac);

        return tratarProjeto(projetos);
    }

    private ProjectResponse tratarProjeto(ProjetoApiResponse projeto) {
        System.out.println("tratar projeto: " + projeto.toString());
        Coordinator coordenador = coordenadorService.findOrCreateBySiape(projeto.getSiapeCoordenador());
        List<Partner> parceiros = verificarEProcessarParceiros(
                resourceOriginClient.findPartners(projeto.getIdProjeto()),
                resourceOriginClient.findIncomeSources(projeto.getIdProjeto()));

        return projetoMapper.toResponse(projeto, coordenador, parceiros);

    }

    public List<Partner> verificarParceiros(List<ParceiroApiResponse> listaDeParceiros,
            List<OrigemRecursoApiResponse> origensRecusos) {
        List<Partner> parceiros = new ArrayList<>();

        for (ParceiroApiResponse parceiroApi : listaDeParceiros) {
            Partner parceiro;
            Optional<Partner> parceiroOpt = partnerService.findByIdParticipe(parceiroApi.getIdParticipe());

            if (parceiroOpt.isPresent()) {
                parceiro = parceiroOpt.get();
            } else {
                parceiroOpt = partnerService.findByNome(parceiroApi.getNomeParticipe());
                if (parceiroOpt.isPresent()) {
                    parceiro = parceiroOpt.get();
                } else {
                    parceiro = new Partner(parceiroApi.getIdParticipe(), parceiroApi.getNomeParticipe());
                }
            }

            for (OrigemRecursoApiResponse origemRecurso : origensRecusos) {
                origemRecurso.toString();
                if (parceiro.getName() != null && parceiro.getName().equals(origemRecurso.getNomeFinanciador())) {
                    FundingType tipo = FundingType.fromName(origemRecurso.getNomeEsferaOrigemRecurso());
                    parceiro.setFundingType(tipo);
                }
            }
            parceiros.add(partnerService.save(parceiro));
        }

        return parceiros;
    }

    public Coordinator verificarCoordenador(Long siapeCoordenador, String nomeCoordenador) {
        Optional<Coordinator> coordenadorOpt = coordenadorService.findById(siapeCoordenador);

        Coordinator coordenador;
        if (coordenadorOpt.isPresent()) {
            coordenador = coordenadorOpt.get();
        } else {

            Coordinator coordenadorApi = coordenadorService.buscarCoordenadorApi(siapeCoordenador);

            if (coordenadorApi != null) {
                coordenador = coordenadorService.save(coordenadorApi);

            } else {
                coordenador = coordenadorService
                        .save(new Coordinator(siapeCoordenador, nomeCoordenador));
            }
        }

        return coordenador;
    }

    public void delete(long numeroSipac, long anoSipac) {
        Project projetoParaDeletar = projetoRepository.findById(new ProjectId(numeroSipac, anoSipac))
                .orElseThrow(() -> new EntityNotFound("Projeto " + numeroSipac + "/" + anoSipac + " não encontrado."));

        projetoRepository.delete(projetoParaDeletar);
    }

    public Project update(ProjectId id, ProjectRequest projetoDto) {
        Project projetoParaAtualizar = projetoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Projeto " + id.getSipacNumber() + "/" + id.getSipacYear()
                        + " não encontrado para atualização."));

        validarAlteracaoFunpecComBolsistas(projetoDto, projetoParaAtualizar);

        if (projetoDto.getCoordinatorId() == null) {
            throw new MissingFields("Coordenador é obrigatório.");
        }
        Coordinator novoCoordenador = coordenadorService.findById(projetoDto.getCoordinatorId())
                .orElseThrow(() -> new EntityNotFound(
                        "Coordenador com ID " + projetoDto.getCoordinatorId() + " não encontrado."));

        projetoParaAtualizar.setTitle(projetoDto.getTitle());
        projetoParaAtualizar.setDescription(projetoDto.getDescription());
        projetoParaAtualizar.setContractAccount(projetoDto.getContractAccount());
        projetoParaAtualizar.setValue(projetoDto.getValue());
        projetoParaAtualizar.setStartDate(projetoDto.getStartDate());
        projetoParaAtualizar.setEndDate(projetoDto.getEndDate());
        projetoParaAtualizar.setStatus(projetoDto.getStatus());
        projetoParaAtualizar.setCategory(projetoDto.getCategory());
        projetoParaAtualizar.setIsResidency(projetoDto.isResidency());
        projetoParaAtualizar.setIsInformaticLaw(projetoDto.isInformaticLaw());
        projetoParaAtualizar.setIsEmbrapii(projetoDto.isEmbrapii());
        projetoParaAtualizar.setIsSebrae(projetoDto.isSebrae());
        projetoParaAtualizar.setCoordinator(novoCoordenador);

        projetoParaAtualizar.getPartners().clear();

        if (projetoDto.getPartnerIds() != null && !projetoDto.getPartnerIds().isEmpty()) {
            List<ProjectPartner> novosProjetoParceiros = prepararListaParceiros(projetoDto, projetoParaAtualizar);
            projetoParaAtualizar.getPartners().addAll(novosProjetoParceiros);
        }

        return projetoRepository.save(projetoParaAtualizar);
    }

    private List<ProjectPartner> prepararListaParceiros(ProjectRequest projetoDto, Project projeto) {
        return projetoDto.getPartnerIds().stream().map(parceiroReq -> {
            if (parceiroReq.getParceiroId() == null) {
                throw new MissingFields("ID do Partner é obrigatório.");
            }
            UUID parceiroId = parceiroReq.getParceiroId();
            Partner parceiro = partnerService.findById(parceiroId)
                    .orElseThrow(() -> new EntityNotFound("Partner com ID " + parceiroId + " não encontrado"));

            ProjectPartner projetoParceiro = new ProjectPartner();
            ProjectPartnerId projetoParceiroId = new ProjectPartnerId(projeto.getId(), parceiro.getId());
            projetoParceiro.setId(projetoParceiroId);
            projetoParceiro.setProject(projeto);
            projetoParceiro.setPartner(parceiro);
            projetoParceiro.setFunpecNumber(parceiroReq.getNumeroFunpec());

            return projetoParceiro;
        }).collect(Collectors.toList());
    }

    private void validarAlteracaoFunpecComBolsistas(ProjectRequest projetoDto, Project projetoAtual) {
        if (projetoDto.getPartnerIds() == null || projetoDto.getPartnerIds().isEmpty()) {
            return;
        }

        Map<UUID, ProjectPartner> parceirosAtuaisMap = projetoAtual.getPartners().stream()
                .collect(Collectors.toMap(p -> p.getPartner().getId(), Function.identity()));

        for (ProjetoParceiroRequest parceiroReq : projetoDto.getPartnerIds()) {
            ProjectPartner parceiroAtual = parceirosAtuaisMap.get(parceiroReq.getParceiroId());

            if (parceiroAtual != null) {
                long funpecAtual = parceiroAtual.getFunpecNumber();
                long funpecNovo = parceiroReq.getNumeroFunpec();

                boolean funpecFoiAlterado = !Objects.equals(funpecAtual, funpecNovo);

                if (funpecFoiAlterado) {
                    boolean temBolsistas = !parceiroAtual.getScholarshipHolders().isEmpty();

                    if (temBolsistas) {
                        throw new ResourceConflict(
                                "Não é possível alterar o Número Funpec do parceiro '"
                                        + parceiroAtual.getPartner().getName()
                                        + "' pois ele já possui bolsistas associados.");
                    }
                }
            }
        }
    }

    public ProjectResponse salvarProjetoPorProcesso(long numeroSipac, long anoSipac) {
        ProjectResponse projeto = buscarNaApi(numeroSipac, anoSipac);
        ProjectRequest projetoRequest = projetoMapper.toRequest(projeto);
        return projetoMapper.toResponse(salvar(projetoRequest));
    }

    private List<Partner> verificarEProcessarParceiros(List<ParceiroApiResponse> listaDeParceirosApi,
            List<OrigemRecursoApiResponse> origensRecursosApi) {

        Map<String, OrigemRecursoApiResponse> origensMap = origensRecursosApi.stream()
                .collect(Collectors.toMap(OrigemRecursoApiResponse::getNomeFinanciador, Function.identity(),
                        (o1, o2) -> o1));

        return listaDeParceirosApi.stream().map(parceiroApi -> {
            Partner parceiro = partnerService.findOrCreateFromApi(parceiroApi);

            OrigemRecursoApiResponse origem = origensMap.get(parceiro.getName());
            if (origem != null) {
                FundingType tipo = FundingType.fromName(origem.getNomeEsferaOrigemRecurso());
                parceiro.setFundingType(tipo);
            }
            return partnerService.save(parceiro);
        }).collect(Collectors.toList());
    }
}