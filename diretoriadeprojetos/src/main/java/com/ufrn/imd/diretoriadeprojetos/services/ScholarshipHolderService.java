package com.ufrn.imd.diretoriadeprojetos.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ScholarshipHolderResponse;
import com.ufrn.imd.diretoriadeprojetos.models.ScholarshipHolder;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ProjectPartner;
import com.ufrn.imd.diretoriadeprojetos.models.ProjectScholarshipHolder;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjectScholarshipHolderId;

import com.ufrn.imd.diretoriadeprojetos.repository.ScholarshipHolderRepository;

import jakarta.transaction.Transactional;

@Service
public class ScholarshipHolderService {

    @Autowired
    private ScholarshipHolderRepository scholarshipHolderRepository;

    @Autowired
    private ProjectScholarshipHolderService projetoHasBolsistaService;

    @Autowired
    private ProjectPartnerService projetoParceiroService;
    private static final DateTimeFormatter FORMATADOR_COMPETENCIA_FLEXIVEL = new DateTimeFormatterBuilder()
            .appendPattern("[MMM/yyyy][M/yyyy][MM/yyyy]") // Tenta "abr/2025", "4/2025" e "04/2025"
            .toFormatter(new Locale("pt", "BR"));

    public List<ScholarshipHolderResponse> findAll() {
        List<ScholarshipHolder> scholarshipHolders = scholarshipHolderRepository.findAll();

        return scholarshipHolders.stream() // Starts a stream of ScholarshipHolder objects
                .flatMap(scholarshipHolder -> scholarshipHolder.getProjects().stream())
                .map(link -> {
                    ScholarshipHolderResponse dto = new ScholarshipHolderResponse();
                    ScholarshipHolder holder = link.getScholarshipHolder();
                    ProjectPartner projectPartner = link.getProjectPartner();

                    // Maps the ScholarshipHolder data
                    if (holder != null) {
                        dto.setId(holder.getId());
                        dto.setCpf(holder.getCpf());
                        dto.setName(holder.getName());
                        dto.setEmail(holder.getEmail());
                        dto.setEducationLevel(holder.getEducationLevel());
                        dto.setCourse(holder.getCourse());
                        dto.setIsTeacher(holder.getIsTeacher());
                        dto.setDegree(holder.getDegree());
                    }

                    dto.setStartDate(link.getStartDate());
                    dto.setEndDate(link.getEndDate());
                    dto.setRubrica(link.getRubrica());
                    dto.setValue(link.getValue());
                    dto.setCH(link.getCh());

                    if (projectPartner != null && projectPartner.getProject() != null) {
                        dto.setFunpecNumber(projectPartner.getFunpecNumber());
                        if (projectPartner.getProject().getId() != null) {
                            dto.setSipacNumber(projectPartner.getProject().getId().getSipacNumber());
                            dto.setSipacYear(projectPartner.getProject().getId().getSipacYear());
                        }
                    }

                    return dto;

                }).collect(Collectors.toList());
    }

    public Optional<ScholarshipHolder> buscarPorId(UUID id) {
        return scholarshipHolderRepository.findById(id);
    }

    public ScholarshipHolder salvar(ScholarshipHolder bolsista) {
        return scholarshipHolderRepository.save(bolsista);
    }

    public Optional<ScholarshipHolder> findByNome(String nome) {
        return scholarshipHolderRepository.findByName(nome);
    }

    public ScholarshipHolder atualizar(UUID id, ScholarshipHolder novoBolsista) {
        return scholarshipHolderRepository.findById(id).map(b -> {
            b.setCpf(novoBolsista.getCpf());
            b.setName(novoBolsista.getName());
            b.setEmail(novoBolsista.getEmail());
            b.setEducationLevel(novoBolsista.getEducationLevel());
            b.setCourse(novoBolsista.getCourse());
            b.setIsTeacher(novoBolsista.getIsTeacher());
            b.setDegree(novoBolsista.getDegree());
            return scholarshipHolderRepository.save(b);
        }).orElseThrow(() -> new RuntimeException("ScholarshipHolder não encontrado"));
    }

    public void deletar(UUID id) {
        scholarshipHolderRepository.deleteById(id);
    }

    public List<ScholarshipHolder> findByProjeto(ProjectId projetoId) {
        List<ProjectScholarshipHolder> associacoes = projetoHasBolsistaService.findByIdProjetoId(projetoId);

        if (associacoes == null || associacoes.isEmpty()) {
            return Collections.emptyList(); // Ou new ArrayList<>();
        }

        return associacoes.stream()
                .map(ProjectScholarshipHolder::getScholarshipHolder)
                .collect(Collectors.toList());
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

    @Transactional
    public List<DadosPagamento> processarCsv(MultipartFile file) {
        System.out.println("\n\n[INFO] ========================================================");
        System.out.println("[INFO] ||         INICIANDO PROCESSAMENTO DO ARQUIVO CSV        ||");
        System.out.println("[INFO] ========================================================\n");

        List<DadosPagamento> pagamentosProcessados = new ArrayList<>();

        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setDelimiter(';')
                .setHeader()
                .setSkipHeaderRecord(true)
                .setAllowMissingColumnNames(true)
                .setTrim(true)
                .build();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.ISO_8859_1));
                CSVParser csvParser = csvFormat.parse(reader)) {

            for (CSVRecord csvRecord : csvParser) {
                System.out.println(String.format("\n[INFO] ----------- Processando Linha %d -----------",
                        csvRecord.getRecordNumber()));

                DadosPagamento dados = parseCsvRecord(csvRecord);

                System.out.println("[DEBUG] Dados lidos da linha: " + dados.toString());

                // 1. Valida e busca o projeto.
                System.out.println("[DEBUG] Buscando projeto com número FUNPEC: " + dados.getNumeroProjeto());
                Optional<ProjectPartner> projetoOpt = projetoParceiroService
                        .findByFunpecNumber(Long.parseLong(dados.getNumeroProjeto()));

                if (!projetoOpt.isPresent()) {
                    System.err.println("Projeto " + dados.getNumeroProjeto() + " não encontrado");
                    continue;
                } else {
                    System.err.println("Projeto " + dados.getNumeroProjeto() + " encontrado");

                }
                ProjectPartner projeto = projetoOpt.get();

                System.out.println("[DEBUG] Projeto encontrado: " + projeto.getProject().getTitulo());
                System.out.println("[DEBUG] Buscando bolsista com nome: '" + dados.getNomeMembro() + "'");
                Optional<ScholarshipHolder> bolsistaOpt = findByNome(dados.getNomeMembro());
                if (dados.getNumeroRubrica() == 18 || dados.getNumeroRubrica() == 20) {
                    if (bolsistaOpt.isPresent()) {
                        System.out.println(
                                "[FLUXO] ScholarshipHolder JÁ EXISTE. Entrando no fluxo de gerenciamento de vínculo...");
                        gerenciarVinculoExistente(bolsistaOpt.get(), projeto, dados);
                    } else {
                        System.out.println(
                                "[FLUXO] ScholarshipHolder NÃO EXISTE. Entrando no fluxo de criação de novo bolsista...");
                        criarNovoBolsistaComVinculo(projeto, dados);
                    }
                    pagamentosProcessados.add(dados);
                    System.out.println("[SUCCESS] Linha " + csvRecord.getRecordNumber() + " processada com sucesso.");
                }
            }

            System.out.println("\n[INFO] ========================================================");
            System.out.println("[INFO] ||       PROCESSAMENTO DO CSV FINALIZADO COM SUCESSO      ||");
            System.out.println("[INFO] ========================================================");

        } catch (IOException | DateTimeParseException e) {
            System.err.println("[ERRO] OCORREU UM ERRO GRAVE DURANTE O PROCESSAMENTO DO CSV: " + e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException("Erro ao processar o arquivo CSV: " + e.getMessage(), e);
        } catch (Exception e) {
            System.out.println(e);
        }
        return pagamentosProcessados;
    }

    private void gerenciarVinculoExistente(ScholarshipHolder bolsista, ProjectPartner projeto,
            DadosPagamento dados) {
        System.out.println("  [FLUXO] -> Entrou em gerenciarVinculoExistente para: " + bolsista.getName());

        // Converte a competência do CSV para o formato YearMonth
        YearMonth competenciaAtual = YearMonth.parse(dados.getCompetencia().toLowerCase(),
                FORMATADOR_COMPETENCIA_FLEXIVEL);
        System.out.println("    [DEBUG] Competência atual do CSV: " + competenciaAtual);
        System.out.println("    [DEBUG] Buscando TODOS os vínculos do bolsista com o projeto...");

        // Pega TODOS os vínculos, não apenas o mais recente
        List<ProjectScholarshipHolder> vinculosExistentes = projetoHasBolsistaService
                .findBolsistaInProjeto(bolsista, projeto.getFunpecNumber())
                .orElse(new ArrayList<>()); // Garante uma lista vazia se não houver nada

        if (vinculosExistentes.isEmpty()) {
            System.out.println("    [DEBUG] Nenhuma relação anterior encontrada para este bolsista no projeto.");
            System.out.println("      [AÇÃO] -> CRIANDO primeiro vínculo para bolsista já existente.");
            criarNovoVinculo(bolsista, projeto, dados);
            return; // Finaliza a execução para esta linha do CSV
        }

        // Procura por um vínculo que pode ser estendido PARA FRENTE
        Optional<ProjectScholarshipHolder> vinculoParaEstenderFim = vinculosExistentes.stream().filter(v -> {
            YearMonth dataFim = YearMonth.from(v.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            return competenciaAtual.equals(dataFim.plusMonths(1));
        }).findFirst();

        // Procura por um vínculo que pode ser estendido PARA TRÁS
        Optional<ProjectScholarshipHolder> vinculoParaEstenderInicio = vinculosExistentes.stream().filter(v -> {
            YearMonth dataInicio = YearMonth
                    .from(v.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            return competenciaAtual.equals(dataInicio.minusMonths(1));
        }).findFirst();

        // --- ANÁLISE DOS CENÁRIOS ---

        if (vinculoParaEstenderFim.isPresent() && vinculoParaEstenderInicio.isPresent()) {
            // CENÁRIO 1: A competência serve como PONTE entre dois vínculos. Vamos uni-los.
            ProjectScholarshipHolder vinculoAnterior = vinculoParaEstenderFim.get();
            ProjectScholarshipHolder vinculoPosterior = vinculoParaEstenderInicio.get();

            System.out.println("      [AÇÃO] -> PONTE DETECTADA: Unindo dois vínculos separados.");
            System.out.println("        - Vínculo 1: de " + vinculoAnterior.getStartDate() + " a "
                    + vinculoAnterior.getStartDate());
            System.out.println("        - Vínculo 2: de " + vinculoPosterior.getStartDate() + " a "
                    + vinculoPosterior.getEndDate());

            // Atualiza a data final do primeiro vínculo para a data final do segundo
            vinculoAnterior.setEndDate(vinculoPosterior.getEndDate());
            projetoHasBolsistaService.salvar(vinculoAnterior);

            projetoHasBolsistaService.deletar(vinculoPosterior);
            System.out.println("      [SUCCESS] Vínculos unidos com sucesso.");

        } else if (vinculoParaEstenderFim.isPresent()) {
            // CENÁRIO 2: Apenas estende um vínculo para FRENTE.
            ProjectScholarshipHolder vinculo = vinculoParaEstenderFim.get();
            System.out.println(
                    "      [AÇÃO] -> CONDIÇÃO VERDADEIRA: Meses seguidos. ATUALIZANDO data final do vínculo existente.");
            vinculo.setEndDate(converterCompetenciaParaDate(dados.getCompetencia(), true));
            projetoHasBolsistaService.salvar(vinculo);

        } else if (vinculoParaEstenderInicio.isPresent()) {
            // CENÁRIO 3: Apenas estende um vínculo para TRÁS (sua nova regra).
            ProjectScholarshipHolder vinculo = vinculoParaEstenderInicio.get();
            System.out.println(
                    "      [AÇÃO] -> CONDIÇÃO VERDADEIRA: Meses seguidos. ATUALIZANDO data início do vínculo existente.");
            vinculo.setStartDate(converterCompetenciaParaDate(dados.getCompetencia(), false));
            projetoHasBolsistaService.salvar(vinculo);

        } else {
            // CENÁRIO 4: Não é contíguo a nenhum vínculo existente. Cria um novo.
            System.out.println(
                    "      [AÇÃO] -> GAP DETECTADO: A competência não é contígua a nenhum vínculo. CRIANDO novo vínculo.");
            criarNovoVinculo(bolsista, projeto, dados);
        }
    }

    private void criarNovoBolsistaComVinculo(ProjectPartner projeto, DadosPagamento dados) {
        System.out.println("  [AÇÃO] -> Entrou em criarNovoBolsistaComVinculo.");
        ScholarshipHolder novoBolsista = new ScholarshipHolder(dados.getNomeMembro());
        if ("docente".equalsIgnoreCase(dados.getTipo())) {
            novoBolsista.setIsTeacher(true);
        } else {
            novoBolsista.setIsTeacher(false);
            novoBolsista.setEducationLevel(dados.getNivel());
        }
        System.out.println("    [AÇÃO] Salvando novo registro de ScholarshipHolder para: " + novoBolsista.getName());
        salvar(novoBolsista);

        criarNovoVinculo(novoBolsista, projeto, dados);
    }

    private void criarNovoVinculo(ScholarshipHolder bolsista, ProjectPartner projeto, DadosPagamento dados) {
        System.out.println("    [AÇÃO] -> Entrou em criarNovoVinculo para: " + bolsista.getName());
        ProjectScholarshipHolderId novoId = new ProjectScholarshipHolderId(projeto.getId(), bolsista.getId());

        Date dataInicio = converterCompetenciaParaDate(dados.getCompetencia(), false);
        Date dataFim = converterCompetenciaParaDate(dados.getCompetencia(), true);

        System.out.println("      [DEBUG] Detalhes do novo vínculo (ProjetoHasBolsista):");
        System.out.println("        - Data Início: " + dataInicio);
        System.out.println("        - Data Fim: " + dataFim);
        System.out.println("        - Valor: " + dados.getValor());

        ProjectScholarshipHolder novoVinculo = new ProjectScholarshipHolder(
                novoId,
                projeto,
                bolsista,
                dados.getNumeroRubrica(),
                dados.getValor(),
                dataInicio,
                dataFim,
                dados.getCargaHoraria());

        System.out.println("      [AÇÃO] Salvando o novo vínculo no banco de dados...");
        projetoHasBolsistaService.salvar(novoVinculo);
    }

    private static Date converterCompetenciaParaDate(String competencia, boolean fimDoMes) {
        if (competencia == null || competencia.trim().isEmpty()) {
            throw new IllegalArgumentException("A string da competência está vazia ou nula.");
        }

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("[MMM/yyyy][M/yyyy][MM/yyyy]") // Tenta "jun/2025", depois "6/2025", depois "06/2025"
                .toFormatter(new Locale("pt", "BR"));

        try {
            YearMonth yearMonth = YearMonth.parse(competencia.toLowerCase(), formatter);
            LocalDate localDate = fimDoMes ? yearMonth.atEndOfMonth() : yearMonth.atDay(1);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            // Se mesmo o formatador flexível falhar, lança um erro claro.
            throw new RuntimeException(
                    "Formato de competência inválido: '" + competencia
                            + "'. Formatos aceitos são 'jun/2025', '6/2025' ou '06/2025'.",
                    e);
        }
    }

    private DadosPagamento parseCsvRecord(CSVRecord csvRecord) {
        DadosPagamento dados = new DadosPagamento();

        // --- Tratamento do NÚMERO DO PROJETO (já corrigido) ---
        String projetoCompleto = csvRecord.get("Projeto");
        String numeroProjeto = projetoCompleto;
        if (projetoCompleto != null && projetoCompleto.contains(" - ")) {
            numeroProjeto = projetoCompleto.split(" - ")[0].trim();
        }
        dados.setNumeroProjeto(numeroProjeto);
        String membroCompleto = csvRecord.get("Membro");
        String nomeMembro = membroCompleto; // Define um valor padrão

        if (membroCompleto != null && membroCompleto.contains(" - ")) {
            nomeMembro = membroCompleto.split(" - ")[1].trim();
        }
        dados.setNomeMembro(nomeMembro);
        dados.setNumeroRubrica(extrairNumeroDaRubrica(csvRecord.get("Rubrica")));
        dados.setNivel(csvRecord.get("Nível"));
        dados.setTipo(csvRecord.get("Tipo"));
        dados.setCargaHoraria(parseCargaHoraria(csvRecord.get("Carga Horária")));
        dados.setValor(Double.parseDouble(csvRecord.get("Valor (R$)").replace(".", "").replace(",", ".")));
        dados.setCompetencia(csvRecord.get("Competência"));

        return dados;
    }

    private int parseCargaHoraria(String cargaHorariaStr) {
        if (cargaHorariaStr != null && !cargaHorariaStr.trim().isEmpty()) {
            try {
                return Integer.parseInt(cargaHorariaStr.trim());
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter Carga Horária: '" + cargaHorariaStr + "' para inteiro.");

                return 0;
            }
        }
        return 0; // Retorna 0 ou um valor padrão se a string for vazia ou nula
    }

    private Integer extrairNumeroDaRubrica(String rubricaCompleta) {
        if (rubricaCompleta == null || rubricaCompleta.trim().isEmpty()) {
            throw new NumberFormatException("A string da rubrica está vazia ou nula.");
        }

        // A expressão regular continua a mesma, para encontrar o número principal
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b(\\d{6,})\\b");
        java.util.regex.Matcher matcher = pattern.matcher(rubricaCompleta);

        if (matcher.find()) {
            // 1. Pega o número completo como antes (ex: "339018")
            int numeroCompleto = Integer.parseInt(matcher.group(1));

            // 2. USA O OPERADOR MÓDULO (%) PARA PEGAR APENAS OS DOIS ÚLTIMOS DÍGITOS
            // Exemplo: 339018 % 100 = 18
            return numeroCompleto % 100;
        }

        // Se não encontrou o padrão específico, lança um erro claro.
        throw new NumberFormatException(
                "Não foi possível extrair um número de rubrica válido da string: '" + rubricaCompleta + "'");
    }

}