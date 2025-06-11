package com.ufrn.imd.diretoriadeprojetos.clients;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ufrn.imd.diretoriadeprojetos.auth.AuthApiService;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroApiResponse;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

@Component
public class ProjetoClient {

        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private AuthApiService authApiService;

        @Autowired
        private AuthApiProperties authProps;

        public List<ProjetoApiResponse> buscarNaApi(String numero, String ano) {
                String token = authApiService.solicitarToken();

                if (token == null) {
                        System.err.println("Erro: token é nulo");
                        return List.of(); // ou lançar exceção
                }

                String url = UriComponentsBuilder.fromUriString("/projeto-convenio/v1/projetos")
                                .queryParam("numero", numero)
                                .queryParam("ano", ano)
                                .toUriString();

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.set("X-api-key", authProps.getXApiKey());
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));

                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

                try {
                        ResponseEntity<ProjetoApiResponse[]> response = restTemplate.exchange(
                                        url,
                                        HttpMethod.GET,
                                        requestEntity,
                                        ProjetoApiResponse[].class);

                        ProjetoApiResponse[] projetosArray = response.getBody();
                        if (projetosArray != null) {
                                return List.of(projetosArray);
                        } else {
                                return List.of();
                        }
                } catch (Exception e) {
                        System.err.println("Erro ao chamar /projeto-convenio/v1/projetos: " + e.getMessage());
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
