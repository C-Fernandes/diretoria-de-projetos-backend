package com.ufrn.imd.diretoriadeprojetos.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProcessoId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Processo {

    @EmbeddedId
    private ProcessoId id;
    @Column(nullable = false)
    private String nome;
    private ProjetoId projetoId;
    @Column
    private long idProcesso;
    @Column(nullable = false)
    private Date dataInicio;
    private String observacao;
    @ManyToOne
    @JoinColumn(name = "coordenador_matricula", nullable = false)
    private Coordenador coordenador;
    private double valor;
    @Column(nullable = false)
    private String financiador;
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Movimentacao> movimentacoes = new ArrayList<>();

}
