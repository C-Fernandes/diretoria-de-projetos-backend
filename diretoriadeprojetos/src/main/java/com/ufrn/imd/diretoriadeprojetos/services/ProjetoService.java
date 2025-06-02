package com.ufrn.imd.diretoriadeprojetos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ufrn.imd.diretoriadeprojetos.clients.ProjetoClient;
import com.ufrn.imd.diretoriadeprojetos.dtos.ProjetoDTO;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ProjetoApiResponse;
import com.ufrn.imd.diretoriadeprojetos.errors.EntityNotFound;
import com.ufrn.imd.diretoriadeprojetos.errors.HttpError;
import com.ufrn.imd.diretoriadeprojetos.errors.MissingFields;
import com.ufrn.imd.diretoriadeprojetos.models.Coordenador;
import com.ufrn.imd.diretoriadeprojetos.models.Parceiro;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoId;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjetoRepository;

import reactor.core.publisher.Mono;

@Service
public class ProjetoService {
    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private CoordenadorService coordenadorService;

    @Autowired
    private ParceiroService parceiroService;
    @Autowired
    private ProjetoClient projetoClient;

    public List<Projeto> listarTodos() {
        return projetoRepository.findAll();
    }

    public Projeto buscarPorId(ProjetoId id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
    }

    public Projeto salvar(ProjetoDTO projetoDTO) {
        if (projetoDTO.contaContrato() == null || projetoDTO.contaContrato().isBlank()) {
            throw new MissingFields("Conta contrato é obrigatório.");
        }

        if (projetoDTO.coordenadorId() == null) {
            throw new MissingFields("Coordenador é obrigatório.");
        }

        if (projetoDTO.parceiroCnpj() == null) {
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

        projeto.setId(new ProjetoId(projetoDTO.numeroSipac(), projetoDTO.anoSipac()));

        Parceiro parceiro = parceiroService.findById(projetoDTO.parceiroCnpj())
                .orElseThrow(() -> new EntityNotFound("Parceiro não encontrado"));
        projeto.setParceiro(parceiro);

        projeto.setValor(projetoDTO.valor());

        return projetoRepository.save(projeto);
    }

    /*
     * public Projeto atualizar(String nSipac, Projeto projetoAtualizado) {
     * Projeto projeto = buscarPorId(nSipac);
     * projeto.setNFunpec(projetoAtualizado.getNFunpec());
     * projeto.setSebrae(projetoAtualizado.getSebrae());
     * projeto.setEmbrapii(projetoAtualizado.getEmbrapii());
     * projeto.setLeiDeInformatica(projetoAtualizado.getLeiDeInformatica());
     * projeto.setValor(projetoAtualizado.getValor());
     * projeto.setDataInicio(projetoAtualizado.getDataInicio());
     * projeto.setDataFim(projetoAtualizado.getDataFim());
     * projeto.setContaContrato(projetoAtualizado.getContaContrato());
     * projeto.setCoordenador(projetoAtualizado.getCoordenador());
     * projeto.setParceiro(projetoAtualizado.getParceiro());
     * return projetoRepository.save(projeto);
     * }
     * 
     * public void deletar(String nSipac) {
     * projetoRepository.deleteById(nSipac);
     * }
     */

    public Mono<List<ProjetoApiResponse>> buscarNaApi(Optional<String> numeroSipac, Optional<String> anoSipac) {
        // aqui dentro, encaminhe para o client
        return projetoClient.buscarNaApi(numeroSipac, anoSipac);
    }

}