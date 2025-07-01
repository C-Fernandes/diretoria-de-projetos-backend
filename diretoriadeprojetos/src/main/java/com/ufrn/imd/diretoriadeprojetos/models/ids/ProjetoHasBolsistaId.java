package com.ufrn.imd.diretoriadeprojetos.models.ids;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ProjetoHasBolsistaId implements Serializable {

    private ProjetoParceiroId projetoParceiroId;

    private UUID bolsistaUuid;
}