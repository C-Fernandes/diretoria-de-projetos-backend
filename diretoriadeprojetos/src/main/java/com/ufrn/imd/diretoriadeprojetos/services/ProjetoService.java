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

import com.ufrn.imd.diretoriadeprojetos.clients.OrigemRecursoClient;
import com.ufrn.imd.diretoriadeprojetos.clients.ProjetoClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.mapper.ProjetoMapper;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoParceiroRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.OrigemRecursoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.enums.TipoFinanciamento;
import com.ufrn.imd.diretoriadeprojetos.errors.EntityNotFound;
import com.ufrn.imd.diretoriadeprojetos.errors.MissingFields;
import com.ufrn.imd.diretoriadeprojetos.errors.ResourceConflict;
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

    public List<ProjetoResponse> findAll() {
        return projetoRepository.findAll().stream()
                .map(projetoMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProjetoResponse findById(ProjetoId id) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        return projetoMapper.toResponse(projeto);
    }

    public Projeto salvar(ProjetoRequest projetoDto) {
        System.out.println("Projeto dto: " + projetoDto);

        if (projetoDto.getNumeroSipac() >= 0) {
            throw new MissingFields("O número SIPAC do projeto é obrigatório.");
        }
        if (projetoDto.getAnoSipac() >= 0) {
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

    public void delete(long numeroSipac, long anoSipac) {
        Projeto projetoParaDeletar = projetoRepository.findById(new ProjetoId(numeroSipac, anoSipac))
                .orElseThrow(() -> new EntityNotFound("Projeto " + numeroSipac + "/" + anoSipac + " não encontrado."));

        projetoRepository.delete(projetoParaDeletar);
    }

    public Projeto update(ProjetoId id, ProjetoRequest projetoDto) {
        Projeto projetoParaAtualizar = projetoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Projeto " + id.getNumeroSipac() + "/" + id.getAnoSipac()
                        + " não encontrado para atualização."));

        validarAlteracaoFunpecComBolsistas(projetoDto, projetoParaAtualizar);

        if (projetoDto.getCoordenadorId() == null) {
            throw new MissingFields("Coordenador é obrigatório.");
        }
        Coordenador novoCoordenador = coordenadorService.findById(projetoDto.getCoordenadorId())
                .orElseThrow(() -> new EntityNotFound(
                        "Coordenador com ID " + projetoDto.getCoordenadorId() + " não encontrado."));

        projetoParaAtualizar.setTitulo(projetoDto.getTitulo());
        projetoParaAtualizar.setDescricao(projetoDto.getDescricao());
        projetoParaAtualizar.setContaContrato(projetoDto.getContaContrato());
        projetoParaAtualizar.setValor(projetoDto.getValor());
        projetoParaAtualizar.setDataInicio(projetoDto.getDataInicio());
        projetoParaAtualizar.setDataFim(projetoDto.getDataFim());
        projetoParaAtualizar.setStatus(projetoDto.getStatus());
        projetoParaAtualizar.setCategoria(projetoDto.getCategoria());
        projetoParaAtualizar.setResidencia(projetoDto.isResidencia());
        projetoParaAtualizar.setLeiDeInformatica(projetoDto.isLeiDeInformatica());
        projetoParaAtualizar.setEmbrapii(projetoDto.isEmbrapii());
        projetoParaAtualizar.setSebrae(projetoDto.isSebrae());
        projetoParaAtualizar.setCoordenador(novoCoordenador);

        projetoParaAtualizar.getParceiros().clear();

        if (projetoDto.getParceirosId() != null && !projetoDto.getParceirosId().isEmpty()) {
            List<ProjetoParceiro> novosProjetoParceiros = prepararListaParceiros(projetoDto, projetoParaAtualizar);
            projetoParaAtualizar.getParceiros().addAll(novosProjetoParceiros);
        }

        return projetoRepository.save(projetoParaAtualizar);
    }

    private List<ProjetoParceiro> prepararListaParceiros(ProjetoRequest projetoDto, Projeto projeto) {
        return projetoDto.getParceirosId().stream().map(parceiroReq -> {
            if (parceiroReq.getParceiroId() == null) {
                throw new MissingFields("ID do Parceiro é obrigatório.");
            }
            UUID parceiroId = parceiroReq.getParceiroId();
            Parceiro parceiro = parceiroService.findById(parceiroId)
                    .orElseThrow(() -> new EntityNotFound("Parceiro com ID " + parceiroId + " não encontrado"));

            ProjetoParceiro projetoParceiro = new ProjetoParceiro();
            ProjetoParceiroId projetoParceiroId = new ProjetoParceiroId(projeto.getId(), parceiro.getId());
            projetoParceiro.setId(projetoParceiroId);
            projetoParceiro.setProjeto(projeto);
            projetoParceiro.setParceiro(parceiro);
            projetoParceiro.setNumeroFunpec(parceiroReq.getNumeroFunpec());

            return projetoParceiro;
        }).collect(Collectors.toList());
    }

    private void validarAlteracaoFunpecComBolsistas(ProjetoRequest projetoDto, Projeto projetoAtual) {
        if (projetoDto.getParceirosId() == null || projetoDto.getParceirosId().isEmpty()) {
            return;
        }

        Map<UUID, ProjetoParceiro> parceirosAtuaisMap = projetoAtual.getParceiros().stream()
                .collect(Collectors.toMap(p -> p.getParceiro().getId(), Function.identity()));

        for (ProjetoParceiroRequest parceiroReq : projetoDto.getParceirosId()) {
            ProjetoParceiro parceiroAtual = parceirosAtuaisMap.get(parceiroReq.getParceiroId());

            if (parceiroAtual != null) {
                long funpecAtual = parceiroAtual.getNumeroFunpec();
                long funpecNovo = parceiroReq.getNumeroFunpec();

                boolean funpecFoiAlterado = !Objects.equals(funpecAtual, funpecNovo);

                if (funpecFoiAlterado) {
                    boolean temBolsistas = !parceiroAtual.getBolsistas().isEmpty();

                    if (temBolsistas) {
                        throw new ResourceConflict(
                                "Não é possível alterar o Número Funpec do parceiro '"
                                        + parceiroAtual.getParceiro().getNome()
                                        + "' pois ele já possui bolsistas associados.");
                    }
                }
            }
        }
    }
}