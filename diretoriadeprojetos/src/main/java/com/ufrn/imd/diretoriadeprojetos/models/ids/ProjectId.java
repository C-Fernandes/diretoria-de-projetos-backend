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
public class ProjectId implements Serializable {

    @Column(name = "sipac_number")
    private long sipacNumber;

    @Column(name = "sipac_year")
    private long sipacYear;

}
