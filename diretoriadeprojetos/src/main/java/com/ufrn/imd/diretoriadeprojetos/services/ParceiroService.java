package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Parceiro;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;
import com.ufrn.imd.diretoriadeprojetos.repository.ParceiroRepository;

import jakarta.transaction.Transactional;

@Service
public class ParceiroService {

    @Autowired
    private ParceiroRepository parceiroRepository;

    public List<ParceiroResponse> findAll() {
        List<Parceiro> parceiros = parceiroRepository.findAll();

        return parceiros.stream().map(parceiro -> {

            ParceiroResponse response = new ParceiroResponse();

            response.setId(parceiro.getId());
            response.setCnpj(parceiro.getCnpj());
            response.setNome(parceiro.getNome());

            if (parceiro.getTipoFinanciamento() != null) {
                response.setTipoFinanciamento(parceiro.getTipoFinanciamento().name());
            }

            if (parceiro.getProjetos() != null) {
                List<ProjetoId> projetoIds = parceiro.getProjetos().stream()
                        .map(projetoParceiro -> projetoParceiro.getProjeto().getId())
                        .collect(Collectors.toList());

                response.setProjetos(projetoIds);
            } else {
                response.setProjetos(new ArrayList<>());
            }
            return response;

        }).collect(Collectors.toList());
    }

    public Optional<Parceiro> findById(UUID id) {
        return parceiroRepository.findById(id);
    }

    public Parceiro save(Parceiro parceiro) {
        return parceiroRepository.save(parceiro);
    }

    public Parceiro update(UUID id, Parceiro parceiroAtualizado) {
        return parceiroRepository.findById(id).map(parceiro -> {
            parceiro.setNome(parceiroAtualizado.getNome());
            parceiro.setTipoFinanciamento(parceiroAtualizado.getTipoFinanciamento());
            parceiro.setProjetos(parceiroAtualizado.getProjetos());
            return parceiroRepository.save(parceiro);
        }).orElseThrow(() -> new RuntimeException("Parceiro não encontrado"));
    }

    public void delete(UUID id) {
        parceiroRepository.deleteById(id);
    }

    public Optional<Parceiro> findByNome(String nomeParticipe) {
        return parceiroRepository.findByNome(nomeParticipe);
    }

    public Optional<Parceiro> findByIdParticipe(Long idParticipe) {
        return parceiroRepository.findByIdParticipe(idParticipe);
    }

    public boolean checarEmbrapii(Parceiro parceiroEmbrapii) {
        Optional<Parceiro> parceiroOptional = findByIdParticipe(parceiroEmbrapii.getIdParticipe());

        if (parceiroOptional.isPresent() && parceiroOptional.get().getIdParticipe() == 21009L) {
            return true;
        }

        if (findByNome(parceiroEmbrapii.getNome())
                .equals("ASSOCIAÇÃO BRASILEIRA DE PESQUISA E INOVAÇÃO INDUSTRIAL  EMBRAPII")) {
            return true;
        }

        return false;
    }

    @Transactional
    public Parceiro findOrCreateFromApi(ParceiroApiResponse parceiroApi) {
        if (parceiroApi == null)
            throw new IllegalArgumentException("Dados do parceiro da API não podem ser nulos.");
        Optional<Parceiro> parceiroOpt = findByIdParticipe(parceiroApi.getIdParticipe());
        if (parceiroOpt.isPresent()) {
            return parceiroOpt.get();
        }
        parceiroOpt = findByNome(parceiroApi.getNomeParticipe());
        if (parceiroOpt.isPresent()) {
            return parceiroOpt.get();
        }
        Parceiro novoParceiro = new Parceiro(parceiroApi.getIdParticipe(), parceiroApi.getNomeParticipe());
        return parceiroRepository.save(novoParceiro);
    }

}
