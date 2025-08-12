package com.ufrn.imd.diretoriadeprojetos.models;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "process")
public class Movement {

    @Id
    private Long id;
    private String originUnit;
    private String destinationUnit;

    private Date sentDate;
    private Date receivedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "process_radical", referencedColumnName = "radical"),
            @JoinColumn(name = "process_protocol_number", referencedColumnName = "protocolNumber"),
            @JoinColumn(name = "process_year", referencedColumnName = "year"),
            @JoinColumn(name = "process_dv", referencedColumnName = "dv")
    })
    private ProjectProcess process;
}
