package com.ufrn.imd.diretoriadeprojetos.errors;

import org.springframework.http.HttpStatus;

public class ApiError extends HttpError {
    public ApiError() {
        super("Erro ao buscar na API", HttpStatus.BAD_GATEWAY);
    }

    public ApiError(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }

    public ApiError(String message, HttpStatus status) {
        super(message, status);
    }
}