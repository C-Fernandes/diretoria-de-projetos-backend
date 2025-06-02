package com.ufrn.imd.diretoriadeprojetos.errors;

import org.springframework.http.HttpStatus;

public class Unauthorized extends HttpError {
    public Unauthorized() {
        super("Não autorizado!", HttpStatus.UNAUTHORIZED);
    };
};
