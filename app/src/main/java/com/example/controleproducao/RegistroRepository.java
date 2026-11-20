package com.example.controleproducao;

import java.util.ArrayList;
import java.util.List;

/**
 * Armazena os registros em memória (ArrayList) e concentra as operações de CRUD.
 * Uma única instância é compartilhada por todas as Activities.
 */
public class RegistroRepository {

    private static RegistroRepository instancia;

    private final List<Registro> registros = new ArrayList<>();
    private int proximoId = 1;

    /** Package-private para permitir instâncias isoladas nos testes. */
    RegistroRepository() {
    }

    public static RegistroRepository getInstance() {
        if (instancia == null) {
            instancia = new RegistroRepository();
        }
        return instancia;
    }

    /** CREATE: grava um novo registro e define seu id. */
    public void adicionar(Registro registro) {
        registro.setId(proximoId++);
        registros.add(registro);
    }

    /** READ: retorna todos os registros. */
    public List<Registro> listar() {
        return registros;
    }

    /** READ: retorna o registro com o id informado, ou null se não existir. */
    public Registro buscarPorId(int id) {
        for (Registro registro : registros) {
            if (registro.getId() == id) {
                return registro;
            }
        }
        return null;
    }

    /** UPDATE: substitui os dados do registro existente. */
    public boolean atualizar(int id, String titulo, String setor, String descricao, boolean concluido) {
        Registro registro = buscarPorId(id);
        if (registro == null) {
            return false;
        }
        registro.setTitulo(titulo);
        registro.setSetor(setor);
        registro.setDescricao(descricao);
        registro.setConcluido(concluido);
        return true;
    }

    /** DELETE: remove o registro com o id informado. */
    public boolean remover(int id) {
        Registro registro = buscarPorId(id);
        return registro != null && registros.remove(registro);
    }
}
