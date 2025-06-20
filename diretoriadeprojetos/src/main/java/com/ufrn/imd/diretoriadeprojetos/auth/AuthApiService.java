package com.ufrn.imd.diretoriadeprojetos.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ufrn.imd.diretoriadeprojetos.dtos.response.TokenResponse;
import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

@Service
public class AuthApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthApiProperties authProps;

    public String solicitarToken() {
        String clientId = authProps.getClientId();
        String clientSecret = authProps.getClientSecret();

        System.out.println(">>> Entrou em AuthApiService.solicitarToken");
        System.out.println("    clientId = " + clientId);

        String url = "/authz-server/oauth/token" +
                "?client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&grant_type=client_credentials";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(MediaType.parseMediaTypes("application/json"));

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<TokenResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    TokenResponse.class);

            TokenResponse tokenResponse = response.getBody();

            if (tokenResponse != null) {
                return tokenResponse.getAccessToken();
            } else {
                System.err.println(">>> TokenResponse veio nulo");
                return null;
            }
        } catch (Exception e) {
            System.err.println(">>> Erro ao obter token no AuthApiService: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
