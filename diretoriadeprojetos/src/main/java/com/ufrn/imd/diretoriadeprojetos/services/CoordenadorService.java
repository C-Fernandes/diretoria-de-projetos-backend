package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.clients.CoordenadorClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.CoordenadorApiResponse;
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
        return coordenadorRepository.save(coordenador);
    }

    public List<Coordenador> listarTodos() {
        return coordenadorRepository.findAll();
    }

    public Optional<Coordenador> buscarPorMatricula(Long matricula) {
        return coordenadorRepository.findById(matricula);
    }

    public Coordenador atualizar(Long matricula, Coordenador coordenadorAtualizado) {
        return coordenadorRepository.findById(matricula).map(coordenador -> {
            coordenador.setNome(coordenadorAtualizado.getNome());
            coordenador.setEmail(coordenadorAtualizado.getEmail());
            coordenador.setUnidadeAcademica(coordenadorAtualizado.getUnidadeAcademica());
            coordenador.setProjetos(coordenadorAtualizado.getProjetos());
            return coordenadorRepository.save(coordenador);
        }).orElseThrow(() -> new RuntimeException("Coordenador não encontrado"));
    }

    public void deletar(Long matricula) {
        coordenadorRepository.deleteById(matricula);
    }

    public Optional<Coordenador> findById(Long coordenadorId) {
        return coordenadorRepository.findById(coordenadorId);
    }

    public Coordenador buscarCoordenadorApi(Long siape) {

        List<CoordenadorApiResponse> coordenadores = coordenadorClient.buscarPorSiape(siape);
        if (!coordenadores.isEmpty()) {
            CoordenadorApiResponse response = coordenadores.get(0);

            System.out.println(response.toString());

            Coordenador coordenador = new Coordenador(response.getIdDocente(), response.getNome(), response.getEmail(),
                    response.getUnidade());
            return coordenador;
        } else {
            return null;
        }
    }
}