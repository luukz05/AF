package com.example.controleproducao;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity de cadastro de registros, onde o CRUD completo é realizado:
 * <ul>
 *     <li><b>Create</b> – sem extra na Intent, grava um novo registro;</li>
 *     <li><b>Read</b>   – com {@link #EXTRA_REGISTRO_ID}, carrega o registro no formulário;</li>
 *     <li><b>Update</b> – no modo edição, salva as alterações do registro carregado;</li>
 *     <li><b>Delete</b> – no modo edição, exclui o registro após confirmação.</li>
 * </ul>
 */
public class CadastroActivity extends AppCompatActivity {

    private static final String TAG = "CadastroActivity";

    /** Chave do extra com o id do registro a ser editado. */
    public static final String EXTRA_REGISTRO_ID = "com.example.controleproducao.EXTRA_REGISTRO_ID";

    private static final int ID_INEXISTENTE = -1;
    private static final int TAMANHO_MINIMO_TITULO = 3;

    private RegistroRepository repositorio;

    /** Registro sendo editado; {@code null} quando a tela está no modo inclusão. */
    private Registro registroEmEdicao;

    // Views do formulário
    private TextInputLayout tilTitulo;
    private TextInputLayout tilSetor;
    private TextInputLayout tilResponsavel;
    private TextInputEditText etTitulo;
    private MaterialAutoCompleteTextView actvSetor;
    private TextInputEditText etResponsavel;
    private TextInputEditText etDescricao;
    private RadioGroup rgTipo;
    private ChipGroup chipGroupPrioridade;
    private MaterialSwitch switchConcluido;
    private TextView tvInfoDatas;
    private MaterialButton btnSalvar;
    private MaterialButton btnExcluir;
    private MaterialButton btnCancelar;

    // =====================================================================
    // Fábricas de Intent (centralizam os parâmetros aceitos por esta tela)
    // =====================================================================

    /** Intent para abrir a tela em modo inclusão (novo registro). */
    public static Intent criarIntentNovo(Context context) {
        return new Intent(context, CadastroActivity.class);
    }

    /** Intent para abrir a tela em modo edição do registro informado. */
    public static Intent criarIntentEdicao(Context context, int registroId) {
        Intent intent = new Intent(context, CadastroActivity.class);
        intent.putExtra(EXTRA_REGISTRO_ID, registroId);
        return intent;
    }

    // =====================================================================
    // Ciclo de vida
    // =====================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Log.d(TAG, "onCreate");

        repositorio = RegistroRepository.getInstance();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        vincularViews();
        configurarListaSetores();

        // READ: verifica se a tela foi aberta para edição de um registro existente
        int registroId = getIntent().getIntExtra(EXTRA_REGISTRO_ID, ID_INEXISTENTE);
        if (registroId != ID_INEXISTENTE) {
            registroEmEdicao = repositorio.buscarPorId(registroId);
            if (registroEmEdicao == null) {
                Toast.makeText(this, R.string.msg_registro_nao_encontrado, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        configurarModoDaTela(savedInstanceState == null);
        configurarAcoes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /** Seta "voltar" da ActionBar se comporta como o botão voltar do sistema. */
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    // =====================================================================
    // Configuração da tela
    // =====================================================================

    private void vincularViews() {
        tilTitulo = findViewById(R.id.til_titulo);
        tilSetor = findViewById(R.id.til_setor);
        tilResponsavel = findViewById(R.id.til_responsavel);
        etTitulo = findViewById(R.id.et_titulo);
        actvSetor = findViewById(R.id.actv_setor);
        etResponsavel = findViewById(R.id.et_responsavel);
        etDescricao = findViewById(R.id.et_descricao);
        rgTipo = findViewById(R.id.rg_tipo);
        chipGroupPrioridade = findViewById(R.id.chip_group_prioridade);
        switchConcluido = findViewById(R.id.switch_concluido);
        tvInfoDatas = findViewById(R.id.tv_info_datas);
        btnSalvar = findViewById(R.id.btn_salvar);
        btnExcluir = findViewById(R.id.btn_excluir);
        btnCancelar = findViewById(R.id.btn_cancelar);
    }

    /** Popula o menu suspenso de setores com o array definido em strings.xml. */
    private void configurarListaSetores() {
        String[] setores = getResources().getStringArray(R.array.setores);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, setores);
        actvSetor.setAdapter(adapter);
    }

    /**
     * Ajusta título, botões e dados conforme o modo (inclusão ou edição).
     *
     * @param preencherCampos {@code false} quando a Activity é recriada (ex.: rotação),
     *                        pois o Android já restaura o conteúdo digitado.
     */
    private void configurarModoDaTela(boolean preencherCampos) {
        if (registroEmEdicao == null) {
            setTitle(R.string.titulo_novo_registro);
            btnSalvar.setText(R.string.acao_cadastrar);
            btnExcluir.setVisibility(View.GONE);
            tvInfoDatas.setVisibility(View.GONE);
        } else {
            setTitle(R.string.titulo_editar_registro);
            btnSalvar.setText(R.string.acao_salvar_alteracoes);
            btnExcluir.setVisibility(View.VISIBLE);
            tvInfoDatas.setVisibility(View.VISIBLE);
            tvInfoDatas.setText(montarTextoDatas(registroEmEdicao));
            if (preencherCampos) {
                preencherFormulario(registroEmEdicao);
            }
        }
    }

    private void configurarAcoes() {
        btnSalvar.setOnClickListener(v -> salvar());
        btnExcluir.setOnClickListener(v -> confirmarExclusao());
        btnCancelar.setOnClickListener(v -> {
            Toast.makeText(this, R.string.msg_operacao_cancelada, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // =====================================================================
    // READ - carrega o registro nos campos
    // =====================================================================

    private void preencherFormulario(Registro registro) {
        etTitulo.setText(registro.getTitulo());
        actvSetor.setText(registro.getSetor(), false); // false = não abrir/filtrar o menu
        etResponsavel.setText(registro.getResponsavel());
        etDescricao.setText(registro.getDescricao());
        rgTipo.check(obterIdRadioDoTipo(registro.getTipo()));
        chipGroupPrioridade.check(obterIdChipDaPrioridade(registro.getPrioridade()));
        switchConcluido.setChecked(registro.isConcluido());
    }

    private String montarTextoDatas(Registro registro) {
        String texto = getString(R.string.formato_criado_em, registro.getDataCriacao());
        if (registro.getDataAtualizacao() != null) {
            texto += "\n" + getString(R.string.formato_atualizado_em, registro.getDataAtualizacao());
        }
        return texto;
    }

    // =====================================================================
    // CREATE / UPDATE
    // =====================================================================

    private void salvar() {
        if (!validarFormulario()) {
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.msg_erro_validacao, Snackbar.LENGTH_LONG).show();
            return;
        }

        Registro dados = new Registro(
                textoDe(etTitulo.getText()),
                textoDe(actvSetor.getText()),
                obterTipoSelecionado(),
                obterPrioridadeSelecionada(),
                textoDe(etResponsavel.getText()),
                textoDe(etDescricao.getText()),
                switchConcluido.isChecked());

        if (registroEmEdicao == null) {
            // CREATE
            Registro novo = repositorio.adicionar(dados);
            Log.d(TAG, "Registro criado: " + novo);
            Toast.makeText(this, R.string.msg_registro_criado, Toast.LENGTH_SHORT).show();
        } else {
            // UPDATE
            boolean atualizado = repositorio.atualizar(registroEmEdicao.getId(), dados);
            if (!atualizado) {
                Snackbar.make(findViewById(android.R.id.content),
                        R.string.msg_erro_atualizar, Snackbar.LENGTH_LONG).show();
                return;
            }
            Log.d(TAG, "Registro atualizado: " + registroEmEdicao);
            Toast.makeText(this, R.string.msg_registro_atualizado, Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish(); // retorna à tela anterior, que se atualiza em onResume()
    }

    /** Valida os campos obrigatórios, exibindo a mensagem de erro em cada campo inválido. */
    private boolean validarFormulario() {
        tilTitulo.setError(null);
        tilSetor.setError(null);
        tilResponsavel.setError(null);

        boolean valido = true;

        String titulo = textoDe(etTitulo.getText());
        if (titulo.isEmpty()) {
            tilTitulo.setError(getString(R.string.erro_campo_obrigatorio));
            valido = false;
        } else if (titulo.length() < TAMANHO_MINIMO_TITULO) {
            tilTitulo.setError(getString(R.string.erro_titulo_curto, TAMANHO_MINIMO_TITULO));
            valido = false;
        }

        if (textoDe(actvSetor.getText()).isEmpty()) {
            tilSetor.setError(getString(R.string.erro_selecione_setor));
            valido = false;
        }

        if (textoDe(etResponsavel.getText()).isEmpty()) {
            tilResponsavel.setError(getString(R.string.erro_campo_obrigatorio));
            valido = false;
        }

        // Coloca o foco no primeiro campo com erro
        if (tilTitulo.getError() != null) {
            etTitulo.requestFocus();
        } else if (tilResponsavel.getError() != null) {
            etResponsavel.requestFocus();
        }
        return valido;
    }

    // =====================================================================
    // DELETE
    // =====================================================================

    private void confirmarExclusao() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialogo_excluir_titulo)
                .setMessage(getString(R.string.dialogo_excluir_mensagem, registroEmEdicao.getTitulo()))
                .setIcon(R.drawable.ic_delete)
                .setNegativeButton(R.string.acao_cancelar, null)
                .setPositiveButton(R.string.acao_excluir, (dialog, which) -> excluir())
                .show();
    }

    private void excluir() {
        if (repositorio.remover(registroEmEdicao.getId())) {
            Log.d(TAG, "Registro excluído: " + registroEmEdicao);
            Toast.makeText(this, R.string.msg_registro_excluido, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.msg_erro_excluir, Snackbar.LENGTH_LONG).show();
        }
    }

    // =====================================================================
    // Conversões entre componentes visuais e valores do modelo
    // =====================================================================

    private String obterTipoSelecionado() {
        int id = rgTipo.getCheckedRadioButtonId();
        if (id == R.id.rb_tipo_checklist) {
            return Registro.TIPO_CHECKLIST;
        } else if (id == R.id.rb_tipo_anotacao) {
            return Registro.TIPO_ANOTACAO;
        }
        return Registro.TIPO_REGISTRO;
    }

    private int obterIdRadioDoTipo(String tipo) {
        if (Registro.TIPO_CHECKLIST.equals(tipo)) {
            return R.id.rb_tipo_checklist;
        } else if (Registro.TIPO_ANOTACAO.equals(tipo)) {
            return R.id.rb_tipo_anotacao;
        }
        return R.id.rb_tipo_registro;
    }

    private String obterPrioridadeSelecionada() {
        int id = chipGroupPrioridade.getCheckedChipId();
        if (id == R.id.chip_prioridade_alta) {
            return Registro.PRIORIDADE_ALTA;
        } else if (id == R.id.chip_prioridade_baixa) {
            return Registro.PRIORIDADE_BAIXA;
        }
        return Registro.PRIORIDADE_MEDIA;
    }

    private int obterIdChipDaPrioridade(String prioridade) {
        if (Registro.PRIORIDADE_ALTA.equals(prioridade)) {
            return R.id.chip_prioridade_alta;
        } else if (Registro.PRIORIDADE_BAIXA.equals(prioridade)) {
            return R.id.chip_prioridade_baixa;
        }
        return R.id.chip_prioridade_media;
    }

    /** Converte o conteúdo de um campo em String sem espaços nas extremidades. */
    private static String textoDe(Editable editable) {
        return editable == null ? "" : editable.toString().trim();
    }
}
