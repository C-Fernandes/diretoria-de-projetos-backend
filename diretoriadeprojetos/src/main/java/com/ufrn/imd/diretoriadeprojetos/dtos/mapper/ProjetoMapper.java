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

@Component
public class ProjetoMapper {

    public ProjetoResponse toResponse(Projeto projeto) {
        if (projeto == null) {
            return null;
        }

        ProjetoResponse response = new ProjetoResponse();
        response.setNumeroSipac(projeto.getId().getNumeroSipac());
        response.setAnoSipac(projeto.getId().getAnoSipac());
        response.setTitulo(projeto.getTitulo());
        response.setLeiDeInformatica(projeto.getLeiDeInformatica());
        response.setEmbrapii(projeto.getEmbrapii());
        response.setResidencia(projeto.getResidencia());
        response.setSebrae(projeto.getSebrae());
        response.setValor(projeto.getValor());
        response.setDataInicio(projeto.getDataInicio());
        response.setDataFim(projeto.getDataFim());
        response.setContaContrato(projeto.getContaContrato());
        response.setStatus(projeto.getStatus());
        response.setDescricao(projeto.getDescricao());
        response.setCoordenador(projeto.getCoordenador());
        response.setCategoria(projeto.getCategoria());

        if (projeto.getParceiros() != null) {
            List<UUID> parceirosIds = projeto.getParceiros().stream()
                    .map(projetoParceiro -> projetoParceiro.getParceiro().getId())
                    .collect(Collectors.toList());
            response.setParceirosId(parceirosIds);
        }

        return response;
    }

    public Projeto toEntity(ProjetoRequest projetoDto, Coordenador coordenador) {
        if (projetoDto == null) {
            return null;
        }

        Projeto projeto = new Projeto();
        ProjetoId projetoId = new ProjetoId(projetoDto.getNumeroSipac(), projetoDto.getAnoSipac());

        projeto.setId(projetoId);
        projeto.setTitulo(projetoDto.getTitulo());
        projeto.setLeiDeInformatica(projetoDto.isLeiDeInformatica());
        projeto.setResidencia(projetoDto.isResidencia());
        projeto.setSebrae(projetoDto.isSebrae());
        projeto.setEmbrapii(projetoDto.isEmbrapii());
        projeto.setCategoria(projetoDto.getCategoria());
        projeto.setValor(projetoDto.getValor());
        projeto.setContaContrato(projetoDto.getContaContrato());
        projeto.setDataInicio(projetoDto.getDataInicio());
        projeto.setDataFim(projetoDto.getDataFim());
        projeto.setStatus(projetoDto.getStatus());
        projeto.setDescricao(projetoDto.getDescricao());
        projeto.setCoordenador(coordenador);

        return projeto;
    }

    public ProjetoResponse toResponse(ProjetoApiResponse projetoApi, Coordenador coordenador,
            List<Parceiro> parceiros) {
        if (projetoApi == null) {
            return null;
        }

        ProjetoResponse respostaProjeto = new ProjetoResponse();
        respostaProjeto.setContaContrato(projetoApi.getNumeroFormatado());
        respostaProjeto.setCoordenador(coordenador);
        respostaProjeto.setNumeroSipac(projetoApi.getNumero().toString());
        respostaProjeto.setAnoSipac(projetoApi.getAno().toString());
        respostaProjeto.setDescricao(projetoApi.getJustificativa());
        respostaProjeto.setTitulo(projetoApi.getTituloProjeto());
        respostaProjeto.setDataFim(projetoApi.getFimExecucao());
        respostaProjeto.setDataInicio(projetoApi.getInicioExecucao());
        respostaProjeto.setStatus(projetoApi.getDescricaoStatus());
        respostaProjeto.setValor(projetoApi.getValorProjeto());
        String tipoProjeto = projetoApi.getTipoProjeto();
        String tipoConvenio = projetoApi.getTipoConvenio();

        String novaCategoria;
        boolean tipoProjetoValido = tipoProjeto != null && !tipoProjeto.isBlank();
        boolean tipoConvenioValido = tipoConvenio != null && !tipoConvenio.isBlank();

        if (tipoProjetoValido && tipoConvenioValido) {
            novaCategoria = String.format("%s %s", tipoProjeto, tipoConvenio);
        } else if (tipoProjetoValido) {
            novaCategoria = tipoProjeto;
        } else if (tipoConvenioValido) {
            novaCategoria = tipoConvenio;
        } else {
            novaCategoria = "Categoria não definida";
        }

        respostaProjeto.setCategoria(novaCategoria);
        if (parceiros != null) {
            List<UUID> listaParceirosIds = parceiros.stream()
                    .map(Parceiro::getId)
                    .collect(Collectors.toList());
            respostaProjeto.setParceirosId(listaParceirosIds);
        }

        return respostaProjeto;
    }
}