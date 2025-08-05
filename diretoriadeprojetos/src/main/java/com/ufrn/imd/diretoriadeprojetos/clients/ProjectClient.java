package com.ufrn.imd.diretoriadeprojetos.clients;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ufrn.imd.diretoriadeprojetos.auth.AuthApiService;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.AuthTokenException;
import com.ufrn.imd.diretoriadeprojetos.errors.ClientException;
import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

@Component
public class ProjectClient {

        private static final String PROJECTS_API_PATH = "/projeto-convenio/v1/projetos";

        private final RestTemplate restTemplate;
        private final AuthApiService authApiService;
        private final AuthApiProperties authProps;

        public ProjectClient(RestTemplate restTemplate, AuthApiService authApiService, AuthApiProperties authProps) {
                this.restTemplate = restTemplate;
                this.authApiService = authApiService;
                this.authProps = authProps;
        }

        public ProjetoApiResponse findByNumberAndYear(long number, long year) throws ClientException {
                String token;
                try {
                        token = authApiService.fetchAccessToken();
                } catch (AuthTokenException e) {
                        throw new ClientException(
                                        "Erro de autenticação interna. Não foi possível acessar o serviço dependente.",
                                        HttpStatus.SERVICE_UNAVAILABLE);
                }

                try {
                        HttpHeaders headers = createHeaders(token);
                        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
                        String url = buildUrl(number, year);

                        ResponseEntity<ProjetoApiResponse[]> response = restTemplate.exchange(
                                        url, HttpMethod.GET, requestEntity, ProjetoApiResponse[].class);

                        ProjetoApiResponse[] projects = Objects.requireNonNull(response.getBody(),
                                        "O corpo da resposta da API de projetos não pode ser nulo.");

                        if (projects.length > 0) {
                                return projects[0];
                        } else {
                                // 2. Trata "não encontrado" como um erro explícito
                                throw new ClientException(
                                                "Nenhum projeto encontrado para o número " + number + " e ano " + year,
                                                HttpStatus.NOT_FOUND);
                        }

                } catch (HttpClientErrorException e) {
                        throw new ClientException("Requisição inválida para a API de projetos.",
                                        HttpStatus.BAD_REQUEST);
                } catch (HttpServerErrorException e) {
                        throw new ClientException("O serviço de projetos está indisponível ou com erro interno.",
                                        HttpStatus.SERVICE_UNAVAILABLE);
                } catch (ResourceAccessException e) {
                        throw new ClientException("Não foi possível conectar ao serviço de projetos.",
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

        private String buildUrl(long number, long year) {
                return UriComponentsBuilder.fromPath(PROJECTS_API_PATH)
                                .queryParam("numero", number)
                                .queryParam("ano", year)
                                .toUriString();
        }
}