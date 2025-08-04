package com.ufrn.imd.diretoriadeprojetos.errors;

import org.springframework.http.HttpStatus;

public class InvalidCredentials extends HttpError {
    public InvalidCredentials() {
        super("Email e/ou senha incorretos.", HttpStatus.BAD_REQUEST);
    }
}
