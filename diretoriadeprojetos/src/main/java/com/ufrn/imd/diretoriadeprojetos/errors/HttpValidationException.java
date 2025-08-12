package com.ufrn.imd.diretoriadeprojetos.errors;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class HttpValidationException extends HttpError {

    private final Map<String, List<String>> errors;

    public HttpValidationException(String field, String message) {
        super("Os dados fornecidos são inválidos.", HttpStatus.UNPROCESSABLE_ENTITY);

        this.errors = Map.of(field, List.of(message));
    }

    @Override
    public Map<String, Object> getError() {
        Map<String, Object> baseError = super.getError();

        baseError.put("errors", this.errors);

        return baseError;
    }
}