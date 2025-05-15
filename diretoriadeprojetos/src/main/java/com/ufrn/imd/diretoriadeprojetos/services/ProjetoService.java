package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjetoRepository;

@Service
public class ProjetoService {
    @Autowired
    private ProjetoRepository projetoRepository;

    public List<Projeto> listarTodos() {
        return projetoRepository.findAll();
    }

    public Projeto buscarPorId(String nSipac) {
        return projetoRepository.findById(nSipac)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
    }

    public Projeto salvar(Projeto projeto) {
        return projetoRepository.save(projeto);
    }

    public Projeto atualizar(String nSipac, Projeto projetoAtualizado) {
        Projeto projeto = buscarPorId(nSipac);
        projeto.setNFunpec(projetoAtualizado.getNFunpec());
        projeto.setSebrae(projetoAtualizado.getSebrae());
        projeto.setEmbrapii(projetoAtualizado.getEmbrapii());
        projeto.setLeiDeInformatica(projetoAtualizado.getLeiDeInformatica());
        projeto.setValor(projetoAtualizado.getValor());
        projeto.setDataInicio(projetoAtualizado.getDataInicio());
        projeto.setDataFim(projetoAtualizado.getDataFim());
        projeto.setContaContrato(projetoAtualizado.getContaContrato());
        projeto.setCoordenador(projetoAtualizado.getCoordenador());
        projeto.setParceiro(projetoAtualizado.getParceiro());
        return projetoRepository.save(projeto);
    }

    public void deletar(String nSipac) {
        projetoRepository.deleteById(nSipac);
    }
}