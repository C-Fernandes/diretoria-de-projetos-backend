package com.ufrn.imd.diretoriadeprojetos.clients;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufrn.imd.diretoriadeprojetos.auth.AuthApiService;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.OrigemRecursoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroApiResponse;
import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

@Component
public class OrigemRecursoClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthApiService authApiService;

    @Autowired
    private AuthApiProperties authProps;

    public List<OrigemRecursoApiResponse> buscarFontesDeRenda(Long idProjeto) {
        String token = authApiService.solicitarToken();

        if (token == null) {
            System.err.println("Erro: token é nulo. Não foi possível autenticar com a API da UFRN.");
            return List.of();
        }

        String baseUrl = "https://api.info.ufrn.br";
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/projeto-convenio/v1/projetos/{idProjeto}/origem-recurso")
                .buildAndExpand(idProjeto)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-api-key", authProps.getXApiKey());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> rawResponse = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    String.class // Esperamos uma String aqui
            );

            String jsonBody = rawResponse.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            List<OrigemRecursoApiResponse> origensDeRecurso = objectMapper.readValue(
                    jsonBody,
                    new TypeReference<List<OrigemRecursoApiResponse>>() {
                    });

            if (origensDeRecurso != null && !origensDeRecurso.isEmpty()) {
                return origensDeRecurso;
            } else {
                System.out.println(
                        "Nenhuma fonte de renda encontrada para o projeto " + idProjeto + " ou resposta vazia.");
                return List.of();
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar fontes de renda para o projeto " + idProjeto + ": " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<ParceiroApiResponse> buscarParceirosNaApi(Long idProjeto) {
        String token = authApiService.solicitarToken();

        if (token == null) {
            System.err.println("Erro: token é nulo");
            return List.of();
        }

        String url = UriComponentsBuilder
                .fromUriString("/projeto-convenio/v1/projetos-convenio/{idProjeto}/participes")
                .buildAndExpand(idProjeto)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-api-key", authProps.getXApiKey());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ParceiroApiResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    ParceiroApiResponse[].class);

            ParceiroApiResponse[] parceirosArray = response.getBody();
            if (parceirosArray != null) {

                System.out.println("Parceiros na api :" + parceirosArray);
                return List.of(parceirosArray);
            } else {
                return List.of();
            }
        } catch (Exception e) {
            System.err.println(
                    "Erro ao chamar /projetos-convenio/{idProjeto}/participes: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
