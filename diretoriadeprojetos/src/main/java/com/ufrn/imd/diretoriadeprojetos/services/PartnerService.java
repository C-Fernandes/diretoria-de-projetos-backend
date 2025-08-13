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
import com.ufrn.imd.diretoriadeprojetos.models.Partner;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;
import com.ufrn.imd.diretoriadeprojetos.repository.PartnerRepository;

import jakarta.transaction.Transactional;

@Service
public class PartnerService {

    @Autowired
    private PartnerRepository partnerRepository;

    public List<ParceiroResponse> findAll() {
        List<Partner> parceiros = partnerRepository.findAll();

        return parceiros.stream().map(parceiro -> {

            ParceiroResponse response = new ParceiroResponse();

            response.setId(parceiro.getId());
            response.setCnpj(parceiro.getCnpj());
            response.setNome(parceiro.getName());

            if (parceiro.getFundingType() != null) {
                response.setTipoFinanciamento(parceiro.getFundingType().name());
            }

            if (parceiro.getProjects() != null) {
                List<ProjectId> projetoIds = parceiro.getProjects().stream()
                        .map(projetoParceiro -> projetoParceiro.getProject().getId())
                        .collect(Collectors.toList());

                response.setProjetos(projetoIds);
            } else {
                response.setProjetos(new ArrayList<>());
            }
            return response;

        }).collect(Collectors.toList());
    }

    public Optional<Partner> findById(UUID id) {
        return partnerRepository.findById(id);
    }

    public Partner save(Partner parceiro) {
        return partnerRepository.save(parceiro);
    }

    public Partner update(UUID id, Partner parceiroAtualizado) {
        return partnerRepository.findById(id).map(parceiro -> {
            parceiro.setName(parceiroAtualizado.getName());
            parceiro.setFundingType(parceiroAtualizado.getFundingType());
            parceiro.setProjects(parceiroAtualizado.getProjects());
            return partnerRepository.save(parceiro);
        }).orElseThrow(() -> new RuntimeException("Parceiro não encontrado"));
    }

    public void delete(UUID id) {
        partnerRepository.deleteById(id);
    }

    public Optional<Partner> findByNome(String nomeParticipe) {
        return partnerRepository.findByName(nomeParticipe);
    }

    public Optional<Partner> findByIdParticipe(Long idParticipe) {
        return partnerRepository.findByParticipeId(idParticipe);
    }

    public boolean checarEmbrapii(Partner parceiroEmbrapii) {
        Optional<Partner> parceiroOptional = findByIdParticipe(parceiroEmbrapii.getParticipeId());

        if (parceiroOptional.isPresent() && parceiroOptional.get().getParticipeId() == 21009L) {
            return true;
        }

        if (findByNome(parceiroEmbrapii.getName())
                .equals("ASSOCIAÇÃO BRASILEIRA DE PESQUISA E INOVAÇÃO INDUSTRIAL  EMBRAPII")) {
            return true;
        }

        return false;
    }

    @Transactional
    public Partner findOrCreateFromApi(ParceiroApiResponse parceiroApi) {
        if (parceiroApi == null)
            throw new IllegalArgumentException("Dados do parceiro da API não podem ser nulos.");
        Optional<Partner> parceiroOpt = findByIdParticipe(parceiroApi.getIdParticipe());
        if (parceiroOpt.isPresent()) {
            return parceiroOpt.get();
        }
        parceiroOpt = findByNome(parceiroApi.getNomeParticipe());
        if (parceiroOpt.isPresent()) {
            return parceiroOpt.get();
        }
        Partner novoParceiro = new Partner(parceiroApi.getIdParticipe(), parceiroApi.getNomeParticipe());
        return partnerRepository.save(novoParceiro);
    }

}
