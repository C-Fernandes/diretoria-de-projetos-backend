package com.ufrn.imd.diretoriadeprojetos.errors;

import org.springframework.http.HttpStatus;

public class ResourceConflict extends HttpError {
    public ResourceConflict(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
