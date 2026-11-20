package com.example.controleproducao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Camada de dados do aplicativo: concentra TODAS as operações de CRUD
 * (Create, Read, Update, Delete) sobre os registros.
 *
 * <p>Os dados ficam armazenados exclusivamente em memória, em um {@link ArrayList},
 * conforme exigido pelo projeto (ambiente sem rede/internet e sem banco de dados).
 * É implementado como Singleton para que todas as Activities compartilhem a mesma lista
 * enquanto o processo do aplicativo estiver ativo.</p>
 */
public final class RegistroRepository {

    /** Filtros de situação disponíveis na tela de listagem. */
    public enum FiltroStatus {
        TODOS, PENDENTES, CONCLUIDOS
    }

    private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");
    private static final String FORMATO_DATA = "dd/MM/yyyy HH:mm";

    private static RegistroRepository instancia;

    /** Estrutura de armazenamento em memória. */
    private final List<Registro> registros = new ArrayList<>();

    /** Gerador sequencial de identificadores. */
    private int proximoId = 1;

    /**
     * Construtor package-private (permite criar instâncias isoladas nos testes unitários).
     *
     * @param carregarExemplos se {@code true}, insere alguns registros de demonstração.
     */
    RegistroRepository(boolean carregarExemplos) {
        if (carregarExemplos) {
            carregarRegistrosDeExemplo();
        }
    }

    /** Retorna a instância única compartilhada por todo o aplicativo. */
    public static synchronized RegistroRepository getInstance() {
        if (instancia == null) {
            instancia = new RegistroRepository(true);
        }
        return instancia;
    }

    // =====================================================================
    // CREATE
    // =====================================================================

    /**
     * Grava um novo registro, atribuindo id e data de criação.
     *
     * @return o próprio registro já com o id preenchido.
     */
    public Registro adicionar(Registro registro) {
        if (registro == null) {
            throw new IllegalArgumentException("Registro não pode ser nulo");
        }
        registro.setId(proximoId++);
        registro.setDataCriacao(dataAtualFormatada());
        registros.add(registro);
        return registro;
    }

    // =====================================================================
    // READ
    // =====================================================================

    /** @return cópia da lista com todos os registros, do mais recente para o mais antigo. */
    public List<Registro> listarTodos() {
        List<Registro> copia = new ArrayList<>(registros.size());
        for (int i = registros.size() - 1; i >= 0; i--) {
            copia.add(registros.get(i));
        }
        return copia;
    }

    /** @return o registro com o id informado ou {@code null} caso não exista. */
    public Registro buscarPorId(int id) {
        for (Registro registro : registros) {
            if (registro.getId() == id) {
                return registro;
            }
        }
        return null;
    }

    /**
     * Busca registros por texto livre (título, descrição, setor, tipo ou responsável)
     * e por situação (pendente/concluído).
     */
    public List<Registro> filtrar(String texto, FiltroStatus status) {
        String termo = texto == null ? "" : texto.trim().toLowerCase(LOCALE_BR);
        List<Registro> resultado = new ArrayList<>();
        for (Registro registro : listarTodos()) {
            if (atendeStatus(registro, status) && contemTermo(registro, termo)) {
                resultado.add(registro);
            }
        }
        return resultado;
    }

    public int contarTotal() {
        return registros.size();
    }

    public int contarConcluidos() {
        int total = 0;
        for (Registro registro : registros) {
            if (registro.isConcluido()) {
                total++;
            }
        }
        return total;
    }

    public int contarPendentes() {
        return contarTotal() - contarConcluidos();
    }

    /** @return quantidade de registros pendentes com prioridade alta (usado no painel inicial). */
    public int contarPendentesPrioridadeAlta() {
        int total = 0;
        for (Registro registro : registros) {
            if (!registro.isConcluido() && Registro.PRIORIDADE_ALTA.equals(registro.getPrioridade())) {
                total++;
            }
        }
        return total;
    }

    // =====================================================================
    // UPDATE
    // =====================================================================

    /**
     * Atualiza os dados de um registro existente.
     *
     * @param id    identificador do registro a ser alterado.
     * @param dados objeto com os novos valores.
     * @return {@code true} se o registro foi encontrado e atualizado.
     */
    public boolean atualizar(int id, Registro dados) {
        Registro existente = buscarPorId(id);
        if (existente == null || dados == null) {
            return false;
        }
        existente.copiarDadosDe(dados);
        existente.setDataAtualizacao(dataAtualFormatada());
        return true;
    }

    /**
     * Inverte a situação do registro (pendente ⇄ concluído).
     *
     * @return {@code true} se o registro foi encontrado.
     */
    public boolean alternarConclusao(int id) {
        Registro existente = buscarPorId(id);
        if (existente == null) {
            return false;
        }
        existente.setConcluido(!existente.isConcluido());
        existente.setDataAtualizacao(dataAtualFormatada());
        return true;
    }

    // =====================================================================
    // DELETE
    // =====================================================================

    /** @return {@code true} se o registro existia e foi removido. */
    public boolean remover(int id) {
        Iterator<Registro> iterator = registros.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    // =====================================================================
    // Métodos auxiliares privados
    // =====================================================================

    private static boolean atendeStatus(Registro registro, FiltroStatus status) {
        if (status == null || status == FiltroStatus.TODOS) {
            return true;
        }
        return status == FiltroStatus.CONCLUIDOS ? registro.isConcluido() : !registro.isConcluido();
    }

    private static boolean contemTermo(Registro registro, String termo) {
        if (termo.isEmpty()) {
            return true;
        }
        return contem(registro.getTitulo(), termo)
                || contem(registro.getDescricao(), termo)
                || contem(registro.getSetor(), termo)
                || contem(registro.getTipo(), termo)
                || contem(registro.getResponsavel(), termo);
    }

    private static boolean contem(String campo, String termo) {
        return campo != null && campo.toLowerCase(LOCALE_BR).contains(termo);
    }

    private static String dataAtualFormatada() {
        return new SimpleDateFormat(FORMATO_DATA, LOCALE_BR).format(new Date());
    }

    /** Registros de demonstração para facilitar a apresentação do aplicativo. */
    private void carregarRegistrosDeExemplo() {
        adicionar(new Registro("Checklist de partida da linha 2", "Montagem",
                Registro.TIPO_CHECKLIST, Registro.PRIORIDADE_ALTA, "Carlos Lima",
                "Verificar EPIs, nível de óleo, sensores de segurança e botão de emergência antes de iniciar o turno.",
                false));
        adicionar(new Registro("Troca de ferramenta - torno CNC 04", "Usinagem",
                Registro.TIPO_REGISTRO, Registro.PRIORIDADE_MEDIA, "Ana Souza",
                "Pastilha de desbaste substituída após 1.200 peças. Próxima troca prevista em 1.000 peças.",
                true));
        adicionar(new Registro("Lote 3471 aguardando inspeção", "Qualidade",
                Registro.TIPO_ANOTACAO, Registro.PRIORIDADE_BAIXA, "Marcos Reis",
                "Separar 5 amostras para inspeção dimensional antes da liberação para expedição.",
                false));
    }
}
