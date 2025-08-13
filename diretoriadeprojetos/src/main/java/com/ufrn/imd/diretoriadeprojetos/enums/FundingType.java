
package com.ufrn.imd.diretoriadeprojetos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FundingType {
    PRIVATE("PRIVADO"),
    PUBLIC("PUBLICO");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    FundingType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static FundingType fromName(String sphereName) {
        if (sphereName == null) {
            return null;
        }
        String normalizedName = sphereName.toUpperCase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")
                .replace("Ã", "A")
                .replace("Õ", "O")
                .replace("Ç", "C");

        if (normalizedName.contains("PRIVADO") || normalizedName.contains("PRIVATE")) {
            return PRIVATE;
        } else if (normalizedName.contains("PUBLICO") || normalizedName.contains("PUBLIC")) {
            return PUBLIC;
        }

        return null;
    }
}