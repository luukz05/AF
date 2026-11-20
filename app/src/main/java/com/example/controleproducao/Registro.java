package com.example.controleproducao;

/**
 * Modelo de dados: uma informação operacional da produção
 * (registro, item de lista de controle ou anotação).
 */
public class Registro {

    private int id;
    private String titulo;
    private String setor;
    private String descricao;
    private boolean concluido;

    public Registro(String titulo, String setor, String descricao, boolean concluido) {
        this.titulo = titulo;
        this.setor = setor;
        this.descricao = descricao;
        this.concluido = concluido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

    /** Texto exibido em cada linha da ListView. */
    @Override
    public String toString() {
        String situacao = concluido ? "Concluído" : "Pendente";
        return titulo + "\n" + setor + " · " + situacao;
    }
}
