package com.ufrn.imd.diretoriadeprojetos.errors;

import org.springframework.http.HttpStatus;

public class EntityNotFound extends HttpError {
    public EntityNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
