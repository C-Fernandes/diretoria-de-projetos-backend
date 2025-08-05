package com.ufrn.imd.diretoriadeprojetos.errors;

import org.springframework.http.HttpStatus;

public class ClientException extends HttpError {
    public ClientException(String message, HttpStatus status) {
        super(message, status);
    }
}