package com.example.controleproducao;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity de listagem (visualização) dos registros.
 *
 * <p>Mostra os registros em uma {@link ListView} com busca por texto e filtro por situação.
 * Um toque abre a {@link DetalheActivity}; um toque longo abre a edição direta na
 * {@link CadastroActivity}; o FAB cria um novo registro.</p>
 *
 * <p>A lista é recarregada em {@link #onResume()}, de modo que inclusões, alterações e
 * exclusões feitas em outras telas aparecem automaticamente ao retornar.</p>
 */
public class ListagemActivity extends AppCompatActivity {

    private static final String TAG = "ListagemActivity";

    private RegistroRepository repositorio;

    /** Lista exibida na tela (subconjunto filtrado dos dados do repositório). */
    private final List<Registro> registrosExibidos = new ArrayList<>();
    private RegistroAdapter adapter;

    private TextView tvContador;
    private TextView tvVazio;

    private String textoBusca = "";
    private RegistroRepository.FiltroStatus filtroAtual = RegistroRepository.FiltroStatus.TODOS;

    /** Intent explícita para abrir esta tela. */
    public static Intent criarIntent(Context context) {
        return new Intent(context, ListagemActivity.class);
    }

    // =====================================================================
    // Ciclo de vida
    // =====================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem);
        Log.d(TAG, "onCreate");

        repositorio = RegistroRepository.getInstance();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        configurarLista();
        configurarBusca();
        configurarFiltros();
        configurarBotaoNovo();

        // Mostra a dica apenas na primeira criação (não em rotações de tela)
        if (savedInstanceState == null) {
            mostrarDica();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - recarregando lista");
        atualizarLista();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    // =====================================================================
    // Configuração da tela
    // =====================================================================

    private void configurarLista() {
        ListView lvRegistros = findViewById(R.id.lv_registros);
        tvContador = findViewById(R.id.tv_contador);
        tvVazio = findViewById(R.id.tv_vazio);

        adapter = new RegistroAdapter(this, registrosExibidos);
        lvRegistros.setAdapter(adapter);
        lvRegistros.setEmptyView(findViewById(R.id.layout_vazio));

        // Toque simples: visualizar detalhes
        lvRegistros.setOnItemClickListener((parent, view, position, id) -> {
            Registro registro = registrosExibidos.get(position);
            startActivity(DetalheActivity.criarIntent(this, registro.getId()));
        });

        // Toque longo: atalho para edição
        lvRegistros.setOnItemLongClickListener((parent, view, position, id) -> {
            Registro registro = registrosExibidos.get(position);
            startActivity(CadastroActivity.criarIntentEdicao(this, registro.getId()));
            return true;
        });
    }

    private void configurarBusca() {
        TextInputEditText etBusca = findViewById(R.id.et_busca);
        etBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // não utilizado
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // não utilizado
            }

            @Override
            public void afterTextChanged(Editable s) {
                textoBusca = s.toString();
                atualizarLista();
            }
        });
    }

    private void configurarFiltros() {
        ChipGroup chipGroupFiltro = findViewById(R.id.chip_group_filtro);
        chipGroupFiltro.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int idSelecionado = checkedIds.isEmpty() ? R.id.chip_filtro_todos : checkedIds.get(0);
            if (idSelecionado == R.id.chip_filtro_pendentes) {
                filtroAtual = RegistroRepository.FiltroStatus.PENDENTES;
            } else if (idSelecionado == R.id.chip_filtro_concluidos) {
                filtroAtual = RegistroRepository.FiltroStatus.CONCLUIDOS;
            } else {
                filtroAtual = RegistroRepository.FiltroStatus.TODOS;
            }
            atualizarLista();
        });
    }

    private void configurarBotaoNovo() {
        ExtendedFloatingActionButton fabNovo = findViewById(R.id.fab_novo);
        fabNovo.setOnClickListener(v -> startActivity(CadastroActivity.criarIntentNovo(this)));
    }

    // =====================================================================
    // Atualização dos dados exibidos
    // =====================================================================

    /** Busca os registros no repositório aplicando busca e filtro atuais. */
    private void atualizarLista() {
        registrosExibidos.clear();
        registrosExibidos.addAll(repositorio.filtrar(textoBusca, filtroAtual));
        adapter.notifyDataSetChanged();

        int quantidade = registrosExibidos.size();
        tvContador.setText(getResources().getQuantityString(
                R.plurals.contador_registros, quantidade, quantidade));

        // Mensagem do estado vazio depende de haver ou não dados cadastrados
        boolean semDados = repositorio.contarTotal() == 0;
        tvVazio.setText(semDados ? R.string.lista_vazia : R.string.lista_sem_resultados);
    }

    /** Exibe uma dica rápida sobre os gestos disponíveis na lista. */
    private void mostrarDica() {
        Snackbar.make(findViewById(R.id.coordinator_listagem),
                        R.string.dica_listagem, Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab_novo)
                .setAction(R.string.acao_ok, v -> { /* apenas fecha a mensagem */ })
                .show();
    }
}
