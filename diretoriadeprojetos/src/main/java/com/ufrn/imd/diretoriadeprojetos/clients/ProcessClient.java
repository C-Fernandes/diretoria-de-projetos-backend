package com.ufrn.imd.diretoriadeprojetos.clients;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ufrn.imd.diretoriadeprojetos.auth.AuthApiService;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.MovimentacaoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProcessoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.AuthTokenException;
import com.ufrn.imd.diretoriadeprojetos.errors.ClientException;
import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

@Component
public class ProcessClient {

    private static final String PROCESS_API_PATH = "/processo/v1/processos";
    private static final String MOVEMENTS_API_PATH = "/processo/v1/movimentacoes";

    private final RestTemplate restTemplate;
    private final AuthApiService authApiService;
    private final AuthApiProperties authProps;

    public ProcessClient(RestTemplate restTemplate, AuthApiService authApiService, AuthApiProperties authProps) {
        this.restTemplate = restTemplate;
        this.authApiService = authApiService;
        this.authProps = authProps;
    }

    public ProcessoApiResponse findProcess(long radical, long protocolNumber, long year, long checkDigit)
            throws ClientException {
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
            String url = buildUrlForProcess(radical, protocolNumber, year, checkDigit);

            ResponseEntity<ProcessoApiResponse[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, ProcessoApiResponse[].class);

            ProcessoApiResponse[] processes = Objects.requireNonNull(response.getBody(),
                    "O corpo da resposta da API de processos não pode ser nulo.");

            if (processes.length > 0) {
                return processes[0];
            } else {
                throw new ClientException("Nenhum processo encontrado para os parâmetros informados.",
                        HttpStatus.NOT_FOUND);
            }

        } catch (HttpClientErrorException e) {
            throw new ClientException("Requisição inválida para a API de processos.", HttpStatus.BAD_REQUEST);
        } catch (HttpServerErrorException e) {
            throw new ClientException("O serviço de processos está indisponível ou com erro interno.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ResourceAccessException e) {
            throw new ClientException("Não foi possível conectar ao serviço de processos.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<MovimentacaoApiResponse> findMovements(long processId) throws ClientException {
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
            String url = buildUrlForMovements(processId);

            ResponseEntity<MovimentacaoApiResponse[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, MovimentacaoApiResponse[].class);

            MovimentacaoApiResponse[] movements = Objects.requireNonNull(response.getBody(),
                    "O corpo da resposta da API de movimentações não pode ser nulo.");
            return Arrays.asList(movements);

        } catch (HttpClientErrorException e) {
            throw new ClientException("Requisição inválida para a API de movimentações.", HttpStatus.BAD_REQUEST);
        } catch (HttpServerErrorException e) {
            throw new ClientException("O serviço de movimentações está indisponível ou com erro interno.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ResourceAccessException e) {
            throw new ClientException("Não foi possível conectar ao serviço de movimentações.",
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

    private String buildUrlForProcess(long radical, long protocolNumber, long year, long checkDigit) {
        return UriComponentsBuilder.fromPath(PROCESS_API_PATH)
                .queryParam("radical", radical)
                .queryParam("num-protocolo", protocolNumber)
                .queryParam("ano", year)
                .queryParam("dv", checkDigit)
                .toUriString();
    }

    private String buildUrlForMovements(long processId) {
        return UriComponentsBuilder.fromPath(MOVEMENTS_API_PATH)
                .queryParam("id-processo", processId)
                .queryParam("limit", 100)
                .toUriString();
    }
}