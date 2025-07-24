package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Processo;

@Service
public class ProcessoService {

    public List<ProjetoResponse> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    public List<ProjetoResponse> buscarNaApi(long radical, long numProtocolo, long ano, long dv) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarNaApi'");
    }

    public void delete(long radical, long numProtocolo, long ano, long dv) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    public ProjetoResponse findById(long radical, long numProtocolo, long ano, long dv) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    public Processo salvar(Processo processo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'salvar'");
    }

}
