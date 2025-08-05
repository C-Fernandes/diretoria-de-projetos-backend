package com.ufrn.imd.diretoriadeprojetos.auth;

import java.net.URI;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ufrn.imd.diretoriadeprojetos.dtos.response.TokenResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.AuthTokenException;
import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

@Service
public class AuthApiService {

    private final RestTemplate restTemplate;
    private final AuthApiProperties authProps;

    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    public AuthApiService(RestTemplate restTemplate, AuthApiProperties authProps) {
        this.restTemplate = restTemplate;
        this.authProps = authProps;
    }

    public String fetchAccessToken() {
        URI uri = UriComponentsBuilder.fromPath("/authz-server/oauth/token")
                .queryParam("grant_type", GRANT_TYPE_CLIENT_CREDENTIALS)
                .queryParam("client_id", authProps.getClientId())
                .queryParam("client_secret", authProps.getClientSecret())
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<TokenResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    TokenResponse.class);

            TokenResponse tokenResponse = response.getBody();

            if (tokenResponse != null && tokenResponse.getAccessToken() != null) {
                return tokenResponse.getAccessToken();
            } else {
                throw new AuthTokenException("Servidor de autorização retornou uma resposta ou token nulos.");
            }
        } catch (RestClientException e) {
            throw new AuthTokenException("Falha ao buscar token de acesso devido a um erro do cliente.");
        }
    }
}