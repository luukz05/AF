package com.example.controleproducao;

/**
 * Modelo de dados (entidade) que representa uma informação operacional da produção:
 * um registro, uma lista de controle (checklist) ou uma anotação.
 *
 * <p>Os objetos desta classe são mantidos apenas em memória pelo {@link RegistroRepository}.</p>
 */
public class Registro {

    // ---------------------------------------------------------------------
    // Constantes de domínio (evitam "strings mágicas" espalhadas pelo código)
    // ---------------------------------------------------------------------
    public static final String TIPO_REGISTRO = "Registro";
    public static final String TIPO_CHECKLIST = "Checklist";
    public static final String TIPO_ANOTACAO = "Anotação";

    public static final String PRIORIDADE_BAIXA = "Baixa";
    public static final String PRIORIDADE_MEDIA = "Média";
    public static final String PRIORIDADE_ALTA = "Alta";

    /** Valor usado enquanto o registro ainda não foi gravado no repositório. */
    public static final int ID_NAO_DEFINIDO = 0;

    // ---------------------------------------------------------------------
    // Atributos
    // ---------------------------------------------------------------------
    private int id = ID_NAO_DEFINIDO;
    private String titulo;
    private String setor;
    private String tipo;
    private String prioridade;
    private String responsavel;
    private String descricao;
    private boolean concluido;
    private String dataCriacao;
    private String dataAtualizacao;

    /**
     * Cria um registro com os dados informados pelo usuário.
     * O id e as datas são definidos pelo {@link RegistroRepository} no momento da gravação.
     */
    public Registro(String titulo, String setor, String tipo, String prioridade,
                    String responsavel, String descricao, boolean concluido) {
        this.titulo = titulo;
        this.setor = setor;
        this.tipo = tipo;
        this.prioridade = prioridade;
        this.responsavel = responsavel;
        this.descricao = descricao;
        this.concluido = concluido;
    }

    /**
     * Copia os dados editáveis de outro registro para este (usado na atualização).
     * Id e data de criação são preservados.
     */
    void copiarDadosDe(Registro origem) {
        this.titulo = origem.titulo;
        this.setor = origem.setor;
        this.tipo = origem.tipo;
        this.prioridade = origem.prioridade;
        this.responsavel = origem.responsavel;
        this.descricao = origem.descricao;
        this.concluido = origem.concluido;
    }

    // ---------------------------------------------------------------------
    // Getters e setters
    // (setters de id/datas são package-private: só o repositório os altera)
    // ---------------------------------------------------------------------
    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSetor() {
        return setor;
    }

    public String getTipo() {
        return tipo;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isConcluido() {
        return concluido;
    }

    void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    /** @return data da última alteração ou {@code null} se o registro nunca foi alterado. */
    public String getDataAtualizacao() {
        return dataAtualizacao;
    }

    void setDataAtualizacao(String dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    @Override
    public String toString() {
        return "Registro{id=" + id + ", titulo='" + titulo + "'}";
    }
}
