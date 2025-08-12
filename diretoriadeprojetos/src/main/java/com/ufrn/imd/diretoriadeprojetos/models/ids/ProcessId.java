package com.ufrn.imd.diretoriadeprojetos.models.ids;

import java.io.Serializable;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
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
public class ProcessId implements Serializable {
    private long radical;

    private long protocolNumber;

    @Column(name = "year")
    private long year;

    private long dv;

}