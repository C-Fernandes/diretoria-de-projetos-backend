
package com.ufrn.imd.diretoriadeprojetos.enums;

public enum FundingType {
    PRIVATE,
    PUBLIC;

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

        if (normalizedName.contains("PRIVATE")) {
            return PRIVATE;
        } else if (normalizedName.contains("PUBLIC")) {
            return PUBLIC;
        }

        return null;
    }
}