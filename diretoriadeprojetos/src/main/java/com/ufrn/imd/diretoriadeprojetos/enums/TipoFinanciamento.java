
package com.ufrn.imd.diretoriadeprojetos.enums;

public enum TipoFinanciamento {
    PRIVADO,
    PUBLICO;

    public static TipoFinanciamento fromNome(String nomeEsfera) {
        if (nomeEsfera == null) {
            return null;
        }

        String normalizedName = nomeEsfera.toUpperCase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")
                .replace("Ã", "A")
                .replace("Õ", "O")
                .replace("Ç", "C");

        if (normalizedName.contains("PRIVADO")) {
            return PRIVADO;
        } else if (normalizedName.contains("PUBLICO")) {
            return PUBLICO;
        }
        return null;
    }
}