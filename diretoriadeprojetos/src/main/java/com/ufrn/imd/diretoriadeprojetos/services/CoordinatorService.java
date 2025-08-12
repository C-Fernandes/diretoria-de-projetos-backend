package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.clients.CoordinatorClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.CoordinatorApiResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.MissingFields;
import com.ufrn.imd.diretoriadeprojetos.errors.ResourceConflict;
import com.ufrn.imd.diretoriadeprojetos.errors.UsedField;
import com.ufrn.imd.diretoriadeprojetos.models.Coordinator;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.repository.CoordinatorRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CoordinatorService {
    @Autowired
    private CoordinatorRepository coordenadorRepository;

    @Autowired
    private CoordinatorClient coordenadorClient;

    public Coordinator save(Coordinator coordenador) {
        if (coordenadorRepository.existsById(coordenador.getSiape())) {
            throw new UsedField(
                    "O siape '" + coordenador.getSiape() + "' já está em uso.");
        }
        return coordenadorRepository.save(coordenador);
    }

    public List<Coordinator> findAll() {
        return coordenadorRepository.findAll();
    }

    public Optional<Coordinator> buscarPorSiape(Long siape) {
        return coordenadorRepository.findById(siape);
    }

    public Coordinator update(Long siape, Coordinator coordenadorAtualizado) {
        Coordinator coordenadorParaAtualizar = coordenadorRepository.findById(siape)
                .orElseThrow(() -> new MissingFields(
                        "Coordenador com matrícula " + siape + " não encontrado."));
        validarEmail(coordenadorAtualizado.getEmail(), siape);
        coordenadorParaAtualizar.setName(coordenadorAtualizado.getName());
        coordenadorParaAtualizar.setEmail(coordenadorAtualizado.getEmail());
        coordenadorParaAtualizar.setAcademicUnit(coordenadorAtualizado.getAcademicUnit());
        coordenadorParaAtualizar.setProjects(coordenadorAtualizado.getProjects());
        return coordenadorRepository.save(coordenadorParaAtualizar);
    }

    private void validarEmail(String email, Long siapeExcluidaDaBusca) {
        coordenadorRepository.findByEmail(email)
                .ifPresent(coordenadorExistente -> {
                    if (!coordenadorExistente.getSiape().equals(siapeExcluidaDaBusca)) {
                        throw new UsedField(
                                "O e-mail '" + email + "' já está em uso por outro coordenador.");
                    }
                });
    }

    public void deletar(Long siape) {
        Coordinator coordenador = coordenadorRepository.findById(siape)
                .orElseThrow(() -> new MissingFields(
                        "Coordenador com matrícula " + siape + " não encontrado. Impossível deletar."));

        if (!coordenador.getProjects().isEmpty()) {
            throw new ResourceConflict(
                    "O coordenador não pode ser excluído, pois está associado a projeto(s).");
        }

        coordenadorRepository.delete(coordenador);
    }

    public Optional<Coordinator> findById(Long coordenadorId) {
        return coordenadorRepository.findById(coordenadorId);
    }

    public Coordinator buscarCoordenadorApi(Long siape) {
        List<CoordinatorApiResponse> coordenadores = coordenadorClient.findBySiape(siape);
        if (!coordenadores.isEmpty()) {
            CoordinatorApiResponse response = coordenadores.get(0);
            Coordinator coordenador = new Coordinator(response.getSiape(), response.getNome(), response.getEmail(),
                    response.getUnidade());
            return coordenador;
        } else {
            return null;
        }
    }

    @Transactional
    public Coordinator findOrCreateBySiape(Long siape) {
        // --- Início do método ---
        System.out.println(">>> [START] findOrCreateBySiape para o SIAPE: " + siape);

        // --- Passo 1: Busca no banco de dados local ---
        System.out.println(">>> [DB_SEARCH] Buscando coordenador no banco de dados local...");
        Optional<Coordinator> coordenadorOpt = findById(siape);

        if (coordenadorOpt.isPresent()) {
            return coordenadorOpt.get();
        } else {
            Coordinator coordenadorDaApi = buscarCoordenadorApi(siape);

            if (coordenadorDaApi == null) {
                throw new EntityNotFoundException(
                        "Coordenador com SIAPE " + siape + " não foi encontrado no banco de dados nem na API externa.");
            }
            Coordinator coordenadorSalvo = coordenadorRepository.save(coordenadorDaApi);
            System.out.println(
                    ">>> [DB_SAVE_SUCCESS] SUCESSO: Novo coordenador salvo com o id: " + coordenadorSalvo.getSiape());
            System.out.println(">>> [END] Retornando novo coordenador salvo.");
            return coordenadorSalvo;
        }
    }
}