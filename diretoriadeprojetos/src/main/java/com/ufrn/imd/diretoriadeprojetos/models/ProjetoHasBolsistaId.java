package com.ufrn.imd.diretoriadeprojetos.models;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class ProjetoHasBolsistaId implements Serializable {

    private String nSipac;
    private UUID bolsistaUuid;
}