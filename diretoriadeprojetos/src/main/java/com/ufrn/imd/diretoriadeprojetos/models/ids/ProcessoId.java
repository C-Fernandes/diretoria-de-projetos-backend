package com.ufrn.imd.diretoriadeprojetos.models.ids;

import jakarta.annotation.Generated;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode

@AllArgsConstructor
@NoArgsConstructor
public class ProcessoId {
    private long radical, numProtocolo, ano, dv;
}
