package com.example.controleproducao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários do CRUD em memória.
 */
public class RegistroRepositoryTest {

    private RegistroRepository repositorio;

    @Before
    public void setUp() {
        repositorio = new RegistroRepository();
    }

    @Test
    public void crudCompleto() {
        Registro registro = new Registro("Troca de óleo", "Manutenção", "", false);

        repositorio.adicionar(registro);
        assertEquals(1, repositorio.listar().size());
        assertEquals(registro, repositorio.buscarPorId(registro.getId()));

        assertTrue(repositorio.atualizar(registro.getId(), "Troca de filtro", "Manutenção", "", true));
        assertEquals("Troca de filtro", repositorio.buscarPorId(registro.getId()).getTitulo());

        assertTrue(repositorio.remover(registro.getId()));
        assertNull(repositorio.buscarPorId(registro.getId()));
    }
}
