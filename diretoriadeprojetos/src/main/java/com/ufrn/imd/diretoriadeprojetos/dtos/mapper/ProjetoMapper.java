package com.ufrn.imd.diretoriadeprojetos.dtos.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoParceiro;

@Component
public class ProjetoMapper {

    public Projeto toEntity(ProjetoResponse dto, Coordenador coordenador, List<ProjetoParceiro> parceiros) {
        Projeto projeto = new Projeto();

        projeto.setIdProjeto(dto.getIdProjeto());
        projeto.setNFunpec(dto.getNFunpec());
        projeto.setTitulo(dto.getTitulo());
        projeto.setSebrae(Boolean.TRUE.equals(dto.getSebrae()));
        projeto.setEmbrapii(Boolean.TRUE.equals(dto.getEmbrapii()));
        projeto.setLeiDeInformatica(Boolean.TRUE.equals(dto.getLeiDeInformatica()));
        projeto.setValor(dto.getValor());
        projeto.setDataInicio(dto.getDataInicio());
        projeto.setDataFim(dto.getDataFim());
        projeto.setContaContrato(dto.getContaContrato());
        projeto.setStatus(dto.getStatus());
        projeto.setCoordenador(coordenador);
        projeto.setParceiros(parceiros);

        return projeto;
    }

    public ProjetoResponse toResponse(Projeto projeto) {
        return ProjetoResponse.builder()
                .idProjeto(projeto.getIdProjeto())
                .nFunpec(projeto.getNFunpec())
                .titulo(projeto.getTitulo())
                .sebrae(projeto.getSebrae())
                .embrapii(projeto.getEmbrapii())
                .leiDeInformatica(projeto.getLeiDeInformatica())
                .valor(projeto.getValor())
                .dataInicio(projeto.getDataInicio())
                .dataFim(projeto.getDataFim())
                .contaContrato(projeto.getContaContrato())
                .status(projeto.getStatus())
                .coordenadorId(projeto.getCoordenador().getMatricula())
                .parceirosId(
                        projeto.getParceiros().stream()
                                .map(p -> p.getParceiro().getId())
                                .collect(Collectors.toList()))
                .build();
    }
}
