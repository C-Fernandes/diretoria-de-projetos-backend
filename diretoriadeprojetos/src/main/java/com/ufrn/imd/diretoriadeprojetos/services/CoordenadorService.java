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
        if (coordenadorRepository.existsById(coordenador.getMatricula())) {
            throw new UsedField(
                    "A matrícula '" + coordenador.getMatricula() + "' já está em uso.");
        }

        return coordenadorRepository.save(coordenador);
    }

    public List<Coordenador> findAll() {
        return coordenadorRepository.findAll();
    }

    public Optional<Coordenador> buscarPorMatricula(Long matricula) {
        return coordenadorRepository.findById(matricula);
    }

    public Coordenador update(Long matricula, Coordenador coordenadorAtualizado) {

        Coordenador coordenadorParaAtualizar = coordenadorRepository.findById(matricula)
                .orElseThrow(() -> new MissingFields(
                        "Coordenador com matrícula " + matricula + " não encontrado."));

        validarEmail(coordenadorAtualizado.getEmail(), matricula);
        coordenadorParaAtualizar.setNome(coordenadorAtualizado.getNome());
        coordenadorParaAtualizar.setEmail(coordenadorAtualizado.getEmail());
        coordenadorParaAtualizar.setUnidadeAcademica(coordenadorAtualizado.getUnidadeAcademica());
        coordenadorParaAtualizar.setProjetos(coordenadorAtualizado.getProjetos());

        return coordenadorRepository.save(coordenadorParaAtualizar);
    }

    private void validarEmail(String email, Long matriculaExcluidaDaBusca) {
        coordenadorRepository.findByEmail(email)
                .ifPresent(coordenadorExistente -> {
                    if (!coordenadorExistente.getMatricula().equals(matriculaExcluidaDaBusca)) {
                        throw new UsedField(
                                "O e-mail '" + email + "' já está em uso por outro coordenador.");
                    }
                });
    }

    public void deletar(Long matricula) {
        Coordenador coordenador = coordenadorRepository.findById(matricula)
                .orElseThrow(() -> new MissingFields(
                        "Coordenador com matrícula " + matricula + " não encontrado. Impossível deletar."));

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