package com.ufrn.imd.diretoriadeprojetos.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ufrn.imd.diretoriadeprojetos.dtos.DadosPagamento;
import com.ufrn.imd.diretoriadeprojetos.models.Bolsista;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoHasBolsista;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;
import com.ufrn.imd.diretoriadeprojetos.repository.BolsistaRepository;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjetoHasBolsistaRepository;

@Service
public class BolsistaService {

    @Autowired
    private BolsistaRepository bolsistaRepository;

    @Autowired
    private ProjetoHasBolsistaService projetoHasBolsistaService;

    public List<Bolsista> listarTodos() {
        return bolsistaRepository.findAll();
    }

    public Optional<Bolsista> buscarPorId(UUID id) {
        return bolsistaRepository.findById(id);
    }

    public Bolsista salvar(Bolsista bolsista) {
        return bolsistaRepository.save(bolsista);
    }

    public Optional<Bolsista> findByNome(String nome) {
        return bolsistaRepository.findByNome(nome);
    }

    public Bolsista atualizar(UUID id, Bolsista novoBolsista) {
        return bolsistaRepository.findById(id).map(b -> {
            b.setCpf(novoBolsista.getCpf());
            b.setNome(novoBolsista.getNome());
            b.setEmail(novoBolsista.getEmail());
            b.setTipoSuperior(novoBolsista.getTipoSuperior());
            b.setCurso(novoBolsista.getCurso());
            b.setDocente(novoBolsista.getDocente());
            b.setFormacao(novoBolsista.getFormacao());
            return bolsistaRepository.save(b);
        }).orElseThrow(() -> new RuntimeException("Bolsista não encontrado"));
    }

    public void deletar(UUID id) {
        bolsistaRepository.deleteById(id);
    }

    public List<Bolsista> findByProjeto(ProjetoId projetoId) {
        List<ProjetoHasBolsista> associacoes = projetoHasBolsistaService.findByIdProjetoId(projetoId);

        return associacoes.stream()
                .map(ProjetoHasBolsista::getBolsista)
                .collect(Collectors.toList());
    }

    public List<DadosPagamento> processarCsv(MultipartFile file) {
        List<DadosPagamento> pagamentos = new ArrayList<>();

        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setDelimiter(';')
                .setHeader() // Indica que a primeira linha é o cabeçalho
                .setSkipHeaderRecord(true) // Pula a linha do cabeçalho ao ler os dados
                .setTrim(true)
                .setAllowMissingColumnNames(true)
                .build();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), Charset.forName("ISO-8859-1")));

                CSVParser csvParser = csvFormat.parse(reader)) {

            for (CSVRecord csvRecord : csvParser) {
                DadosPagamento dados = new DadosPagamento();
                dados.setNumeroProjeto(pegarNumeroProjeto(csvRecord));
                dados.setNumeroRubrica(pegarRubrica(csvRecord));
                dados.setNomeMembro(pegarMembro(csvRecord));
                dados.setNivel(csvRecord.get("Nível"));
                dados.setTipo(csvRecord.get("Tipo"));
                dados.setCargaHoraria(Double.parseDouble(csvRecord.get("Carga Horária").replace(",", ".")));
                dados.setValor(Double.parseDouble(csvRecord.get("Valor (R$)").replace(".", "").replace(",", ".")));
                dados.setCompetencia(csvRecord.get("Competência")); // Ajuste "Competência" para o nome real da coluna

                Optional<Bolsista> bolsistaExistente = findByNome(dados.getNomeMembro());
                if (bolsistaExistente.isPresent()) {
                    Optional<List<ProjetoHasBolsista>> relacoesBolsistasNoProjeto = projetoHasBolsistaService
                            .findBolsistaInProjeto(bolsistaExistente.get(), dados.getNumeroProjeto());

                    if (relacoesBolsistasNoProjeto.isPresent()) {

                        // caso a relaçaõ exista nesse projeto
                        for (ProjetoHasBolsista relacao : relacoesBolsistasNoProjeto.get()) {

                        }
                    } else {
                        // caso a relação não exista nesse projeto
                    }
                } else {

                    // caso o bolsista nem exista
                    Bolsista novoBolsista = new Bolsista(dados.getNomeMembro());
                    if (dados.getTipo().equalsIgnoreCase("docente")) {
                        novoBolsista.setDocente(true);
                    } else {
                        novoBolsista.setTipoSuperior(dados.getNivel());
                    }
                    novoBolsista = salvar(novoBolsista);
                }

                pagamentos.add(dados);
            }

        } catch (IOException e) {
            // Trate a exceção de I/O
            e.printStackTrace();
            // Ou lance uma exceção personalizada
            throw new RuntimeException("Erro ao processar o arquivo CSV: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            // Trate a exceção de formato de número (ex: se Carga Horária ou Valor não forem
            // números válidos)
            e.printStackTrace();
            throw new RuntimeException("Erro de formato de número no CSV: " + e.getMessage(), e);
        }

        return pagamentos;
    }

    private String pegarRubrica(CSVRecord csvRecord) {
        String rubricaCompleta = csvRecord.get("Rubrica");

        String rubrica;
        if (rubricaCompleta != null && !rubricaCompleta.isEmpty()) {
            Pattern pattern = Pattern.compile("^\\((\\d{2})\\)");
            Matcher matcher = pattern.matcher(rubricaCompleta);

            if (matcher.find()) {
                rubrica = matcher.group(1);
            } else {
                java.util.regex.Matcher matcher20 = java.util.regex.Pattern.compile("20")
                        .matcher(rubricaCompleta);
                java.util.regex.Matcher matcher18 = java.util.regex.Pattern.compile("18")
                        .matcher(rubricaCompleta);

                if (matcher20.find()) {
                    rubrica = "20";
                } else if (matcher18.find()) {
                    rubrica = "18";
                } else {
                    rubrica = null; // Ou um valor padrão se não encontrar 20 ou 18
                }
            }
        } else {
            rubrica = null; // Trata o caso da coluna "Rubrica" ser nula ou vazia
        }
        return rubrica;
    }

    private String pegarMembro(CSVRecord csvRecord) {
        String membroCompleto = csvRecord.get("Membro");
        String nomeMembro;
        if (membroCompleto != null && !membroCompleto.isEmpty()) {

            String separador = " - ";

            int ultimoIndice = membroCompleto.lastIndexOf(separador);

            if (ultimoIndice != -1) {
                String nomeApenas = membroCompleto.substring(ultimoIndice + separador.length());

                nomeMembro = nomeApenas.trim();

            } else {
                nomeMembro = membroCompleto.trim();
            }

        } else {
            nomeMembro = null;
        }
        return nomeMembro;
    }

    private String pegarNumeroProjeto(CSVRecord csvRecord) {
        String projetoCompleto = csvRecord.get("Projeto");

        String numeroProjeto;
        if (projetoCompleto != null && !projetoCompleto.isEmpty()) {
            Pattern pattern = Pattern.compile("^\\d+");
            Matcher matcher = pattern.matcher(projetoCompleto);
            if (matcher.find()) {
                numeroProjeto = matcher.group();
            } else {
                numeroProjeto = null;
            }
        } else {
            numeroProjeto = null;
        }
        return numeroProjeto;
    }

}