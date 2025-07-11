package com.ufrn.imd.diretoriadeprojetos.errors;

import org.springframework.http.HttpStatus;

public class RouteNotFound extends HttpError {
    public RouteNotFound() {
        super("A rota solicitada não foi encontrada.", HttpStatus.NOT_FOUND);
    }
}