package com.ufrn.imd.diretoriadeprojetos.dtos.mapper;

import org.springframework.stereotype.Component;

import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Parceiro;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component // Marca esta classe como um componente Spring para poder ser injetada
public class ProjetoMapper {

    /**
     * Converte uma entidade Projeto para um DTO ProjetoResponse.
     * Usado para listar projetos.
     */
    public ProjetoResponse toResponse(Projeto projeto) {
        if (projeto == null) {
            return null;
        }

        ProjetoResponse response = new ProjetoResponse();
        response.setNumeroSipac(projeto.getId().getNumeroSipac());
        response.setAnoSipac(projeto.getId().getAnoSipac());
        response.setTitulo(projeto.getTitulo());
        response.setLeiDeInformatica(projeto.getLeiDeInformatica());
        response.setValor(projeto.getValor());
        response.setDataInicio(projeto.getDataInicio());
        response.setDataFim(projeto.getDataFim());
        response.setContaContrato(projeto.getContaContrato());
        response.setStatus(projeto.getStatus());
        response.setDescricao(projeto.getDescricao());
        response.setCoordenador(projeto.getCoordenador()); // Pode ser necessário um CoordenadorMapper também

        // Mapeia a lista de parceiros para uma lista de IDs
        if (projeto.getParceiros() != null) {
            List<UUID> parceirosIds = projeto.getParceiros().stream()
                    .map(projetoParceiro -> projetoParceiro.getParceiro().getId())
                    .collect(Collectors.toList());
            response.setParceirosId(parceirosIds);
        }

        return response;
    }

    /**
     * Converte um DTO ProjetoRequest para uma entidade Projeto.
     * Ideal para o método de salvar. Note que ele não preenche os parceiros,
     * isso será feito no serviço que tem acesso aos repositórios.
     */
    public Projeto toEntity(ProjetoRequest projetoDto, Coordenador coordenador) {
        if (projetoDto == null) {
            return null;
        }

        Projeto projeto = new Projeto();
        ProjetoId projetoId = new ProjetoId(projetoDto.getNumeroSipac(), projetoDto.getAnoSipac());

        projeto.setId(projetoId);
        projeto.setTitulo(projetoDto.getTitulo());
        projeto.setLeiDeInformatica(projetoDto.isLeiInformatica());
        projeto.setResidencia(projetoDto.isResidencia());
        projeto.setValor(projetoDto.getValor());
        projeto.setContaContrato(projetoDto.getContaContrato());
        projeto.setDataInicio(projetoDto.getDataInicio());
        projeto.setDataFim(projetoDto.getDataFim());
        projeto.setStatus(projetoDto.getStatus());
        projeto.setDescricao(projetoDto.getDescricao());
        projeto.setCoordenador(coordenador); // Usa o coordenador que já foi buscado no serviço

        return projeto;
    }

    /**
     * Converte um objeto vindo de uma API externa para o nosso DTO de resposta.
     * Usado para tratar os dados da busca na API externa.
     */
    public ProjetoResponse toResponse(ProjetoApiResponse projetoApi, Coordenador coordenador,
            List<Parceiro> parceiros) {
        if (projetoApi == null) {
            return null;
        }

        ProjetoResponse respostaProjeto = new ProjetoResponse();
        respostaProjeto.setContaContrato(projetoApi.getNumeroFormatado());
        respostaProjeto.setCoordenador(coordenador); // Usa o coordenador já verificado
        respostaProjeto.setNumeroSipac(projetoApi.getNumero().toString());
        respostaProjeto.setAnoSipac(projetoApi.getAno().toString());
        respostaProjeto.setDescricao(projetoApi.getJustificativa());
        respostaProjeto.setTitulo(projetoApi.getTituloProjeto());
        respostaProjeto.setDataFim(projetoApi.getFimExecucao());
        respostaProjeto.setDataInicio(projetoApi.getInicioExecucao());
        respostaProjeto.setStatus(projetoApi.getDescricaoStatus());
        respostaProjeto.setValor(projetoApi.getValorProjeto());

        if (parceiros != null) {
            List<UUID> listaParceirosIds = parceiros.stream()
                    .map(Parceiro::getId)
                    .collect(Collectors.toList());
            respostaProjeto.setParceirosId(listaParceirosIds);
        }

        return respostaProjeto;
    }
}