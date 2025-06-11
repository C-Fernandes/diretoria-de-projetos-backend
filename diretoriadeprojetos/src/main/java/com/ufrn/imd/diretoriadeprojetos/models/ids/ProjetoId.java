package com.ufrn.imd.diretoriadeprojetos.models.ids;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ProjetoId implements Serializable {

    @Column(name = "numero_sipac")
    private String numeroSipac;

    @Column(name = "ano_sipac")
    private String anoSipac;

}
