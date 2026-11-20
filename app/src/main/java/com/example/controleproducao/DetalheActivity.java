package com.example.controleproducao;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * Activity de visualização detalhada de um registro (somente leitura).
 *
 * <p>Recebe o id do registro via Intent ({@link #EXTRA_REGISTRO_ID}) e permite:
 * alternar a situação (pendente/concluído, com opção de desfazer) e abrir a edição.</p>
 *
 * <p>Os dados são recarregados em {@link #onResume()}: se o registro tiver sido
 * excluído na tela de edição, esta tela é encerrada automaticamente.</p>
 */
public class DetalheActivity extends AppCompatActivity {

    private static final String TAG = "DetalheActivity";

    public static final String EXTRA_REGISTRO_ID = "com.example.controleproducao.EXTRA_DETALHE_ID";

    private static final int ID_INEXISTENTE = -1;

    private RegistroRepository repositorio;
    private int registroId;

    private TextView tvTitulo;
    private TextView tvPrioridade;
    private TextView tvStatus;
    private TextView tvSetor;
    private TextView tvTipo;
    private TextView tvResponsavel;
    private TextView tvCriadoEm;
    private TextView tvAtualizadoEm;
    private TextView tvDescricao;
    private MaterialButton btnAlternarStatus;

    /** Intent explícita para exibir os detalhes do registro informado. */
    public static Intent criarIntent(Context context, int registroId) {
        Intent intent = new Intent(context, DetalheActivity.class);
        intent.putExtra(EXTRA_REGISTRO_ID, registroId);
        return intent;
    }

    // =====================================================================
    // Ciclo de vida
    // =====================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);
        Log.d(TAG, "onCreate");

        repositorio = RegistroRepository.getInstance();
        registroId = getIntent().getIntExtra(EXTRA_REGISTRO_ID, ID_INEXISTENTE);

        if (registroId == ID_INEXISTENTE) {
            Toast.makeText(this, R.string.msg_registro_nao_encontrado, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        vincularViews();
        configurarAcoes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - recarregando registro " + registroId);

        Registro registro = repositorio.buscarPorId(registroId);
        if (registro == null) {
            // O registro foi excluído na tela de edição: não há mais o que exibir
            finish();
            return;
        }
        exibir(registro);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    // =====================================================================
    // Configuração da tela
    // =====================================================================

    private void vincularViews() {
        tvTitulo = findViewById(R.id.tv_detalhe_titulo);
        tvPrioridade = findViewById(R.id.tv_detalhe_prioridade);
        tvStatus = findViewById(R.id.tv_detalhe_status);
        tvSetor = findViewById(R.id.tv_detalhe_setor);
        tvTipo = findViewById(R.id.tv_detalhe_tipo);
        tvResponsavel = findViewById(R.id.tv_detalhe_responsavel);
        tvCriadoEm = findViewById(R.id.tv_detalhe_criado_em);
        tvAtualizadoEm = findViewById(R.id.tv_detalhe_atualizado_em);
        tvDescricao = findViewById(R.id.tv_detalhe_descricao);
        btnAlternarStatus = findViewById(R.id.btn_alternar_status);
    }

    private void configurarAcoes() {
        MaterialButton btnEditar = findViewById(R.id.btn_editar);
        btnEditar.setOnClickListener(v ->
                startActivity(CadastroActivity.criarIntentEdicao(this, registroId)));

        btnAlternarStatus.setOnClickListener(v -> alternarStatus());
    }

    /** Preenche a tela com os dados do registro. */
    private void exibir(Registro registro) {
        setTitle(registro.getTipo());

        tvTitulo.setText(registro.getTitulo());

        tvPrioridade.setText(getString(R.string.formato_prioridade, registro.getPrioridade()));
        tvPrioridade.setTextColor(RegistroAdapter.obterCorPrioridade(this, registro.getPrioridade()));

        RegistroAdapter.aplicarStatus(tvStatus, registro.isConcluido());

        tvSetor.setText(registro.getSetor());
        tvTipo.setText(registro.getTipo());
        tvResponsavel.setText(registro.getResponsavel());
        tvCriadoEm.setText(registro.getDataCriacao());
        tvAtualizadoEm.setText(registro.getDataAtualizacao() != null
                ? registro.getDataAtualizacao()
                : getString(R.string.valor_nunca));
        tvDescricao.setText(TextUtils.isEmpty(registro.getDescricao())
                ? getString(R.string.valor_sem_descricao)
                : registro.getDescricao());

        btnAlternarStatus.setText(registro.isConcluido()
                ? R.string.acao_marcar_pendente
                : R.string.acao_marcar_concluido);
        btnAlternarStatus.setIconResource(registro.isConcluido()
                ? R.drawable.ic_undo
                : R.drawable.ic_check);
    }

    /** UPDATE rápido: inverte a situação e oferece "Desfazer" via Snackbar. */
    private void alternarStatus() {
        if (!repositorio.alternarConclusao(registroId)) {
            Toast.makeText(this, R.string.msg_registro_nao_encontrado, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Registro registro = repositorio.buscarPorId(registroId);
        exibir(registro);

        int mensagem = registro.isConcluido()
                ? R.string.msg_marcado_concluido
                : R.string.msg_marcado_pendente;

        Snackbar.make(findViewById(android.R.id.content), mensagem, Snackbar.LENGTH_LONG)
                .setAction(R.string.acao_desfazer, v -> {
                    if (repositorio.alternarConclusao(registroId)) {
                        exibir(repositorio.buscarPorId(registroId));
                        Toast.makeText(this, R.string.msg_alteracao_desfeita,
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
