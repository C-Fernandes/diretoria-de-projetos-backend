package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;

@Entity
public class Aditivo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private Date dataAssinatura;
    private Double valor;
    private Date novaData;
    private String processo;
    private String objetoTermo;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "numero_sipac", referencedColumnName = "numero_sipac"),
            @JoinColumn(name = "ano_sipac", referencedColumnName = "ano_sipac")
    })
    private Projeto projeto;
}
