package com.ufrn.imd.diretoriadeprojetos.errors;

import org.springframework.http.HttpStatus;

public class MissingFields extends HttpError {
    public MissingFields(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
