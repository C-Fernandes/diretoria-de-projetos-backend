package com.ufrn.imd.diretoriadeprojetos.models;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class ProjetoHasBolsistaId implements Serializable {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "numeroSipac", column = @Column(name = "numero_sipac", insertable = false, updatable = false)),
            @AttributeOverride(name = "anoSipac", column = @Column(name = "ano_sipac", insertable = false, updatable = false))
    })
    private ProjetoId projetoId;

    private UUID bolsistaUuid;
}