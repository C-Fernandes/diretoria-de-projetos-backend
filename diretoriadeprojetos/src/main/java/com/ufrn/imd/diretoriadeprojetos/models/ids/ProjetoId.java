package com.ufrn.imd.diretoriadeprojetos.models.ids;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class ProjetoId implements Serializable {

    @Column(name = "numero_sipac")
    private long numeroSipac;

    @Column(name = "ano_sipac")
    private long anoSipac;

}
