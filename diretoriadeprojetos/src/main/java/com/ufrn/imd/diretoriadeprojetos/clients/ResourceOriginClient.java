package com.ufrn.imd.diretoriadeprojetos.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufrn.imd.diretoriadeprojetos.auth.AuthApiService;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.OrigemRecursoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroApiResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.AuthTokenException;
import com.ufrn.imd.diretoriadeprojetos.errors.ClientException;
import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class ResourceOriginClient {

    private static final String INCOME_SOURCES_API_PATH = "/projeto-convenio/v1/projetos/{projectId}/origem-recurso";
    private static final String PARTNERS_API_PATH = "/projeto-convenio/v1/projetos-convenio/{projectId}/participes";

    private final RestTemplate restTemplate;
    private final AuthApiService authApiService;
    private final AuthApiProperties authProps;

    public ResourceOriginClient(RestTemplate restTemplate, AuthApiService authApiService, AuthApiProperties authProps) {
        this.restTemplate = restTemplate;
        this.authApiService = authApiService;
        this.authProps = authProps;
    }

    public List<OrigemRecursoApiResponse> findIncomeSources(Long projectId) throws ClientException {
        String token;
        try {
            token = authApiService.fetchAccessToken();
        } catch (AuthTokenException e) {
            throw new ClientException("Erro de autenticação interna. Não foi possível acessar o serviço dependente.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        try {
            HttpHeaders headers = createHeaders(token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            String url = buildUrlForIncomeSources(projectId);

            ResponseEntity<List<OrigemRecursoApiResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<List<OrigemRecursoApiResponse>>() {
                    });

            return Objects.requireNonNull(response.getBody(),
                    "O corpo da resposta da API de origens de recurso não pode ser nulo.");

        } catch (HttpClientErrorException e) {
            throw new ClientException("Requisição inválida para a API de origens de recurso.", HttpStatus.BAD_REQUEST);
        } catch (HttpServerErrorException e) {
            throw new ClientException("O serviço de origens de recurso está indisponível ou com erro interno.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ResourceAccessException e) {
            throw new ClientException("Não foi possível conectar ao serviço de origens de recurso.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<ParceiroApiResponse> findPartners(Long projectId) throws ClientException {
        String token;
        try {
            token = authApiService.fetchAccessToken();
        } catch (AuthTokenException e) {
            throw new ClientException("Erro de autenticação interna. Não foi possível acessar o serviço dependente.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        try {
            HttpHeaders headers = createHeaders(token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            String url = buildUrlForPartners(projectId);

            ResponseEntity<ParceiroApiResponse[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, ParceiroApiResponse[].class);

            ParceiroApiResponse[] partners = Objects.requireNonNull(response.getBody(),
                    "O corpo da resposta da API de parceiros não pode ser nulo.");
            return Arrays.asList(partners);

        } catch (HttpClientErrorException e) {
            throw new ClientException("Requisição inválida para a API de parceiros.", HttpStatus.BAD_REQUEST);
        } catch (HttpServerErrorException e) {
            throw new ClientException("O serviço de parceiros está indisponível ou com erro interno.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ResourceAccessException e) {
            throw new ClientException("Não foi possível conectar ao serviço de parceiros.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-api-key", authProps.getXApiKey());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private String buildUrlForIncomeSources(Long projectId) {
        // Ajustado para usar .fromPath, que cria uma URL relativa
        return UriComponentsBuilder.fromPath(INCOME_SOURCES_API_PATH)
                .buildAndExpand(projectId)
                .toUriString();
    }

    private String buildUrlForPartners(Long projectId) {
        // Ajustado para usar .fromPath, que cria uma URL relativa
        return UriComponentsBuilder.fromPath(PARTNERS_API_PATH)
                .buildAndExpand(projectId)
                .toUriString();
    }
}