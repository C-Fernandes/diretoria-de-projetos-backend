package com.ufrn.imd.diretoriadeprojetos.errors;

import org.springframework.http.HttpStatus;

public class AuthTokenException extends HttpError {
    public AuthTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

}