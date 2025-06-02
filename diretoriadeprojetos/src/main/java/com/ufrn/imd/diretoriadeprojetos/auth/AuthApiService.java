package com.ufrn.imd.diretoriadeprojetos.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

import reactor.core.publisher.Mono;

@Service
public class AuthApiService {
    @Autowired
    private WebClient webClient;
    @Autowired
    private AuthApiProperties authProperties;

    public String solicitarToken(String clientId, String clientSecret) {
        Mono<String> response = webClient.post()
                .uri("/authz-server/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(String.class);

        return response.block();
    }
}
