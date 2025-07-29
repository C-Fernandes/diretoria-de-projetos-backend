package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.clients.CoordenadorClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.CoordenadorApiResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.MissingFields;
import com.ufrn.imd.diretoriadeprojetos.errors.ResourceConflict;
import com.ufrn.imd.diretoriadeprojetos.errors.UsedField;
import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.repository.CoordenadorRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CoordenadorService {
    @Autowired
    private CoordenadorRepository coordenadorRepository;

    @Autowired
    private CoordenadorClient coordenadorClient;

    public Coordenador save(Coordenador coordenador) {
        if (coordenadorRepository.existsById(coordenador.getSiape())) {
            throw new UsedField(
                    "O siape '" + coordenador.getSiape() + "' já está em uso.");
        }
        return coordenadorRepository.save(coordenador);
    }

    public List<Coordenador> findAll() {
        return coordenadorRepository.findAll();
    }

    public Optional<Coordenador> buscarPorSiape(Long siape) {
        return coordenadorRepository.findById(siape);
    }

    public Coordenador update(Long siape, Coordenador coordenadorAtualizado) {
        Coordenador coordenadorParaAtualizar = coordenadorRepository.findById(siape)
                .orElseThrow(() -> new MissingFields(
                        "Coordenador com matrícula " + siape + " não encontrado."));
        validarEmail(coordenadorAtualizado.getEmail(), siape);
        coordenadorParaAtualizar.setNome(coordenadorAtualizado.getNome());
        coordenadorParaAtualizar.setEmail(coordenadorAtualizado.getEmail());
        coordenadorParaAtualizar.setUnidadeAcademica(coordenadorAtualizado.getUnidadeAcademica());
        coordenadorParaAtualizar.setProjetos(coordenadorAtualizado.getProjetos());
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
        Coordenador coordenador = coordenadorRepository.findById(siape)
                .orElseThrow(() -> new MissingFields(
                        "Coordenador com matrícula " + siape + " não encontrado. Impossível deletar."));

        if (!coordenador.getProjetos().isEmpty()) {
            throw new ResourceConflict(
                    "O coordenador não pode ser excluído, pois está associado a projeto(s).");
        }

        coordenadorRepository.delete(coordenador);
    }

    public Optional<Coordenador> findById(Long coordenadorId) {
        return coordenadorRepository.findById(coordenadorId);
    }

    public Coordenador buscarCoordenadorApi(Long siape) {
        List<CoordenadorApiResponse> coordenadores = coordenadorClient.buscarPorSiape(siape);
        if (!coordenadores.isEmpty()) {
            CoordenadorApiResponse response = coordenadores.get(0);
            Coordenador coordenador = new Coordenador(response.getSiape(), response.getNome(), response.getEmail(),
                    response.getUnidade());
            return coordenador;
        } else {
            return null;
        }
    }

    @Transactional
    public Coordenador findOrCreateBySiape(Long siape) {
        // --- Início do método ---
        System.out.println(">>> [START] findOrCreateBySiape para o SIAPE: " + siape);

        // --- Passo 1: Busca no banco de dados local ---
        System.out.println(">>> [DB_SEARCH] Buscando coordenador no banco de dados local...");
        Optional<Coordenador> coordenadorOpt = findById(siape);

        if (coordenadorOpt.isPresent()) {
            // --- Caminho 1: Coordenador foi encontrado localmente ---
            System.out.println(">>> [DB_FOUND] SUCESSO: Coordenador encontrado no banco de dados local.");
            System.out.println(">>> [END] Retornando coordenador existente.");
            return coordenadorOpt.get();
        } else {
            // --- Caminho 2: Coordenador NÃO foi encontrado localmente ---
            System.out.println(
                    ">>> [DB_NOT_FOUND] INFO: Coordenador não encontrado localmente. Partindo para a busca na API externa...");

            // --- Passo 2: Busca na API externa ---
            Coordenador coordenadorDaApi = buscarCoordenadorApi(siape);

            if (coordenadorDaApi == null) {
                // --- Caminho 2a: Coordenador também NÃO foi encontrado na API ---
                System.out.println(">>> [API_NOT_FOUND] ERRO: Coordenador também não foi encontrado na API externa.");
                System.out.println(">>> [END] Lançando EntityNotFoundException.");
                throw new EntityNotFoundException(
                        "Coordenador com SIAPE " + siape + " não foi encontrado no banco de dados nem na API externa.");
            }

            // --- Caminho 2b: Coordenador foi encontrado na API ---
            System.out.println(">>> [API_FOUND] SUCESSO: Coordenador encontrado na API externa.");
            // Imprimir os dados do objeto recebido é muito útil para depuração
            System.out.println(">>> [API_DATA] Dados recebidos: " + coordenadorDaApi.toString());

            // --- Passo 3: Salva o novo coordenador no banco ---
            System.out.println(">>> [DB_SAVE] Salvando o novo coordenador no banco de dados...");
            Coordenador coordenadorSalvo = coordenadorRepository.save(coordenadorDaApi);
            System.out.println(
                    ">>> [DB_SAVE_SUCCESS] SUCESSO: Novo coordenador salvo com o id: " + coordenadorSalvo.getSiape());
            System.out.println(">>> [END] Retornando novo coordenador salvo.");
            return coordenadorSalvo;
        }
    }
}