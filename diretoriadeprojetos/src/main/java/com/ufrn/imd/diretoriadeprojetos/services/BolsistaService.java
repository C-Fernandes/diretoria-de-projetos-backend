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
import com.ufrn.imd.diretoriadeprojetos.dtos.response.BolsistaResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Bolsista;
import com.ufrn.imd.diretoriadeprojetos.models.Projeto;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoHasBolsista;
import com.ufrn.imd.diretoriadeprojetos.models.ProjetoParceiro;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoHasBolsistaId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoId;
import com.ufrn.imd.diretoriadeprojetos.models.ids.ProjetoParceiroId;
import com.ufrn.imd.diretoriadeprojetos.repository.BolsistaRepository;
import com.ufrn.imd.diretoriadeprojetos.repository.ProjetoHasBolsistaRepository;

import jakarta.transaction.Transactional;

@Service
public class BolsistaService {

    @Autowired
    private BolsistaRepository bolsistaRepository;

    @Autowired
    private ProjetoHasBolsistaService projetoHasBolsistaService;

    @Autowired
    private ProjetoParceiroService projetoParceiroService;
    private static final DateTimeFormatter FORMATADOR_COMPETENCIA_FLEXIVEL = new DateTimeFormatterBuilder()
            .appendPattern("[MMM/yyyy][M/yyyy][MM/yyyy]") // Tenta "abr/2025", "4/2025" e "04/2025"
            .toFormatter(new Locale("pt", "BR"));

    public List<BolsistaResponse> listarTodos() {
        List<Bolsista> bolsistas = bolsistaRepository.findAll();

        return bolsistas.stream() // Inicia um fluxo de objetos Bolsista
                .flatMap(bolsista -> bolsista.getProjetos().stream())
                .map(vinculo -> {
                    BolsistaResponse dto = new BolsistaResponse();
                    Bolsista bolsista = vinculo.getBolsista();
                    ProjetoParceiro projetoParceiro = vinculo.getProjetoParceiro();

                    // Mapeia os dados do Bolsista
                    if (bolsista != null) {
                        dto.setId(bolsista.getId());
                        dto.setCpf(bolsista.getCpf());
                        dto.setNome(bolsista.getNome());
                        dto.setEmail(bolsista.getEmail());
                        dto.setTipoSuperior(bolsista.getTipoSuperior());
                        dto.setCurso(bolsista.getCurso());
                        dto.setDocente(bolsista.getDocente());
                        dto.setFormacao(bolsista.getFormacao());
                    }

                    dto.setDataInicio(vinculo.getDataInicio());
                    dto.setDataFim(vinculo.getDataFim());
                    dto.setRubrica(vinculo.getRubrica());
                    dto.setValor(vinculo.getValor());
                    dto.setCHSemanal(vinculo.getCHSemanal());

                    if (projetoParceiro != null && projetoParceiro.getProjeto() != null) {
                        dto.setNumeroFunpec(projetoParceiro.getNumeroFunpec());
                        if (projetoParceiro.getProjeto().getId() != null) {
                            dto.setNumeroSipac(projetoParceiro.getProjeto().getId().getNumeroSipac());
                            dto.setAnoSipac(projetoParceiro.getProjeto().getId().getAnoSipac());
                        }
                    }

                    return dto;

                }).collect(Collectors.toList());
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

        if (associacoes == null || associacoes.isEmpty()) {
            return Collections.emptyList(); // Ou new ArrayList<>();
        }

        return associacoes.stream()
                .map(ProjetoHasBolsista::getBolsista)
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
                Optional<ProjetoParceiro> projetoOpt = projetoParceiroService
                        .findByNumeroFunpec(Long.parseLong(dados.getNumeroProjeto()));

                if (!projetoOpt.isPresent()) {
                    System.err.println("Projeto " + dados.getNumeroProjeto() + " não encontrado");
                    continue;
                } else {
                    System.err.println("Projeto " + dados.getNumeroProjeto() + " encontrado");

                }
                ProjetoParceiro projeto = projetoOpt.get();

                System.out.println("[DEBUG] Projeto encontrado: " + projeto.getProjeto().getTitulo());

                // 2. Busca o bolsista pelo nome.
                System.out.println("[DEBUG] Buscando bolsista com nome: '" + dados.getNomeMembro() + "'");
                Optional<Bolsista> bolsistaOpt = findByNome(dados.getNomeMembro());

                if (bolsistaOpt.isPresent()) {
                    System.out.println("[FLUXO] Bolsista JÁ EXISTE. Entrando no fluxo de gerenciamento de vínculo...");
                    gerenciarVinculoExistente(bolsistaOpt.get(), projeto, dados);
                } else {
                    System.out.println("[FLUXO] Bolsista NÃO EXISTE. Entrando no fluxo de criação de novo bolsista...");
                    criarNovoBolsistaComVinculo(projeto, dados);
                }
                pagamentosProcessados.add(dados);
                System.out.println("[SUCCESS] Linha " + csvRecord.getRecordNumber() + " processada com sucesso.");
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

    private void gerenciarVinculoExistente(Bolsista bolsista, ProjetoParceiro projeto, DadosPagamento dados) {
        System.out.println("  [FLUXO] -> Entrou em gerenciarVinculoExistente para: " + bolsista.getNome());

        // Converte a competência do CSV para o formato YearMonth
        YearMonth competenciaAtual = YearMonth.parse(dados.getCompetencia().toLowerCase(),
                FORMATADOR_COMPETENCIA_FLEXIVEL);
        System.out.println("    [DEBUG] Competência atual do CSV: " + competenciaAtual);
        System.out.println("    [DEBUG] Buscando TODOS os vínculos do bolsista com o projeto...");

        // Pega TODOS os vínculos, não apenas o mais recente
        List<ProjetoHasBolsista> vinculosExistentes = projetoHasBolsistaService
                .findBolsistaInProjeto(bolsista, projeto.getNumeroFunpec())
                .orElse(new ArrayList<>()); // Garante uma lista vazia se não houver nada

        if (vinculosExistentes.isEmpty()) {
            System.out.println("    [DEBUG] Nenhuma relação anterior encontrada para este bolsista no projeto.");
            System.out.println("      [AÇÃO] -> CRIANDO primeiro vínculo para bolsista já existente.");
            criarNovoVinculo(bolsista, projeto, dados);
            return; // Finaliza a execução para esta linha do CSV
        }

        // Procura por um vínculo que pode ser estendido PARA FRENTE
        Optional<ProjetoHasBolsista> vinculoParaEstenderFim = vinculosExistentes.stream().filter(v -> {
            YearMonth dataFim = YearMonth.from(v.getDataFim().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            return competenciaAtual.equals(dataFim.plusMonths(1));
        }).findFirst();

        // Procura por um vínculo que pode ser estendido PARA TRÁS
        Optional<ProjetoHasBolsista> vinculoParaEstenderInicio = vinculosExistentes.stream().filter(v -> {
            YearMonth dataInicio = YearMonth
                    .from(v.getDataInicio().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            return competenciaAtual.equals(dataInicio.minusMonths(1));
        }).findFirst();

        // --- ANÁLISE DOS CENÁRIOS ---

        if (vinculoParaEstenderFim.isPresent() && vinculoParaEstenderInicio.isPresent()) {
            // CENÁRIO 1: A competência serve como PONTE entre dois vínculos. Vamos uni-los.
            ProjetoHasBolsista vinculoAnterior = vinculoParaEstenderFim.get();
            ProjetoHasBolsista vinculoPosterior = vinculoParaEstenderInicio.get();

            System.out.println("      [AÇÃO] -> PONTE DETECTADA: Unindo dois vínculos separados.");
            System.out.println("        - Vínculo 1: de " + vinculoAnterior.getDataInicio() + " a "
                    + vinculoAnterior.getDataFim());
            System.out.println("        - Vínculo 2: de " + vinculoPosterior.getDataInicio() + " a "
                    + vinculoPosterior.getDataFim());

            // Atualiza a data final do primeiro vínculo para a data final do segundo
            vinculoAnterior.setDataFim(vinculoPosterior.getDataFim());
            projetoHasBolsistaService.salvar(vinculoAnterior);

            projetoHasBolsistaService.deletar(vinculoPosterior);
            System.out.println("      [SUCCESS] Vínculos unidos com sucesso.");

        } else if (vinculoParaEstenderFim.isPresent()) {
            // CENÁRIO 2: Apenas estende um vínculo para FRENTE.
            ProjetoHasBolsista vinculo = vinculoParaEstenderFim.get();
            System.out.println(
                    "      [AÇÃO] -> CONDIÇÃO VERDADEIRA: Meses seguidos. ATUALIZANDO data final do vínculo existente.");
            vinculo.setDataFim(converterCompetenciaParaDate(dados.getCompetencia(), true));
            projetoHasBolsistaService.salvar(vinculo);

        } else if (vinculoParaEstenderInicio.isPresent()) {
            // CENÁRIO 3: Apenas estende um vínculo para TRÁS (sua nova regra).
            ProjetoHasBolsista vinculo = vinculoParaEstenderInicio.get();
            System.out.println(
                    "      [AÇÃO] -> CONDIÇÃO VERDADEIRA: Meses seguidos. ATUALIZANDO data início do vínculo existente.");
            vinculo.setDataInicio(converterCompetenciaParaDate(dados.getCompetencia(), false));
            projetoHasBolsistaService.salvar(vinculo);

        } else {
            // CENÁRIO 4: Não é contíguo a nenhum vínculo existente. Cria um novo.
            System.out.println(
                    "      [AÇÃO] -> GAP DETECTADO: A competência não é contígua a nenhum vínculo. CRIANDO novo vínculo.");
            criarNovoVinculo(bolsista, projeto, dados);
        }
    }

    private void criarNovoBolsistaComVinculo(ProjetoParceiro projeto, DadosPagamento dados) {
        System.out.println("  [AÇÃO] -> Entrou em criarNovoBolsistaComVinculo.");
        Bolsista novoBolsista = new Bolsista(dados.getNomeMembro());
        if ("docente".equalsIgnoreCase(dados.getTipo())) {
            novoBolsista.setDocente(true);
        } else {
            novoBolsista.setDocente(false);
            novoBolsista.setTipoSuperior(dados.getNivel());
        }
        System.out.println("    [AÇÃO] Salvando novo registro de Bolsista para: " + novoBolsista.getNome());
        salvar(novoBolsista);

        criarNovoVinculo(novoBolsista, projeto, dados);
    }

    private void criarNovoVinculo(Bolsista bolsista, ProjetoParceiro projeto, DadosPagamento dados) {
        System.out.println("    [AÇÃO] -> Entrou em criarNovoVinculo para: " + bolsista.getNome());
        ProjetoHasBolsistaId novoId = new ProjetoHasBolsistaId(projeto.getId(), bolsista.getId());

        Date dataInicio = converterCompetenciaParaDate(dados.getCompetencia(), false);
        Date dataFim = converterCompetenciaParaDate(dados.getCompetencia(), true);

        System.out.println("      [DEBUG] Detalhes do novo vínculo (ProjetoHasBolsista):");
        System.out.println("        - Data Início: " + dataInicio);
        System.out.println("        - Data Fim: " + dataFim);
        System.out.println("        - Valor: " + dados.getValor());

        ProjetoHasBolsista novoVinculo = new ProjetoHasBolsista(
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