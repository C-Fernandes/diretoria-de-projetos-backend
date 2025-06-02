package com.ufrn.imd.diretoriadeprojetos.models;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component

public class AuthApiProperties {

    @Value("${auth.client-id}")
    private String clientId;

    @Value("${auth.client-secret}")
    private String clientSecret;
    @Value("${auth.x-api-key}")
    private String xApiKey;
}
