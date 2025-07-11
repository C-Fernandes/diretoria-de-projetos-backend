package com.ufrn.imd.diretoriadeprojetos.errors;

import org.springframework.http.HttpStatus;

public class UsedField extends HttpError {
    public UsedField(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
