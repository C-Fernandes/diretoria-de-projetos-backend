package com.ufrn.imd.diretoriadeprojetos.clients;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ufrn.imd.diretoriadeprojetos.auth.AuthApiService;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.CoordenadorApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.MovimentacaoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.ApiError;
import com.ufrn.imd.diretoriadeprojetos.errors.ResourceConflict;
import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

@Component
public class ProcessoClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthApiService authApiService;

    @Autowired
    private AuthApiProperties authProps;

    public ProcessoApiResponse buscarProcesso(long radical, long numProtocolo, long ano, long dv) {
        String token = authApiService.solicitarToken();
        if (token == null) {
            throw new ApiError("Erro ao buscar token da API");
        }

        String url = UriComponentsBuilder.fromPath("processo/v1/processos")
                .queryParam("radical", radical)
                .queryParam("num-protocolo", numProtocolo)
                .queryParam("ano", ano)
                .queryParam("dv", dv)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-api-key", authProps.getXApiKey());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ProcessoApiResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    ProcessoApiResponse[].class);

            ProcessoApiResponse[] processos = response.getBody();

            if (processos != null && processos.length > 0) {
                System.out.println("na api: " + processos[0].toString());
                return processos[0];
            } else {
                throw new ApiError("Nenhum processo encontrado");
            }

        } catch (RestClientException e) {
            System.out.println("!!! ERRO: Ocorreu um erro de comunicação ao chamar a API.");
            e.printStackTrace();
            throw new ApiError("Erro de comunicação com a API externa");
        }
    }

    public List<MovimentacaoApiResponse> buscarMovimetacoes(long processoId) {
        String token = authApiService.solicitarToken();
        if (token == null) {
            throw new ApiError("Erro ao buscar token da API");
        }

        String url = UriComponentsBuilder.fromPath("processo/v1/movimentacoes")
                .queryParam("id-processo", processoId).queryParam("limit", 100)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-api-key", authProps.getXApiKey());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<MovimentacaoApiResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    MovimentacaoApiResponse[].class);

            MovimentacaoApiResponse[] movimentacoes = response.getBody();

            if (movimentacoes != null) {
                return List.of(movimentacoes);
            } else {
                return List.of();
            }

        } catch (RestClientException e) {
            System.out.println("!!! ERRO: Ocorreu um erro de comunicação ao chamar a API.");
            e.printStackTrace(); // Mantém o print do stack trace completo
            throw new ApiError("Erro de comunicação com a API externa");
        }
    }

}