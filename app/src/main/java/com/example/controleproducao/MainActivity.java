package com.example.controleproducao;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * Activity principal (tela inicial / painel).
 *
 * <p>Exibe um resumo dos registros armazenados em memória e dá acesso às telas de
 * cadastro e de listagem por meio de Intents explícitas.</p>
 *
 * <p>Os métodos do ciclo de vida são sobrescritos e registrados no Logcat (tag
 * {@value #TAG}) para evidenciar o fluxo. O resumo é recalculado em {@link #onResume()},
 * garantindo que os números estejam sempre atualizados ao retornar de outra tela.</p>
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    /** Intervalo máximo (ms) entre dois toques em "voltar" para fechar o app. */
    private static final long INTERVALO_SAIDA_MS = 2000L;

    private RegistroRepository repositorio;

    private TextView tvTotal;
    private TextView tvPendentes;
    private TextView tvConcluidos;
    private TextView tvAlerta;
    private MaterialCardView cardAlerta;

    private long ultimoToqueVoltar = 0L;

    // =====================================================================
    // Ciclo de vida
    // =====================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        repositorio = RegistroRepository.getInstance();

        vincularViews();
        configurarBotoes();
        configurarBotaoVoltar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - atualizando painel");
        // Sempre que a tela volta ao primeiro plano os números são recalculados
        atualizarPainel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    // =====================================================================
    // Configuração da tela
    // =====================================================================

    private void vincularViews() {
        tvTotal = findViewById(R.id.tv_total);
        tvPendentes = findViewById(R.id.tv_pendentes);
        tvConcluidos = findViewById(R.id.tv_concluidos);
        tvAlerta = findViewById(R.id.tv_alerta);
        cardAlerta = findViewById(R.id.card_alerta);
    }

    private void configurarBotoes() {
        MaterialButton btnNovoRegistro = findViewById(R.id.btn_novo_registro);
        MaterialButton btnVerRegistros = findViewById(R.id.btn_ver_registros);

        // Intent explícita para a tela de cadastro (modo inclusão)
        btnNovoRegistro.setOnClickListener(v ->
                startActivity(CadastroActivity.criarIntentNovo(this)));

        // Intent explícita para a tela de listagem
        btnVerRegistros.setOnClickListener(v ->
                startActivity(ListagemActivity.criarIntent(this)));

        // Tocar no alerta leva direto à lista
        cardAlerta.setOnClickListener(v ->
                startActivity(ListagemActivity.criarIntent(this)));
    }

    /** Exige dois toques em "voltar" para sair, evitando fechamento acidental. */
    private void configurarBotaoVoltar() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                long agora = System.currentTimeMillis();
                if (agora - ultimoToqueVoltar < INTERVALO_SAIDA_MS) {
                    finish();
                } else {
                    ultimoToqueVoltar = agora;
                    Toast.makeText(MainActivity.this, R.string.msg_voltar_para_sair,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /** Lê os totais do repositório e atualiza os cartões do painel. */
    private void atualizarPainel() {
        tvTotal.setText(String.valueOf(repositorio.contarTotal()));
        tvPendentes.setText(String.valueOf(repositorio.contarPendentes()));
        tvConcluidos.setText(String.valueOf(repositorio.contarConcluidos()));

        int criticos = repositorio.contarPendentesPrioridadeAlta();
        if (criticos > 0) {
            cardAlerta.setVisibility(View.VISIBLE);
            tvAlerta.setText(getResources().getQuantityString(
                    R.plurals.alerta_prioridade_alta, criticos, criticos));
        } else {
            cardAlerta.setVisibility(View.GONE);
        }
    }
}
