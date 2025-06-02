package com.ufrn.imd.diretoriadeprojetos.clients;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import com.ufrn.imd.diretoriadeprojetos.auth.AuthApiService;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.models.AuthApiProperties;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
public class ProjetoClient {
    @Autowired
    private WebClient webClient;

    @Autowired
    private AuthApiService authApiService;

    @Autowired
    private AuthApiProperties authProperties;

    public Mono<List<ProjetoApiResponse>> buscarNaApi(Optional<String> numero,
            Optional<String> ano) {
        if (!numero.isPresent() && !ano.isPresent()) {
            return Mono.error(new IllegalArgumentException(
                    "É preciso informar pelo menos o número ou o ano do projeto."));
        }

        Mono<String> tokenMono = Mono
                .fromCallable(() -> authApiService.solicitarToken(
                        authProperties.getClientId(),
                        authProperties.getClientSecret()));

        // 3) monta e dispara a requisição
        return tokenMono.flatMap(token -> webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder ub = uriBuilder.path("/projeto-convenio/v1/projetos");
                    numero.ifPresent(n -> ub.queryParam("numero", n));
                    ano.ifPresent(a -> ub.queryParam("ano", a));
                    return ub.build();
                })
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(), // predicate correto
                        resp -> Mono.error(new RuntimeException("Filtro inválido: "
                                + resp.statusCode().value())) // devolve Mono<Throwable>
                )
                .onStatus(
                        status -> status.is5xxServerError(), // mesmo para 5xx
                        resp -> Mono.error(new RuntimeException(
                                "Erro no servidor externo")))
                .bodyToFlux(ProjetoApiResponse.class)
                .collectList());
    }
}
