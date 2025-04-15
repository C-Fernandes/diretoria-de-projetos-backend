package com.ufrn.imd.diretoriadeprojetos.models;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ProjetoHasBolsistaId implements Serializable {

    private String projetoId; // corresponde ao nSipac
    private String bolsistaId;

}