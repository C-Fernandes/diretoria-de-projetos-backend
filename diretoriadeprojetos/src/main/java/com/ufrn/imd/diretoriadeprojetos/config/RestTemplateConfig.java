package com.ufrn.imd.diretoriadeprojetos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("https://api.info.ufrn.br");
        restTemplate.setUriTemplateHandler(uriBuilderFactory);

        return restTemplate;
    }
}
