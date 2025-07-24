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

            System.out.println(response.toString());

            Coordenador coordenador = new Coordenador(response.getSiape(), response.getNome(), response.getEmail(),
                    response.getUnidade());
            return coordenador;
        } else {
            return null;
        }
    }
}