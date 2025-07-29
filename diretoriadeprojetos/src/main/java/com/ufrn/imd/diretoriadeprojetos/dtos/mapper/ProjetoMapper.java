package com.ufrn.imd.diretoriadeprojetos.dtos.mapper;

import org.springframework.stereotype.Component;

import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoParceiroRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.request.ProjetoRequest;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.enums.TipoFinanciamento;
import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Parceiro;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoHasParceiro;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        response.setIdProjeto(projeto.getIdProjeto());
        response.setTipoFinanciamento(encontrarTipoFinanciamento(projeto));

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
        projeto.setIdProjeto(projetoDto.getIdProjeto());

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
        respostaProjeto.setNumeroSipac(projetoApi.getNumero());
        respostaProjeto.setAnoSipac(projetoApi.getAno());
        respostaProjeto.setDescricao(projetoApi.getJustificativa());
        respostaProjeto.setTitulo(projetoApi.getTituloProjeto());
        respostaProjeto.setDataFim(projetoApi.getFimExecucao());
        respostaProjeto.setDataInicio(projetoApi.getInicioExecucao());
        respostaProjeto.setStatus(projetoApi.getDescricaoStatus());
        respostaProjeto.setValor(projetoApi.getValorProjeto());
        respostaProjeto.setIdProjeto(projetoApi.getIdProjeto());
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

    public ProjetoRequest toRequest(ProjetoResponse response) {
        if (response == null) {
            return null;
        }

        ProjetoRequest request = new ProjetoRequest();
        request.setNumeroSipac(response.getNumeroSipac());
        request.setAnoSipac(response.getAnoSipac());
        request.setTitulo(response.getTitulo());
        request.setValor(response.getValor());
        request.setContaContrato(response.getContaContrato());
        request.setDataInicio(response.getDataInicio());
        request.setDataFim(response.getDataFim());
        request.setLeiDeInformatica(Boolean.TRUE.equals(response.getLeiDeInformatica()));
        request.setSebrae(Boolean.TRUE.equals(response.getSebrae()));
        request.setEmbrapii(Boolean.TRUE.equals(response.getEmbrapii()));
        request.setResidencia(Boolean.TRUE.equals(response.getResidencia()));
        request.setCategoria(response.getCategoria());
        request.setStatus(response.getStatus());
        request.setDescricao(response.getDescricao());
        request.setIdProjeto(response.getIdProjeto());
        if (response.getCoordenador() != null) {
            request.setCoordenadorId(response.getCoordenador().getSiape());
        }

        if (response.getParceirosId() != null && !response.getParceirosId().isEmpty()) {
            List<ProjetoParceiroRequest> parceiroRequests = response.getParceirosId().stream()
                    .map(uuid -> {
                        ProjetoParceiroRequest ppr = new ProjetoParceiroRequest();
                        ppr.setParceiroId(uuid);
                        return ppr;
                    })
                    .collect(Collectors.toList());
            request.setParceirosId(parceiroRequests);
        }

        return request;
    }

    public TipoFinanciamento encontrarTipoFinanciamento(Projeto projeto) {
        List<String> ordemPrioridade = Arrays.asList("EMPRESA", "SEBRAE", "UFRN", "EMBRAPII", "FUNPEC");
        Map<String, Parceiro> parceirosPorTipo = new HashMap<>();

        for (ProjetoHasParceiro projetoParceiro : projeto.getParceiros()) {
            Parceiro parceiro = projetoParceiro.getParceiro();
            String nomeParceiro = parceiro.getNome().toUpperCase();

            if (nomeParceiro.contains("FUNPEC")) {
                parceirosPorTipo.put("FUNPEC", parceiro);
            } else if (nomeParceiro.contains("EMBRAPII")) {
                parceirosPorTipo.put("EMBRAPII", parceiro);
            } else if (nomeParceiro.contains("UFRN")) {
                parceirosPorTipo.put("UFRN", parceiro);
            } else if (nomeParceiro.contains("SEBRAE")) {
                parceirosPorTipo.put("SEBRAE", parceiro);
            } else {
                parceirosPorTipo.put("EMPRESA", parceiro);
            }
        }

        TipoFinanciamento tipoFinanciamento = null;
        for (String tipo : ordemPrioridade) {
            if (parceirosPorTipo.containsKey(tipo)) {
                tipoFinanciamento = parceirosPorTipo.get(tipo).getTipoFinanciamento();
                break;
            }
        }

        return tipoFinanciamento;
    }
}