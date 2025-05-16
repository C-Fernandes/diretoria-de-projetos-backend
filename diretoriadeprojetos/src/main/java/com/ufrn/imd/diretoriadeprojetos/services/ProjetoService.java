package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.dtos.ProjetoDTO;
import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Parceiro;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjetoRepository;
import com.ufrn.imd.errors.EntityNotFound;
import com.ufrn.imd.errors.HttpError;
import com.ufrn.imd.errors.MissingFields;

@Service
public class ProjetoService {
    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private CoordenadorService coordenadorService;

    @Autowired
    private ParceiroService parceiroService;

    public List<Projeto> listarTodos() {
        return projetoRepository.findAll();
    }

    public Projeto buscarPorId(String nSipac) {
        return projetoRepository.findById(nSipac)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
    }

    public Projeto salvar(ProjetoDTO projetoDTO) {
        if (projetoDTO.contaContrato() == null || projetoDTO.contaContrato().isBlank()) {
            throw new MissingFields("Conta contrato é obrigatório.");
        }

        if (projetoDTO.coordenadorId() == null) {
            throw new MissingFields("Coordenador é obrigatório.");
        }

        if (projetoDTO.parceiroId() == null) {
            throw new MissingFields("Parceiro é obrigatório.");
        }

        Projeto projeto = new Projeto();
        projeto.setContaContrato(projetoDTO.contaContrato());

        Coordenador coordenador = coordenadorService.buscarPorMatricula(projetoDTO.coordenadorId())
                .orElseThrow(() -> new EntityNotFound("Coordenador não encontrado"));
        projeto.setCoordenador(coordenador);
        projeto.setNFunpec(projetoDTO.nFunpec());

        projeto.setTitulo(projetoDTO.titulo());
        projeto.setDataFim(projetoDTO.dataFim());
        projeto.setDataInicio(projetoDTO.dataInicio());

        projeto.setEmbrapii(Boolean.TRUE.equals(projetoDTO.embrapii()));
        projeto.setLeiDeInformatica(Boolean.TRUE.equals(projetoDTO.leiDeInformatica()));
        projeto.setSebrae(Boolean.TRUE.equals(projetoDTO.sebrae()));

        projeto.setNSipac(projetoDTO.nSipac());

        Parceiro parceiro = parceiroService.findById(projetoDTO.parceiroId())
                .orElseThrow(() -> new EntityNotFound("Parceiro não encontrado"));
        projeto.setParceiro(parceiro);

        projeto.setValor(projetoDTO.valor());

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