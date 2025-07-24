package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.Date;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Movimentacao {

    @Id
    private long id;
    private String unidadeOrigem;
    private String unidadeDestino;
    private Date dataEnvioOrigem;
    private Date dataRecebimentoDestino;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "radical", referencedColumnName = "radical"),
            @JoinColumn(name = "numProtocolo", referencedColumnName = "numProtocolo"),
            @JoinColumn(name = "ano", referencedColumnName = "ano"),
            @JoinColumn(name = "dv", referencedColumnName = "dv")
    })
    private Processo processo;
}
