package com.example.controleproducao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Testes unitários (executados na JVM) das operações de CRUD do {@link RegistroRepository}.
 */
public class RegistroRepositoryTest {

    private RegistroRepository repositorio;

    @Before
    public void setUp() {
        // Instância isolada e vazia para cada teste
        repositorio = new RegistroRepository(false);
    }

    private Registro novoRegistro(String titulo, String prioridade, boolean concluido) {
        return new Registro(titulo, "Montagem", Registro.TIPO_REGISTRO, prioridade,
                "Operador", "Descrição de teste", concluido);
    }

    @Test
    public void adicionar_atribuiIdSequencialEDataDeCriacao() {
        Registro primeiro = repositorio.adicionar(novoRegistro("Primeiro", Registro.PRIORIDADE_BAIXA, false));
        Registro segundo = repositorio.adicionar(novoRegistro("Segundo", Registro.PRIORIDADE_BAIXA, false));

        assertEquals(1, primeiro.getId());
        assertEquals(2, segundo.getId());
        assertNotNull(primeiro.getDataCriacao());
        assertNull(primeiro.getDataAtualizacao());
        assertEquals(2, repositorio.contarTotal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void adicionar_nulo_lancaExcecao() {
        repositorio.adicionar(null);
    }

    @Test
    public void listarTodos_retornaDoMaisRecenteParaOMaisAntigo() {
        repositorio.adicionar(novoRegistro("A", Registro.PRIORIDADE_BAIXA, false));
        repositorio.adicionar(novoRegistro("B", Registro.PRIORIDADE_BAIXA, false));

        List<Registro> lista = repositorio.listarTodos();
        assertEquals("B", lista.get(0).getTitulo());
        assertEquals("A", lista.get(1).getTitulo());
    }

    @Test
    public void buscarPorId_inexistente_retornaNulo() {
        assertNull(repositorio.buscarPorId(99));
    }

    @Test
    public void atualizar_alteraDadosEPreservaIdEDataCriacao() {
        Registro original = repositorio.adicionar(novoRegistro("Antigo", Registro.PRIORIDADE_BAIXA, false));
        String dataCriacao = original.getDataCriacao();

        boolean ok = repositorio.atualizar(original.getId(),
                novoRegistro("Novo título", Registro.PRIORIDADE_ALTA, true));

        Registro atualizado = repositorio.buscarPorId(original.getId());
        assertTrue(ok);
        assertEquals("Novo título", atualizado.getTitulo());
        assertEquals(Registro.PRIORIDADE_ALTA, atualizado.getPrioridade());
        assertTrue(atualizado.isConcluido());
        assertEquals(dataCriacao, atualizado.getDataCriacao());
        assertNotNull(atualizado.getDataAtualizacao());
    }

    @Test
    public void atualizar_inexistente_retornaFalse() {
        assertFalse(repositorio.atualizar(42, novoRegistro("X", Registro.PRIORIDADE_BAIXA, false)));
    }

    @Test
    public void remover_excluiApenasORegistroInformado() {
        Registro a = repositorio.adicionar(novoRegistro("A", Registro.PRIORIDADE_BAIXA, false));
        Registro b = repositorio.adicionar(novoRegistro("B", Registro.PRIORIDADE_BAIXA, false));

        assertTrue(repositorio.remover(a.getId()));
        assertFalse(repositorio.remover(a.getId()));
        assertNull(repositorio.buscarPorId(a.getId()));
        assertNotNull(repositorio.buscarPorId(b.getId()));
        assertEquals(1, repositorio.contarTotal());
    }

    @Test
    public void alternarConclusao_inverteSituacao() {
        Registro r = repositorio.adicionar(novoRegistro("A", Registro.PRIORIDADE_BAIXA, false));

        assertTrue(repositorio.alternarConclusao(r.getId()));
        assertTrue(repositorio.buscarPorId(r.getId()).isConcluido());
        assertTrue(repositorio.alternarConclusao(r.getId()));
        assertFalse(repositorio.buscarPorId(r.getId()).isConcluido());
        assertFalse(repositorio.alternarConclusao(999));
    }

    @Test
    public void contadores_refletemSituacaoEPrioridade() {
        repositorio.adicionar(novoRegistro("A", Registro.PRIORIDADE_ALTA, false));
        repositorio.adicionar(novoRegistro("B", Registro.PRIORIDADE_ALTA, true));
        repositorio.adicionar(novoRegistro("C", Registro.PRIORIDADE_BAIXA, false));

        assertEquals(3, repositorio.contarTotal());
        assertEquals(1, repositorio.contarConcluidos());
        assertEquals(2, repositorio.contarPendentes());
        assertEquals(1, repositorio.contarPendentesPrioridadeAlta());
    }

    @Test
    public void filtrar_porTextoIgnorandoMaiusculasEPorSituacao() {
        repositorio.adicionar(novoRegistro("Troca de óleo", Registro.PRIORIDADE_MEDIA, false));
        repositorio.adicionar(novoRegistro("Inspeção do lote", Registro.PRIORIDADE_MEDIA, true));

        assertEquals(1, repositorio.filtrar("ÓLEO", RegistroRepository.FiltroStatus.TODOS).size());
        assertEquals(2, repositorio.filtrar("", RegistroRepository.FiltroStatus.TODOS).size());
        assertEquals(2, repositorio.filtrar("montagem", null).size());
        assertEquals(1, repositorio.filtrar(null, RegistroRepository.FiltroStatus.CONCLUIDOS).size());
        assertEquals("Troca de óleo",
                repositorio.filtrar("", RegistroRepository.FiltroStatus.PENDENTES).get(0).getTitulo());
        assertTrue(repositorio.filtrar("inexistente", RegistroRepository.FiltroStatus.TODOS).isEmpty());
    }

    @Test
    public void instanciaPadrao_contemRegistrosDeExemplo() {
        assertTrue(new RegistroRepository(true).contarTotal() > 0);
    }
}
