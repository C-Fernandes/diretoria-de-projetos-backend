package com.ufrn.imd.diretoriadeprojetos.dtos.response;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScholarshipHolderResponse {

    private UUID id;
    private String name;
    private String cpf;
    private String email;
    private Date startDate;
    private Date endDate;
    private Long funpecNumber;
    private int rubrica;
    private long sipacNumber;
    private long sipacYear;
    private String educationLevel;
    private String course;
    private String degree;
    private Boolean isTeacher;
    private int cH;
    private double value;

}