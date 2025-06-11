package com.ufrn.imd.diretoriadeprojetos.clients;

import java.util.List;
import org.springframework.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ufrn.imd.diretoriadeprojetos.auth.AuthApiService;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.CoordenadorApiResponse;
import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

@Component
public class CoordenadorClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthApiService authApiService;

    @Autowired
    private AuthApiProperties authProps;

    public List<CoordenadorApiResponse> buscarPorSiape(Long siape) {
        String token = authApiService.solicitarToken();

        if (token == null) {
            System.err.println("Erro: token é nulo");
            return List.of();
        }

        String url = UriComponentsBuilder.fromPath("/docente/v1/docentes")
                .queryParam("siape", siape)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-api-key", authProps.getXApiKey());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<CoordenadorApiResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    CoordenadorApiResponse[].class);

            CoordenadorApiResponse[] docentes = response.getBody();

            if (docentes != null) {
                return List.of(docentes);
            } else {
                return List.of();
            }
        } catch (Exception e) {
            System.err.println("Erro ao chamar /docente/v1/docentes: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
