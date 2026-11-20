package com.example.controleproducao;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Tela de cadastro com o CRUD completo:
 * - sem extra: cria um novo registro (Create);
 * - com EXTRA_ID: carrega o registro (Read), permite salvar alterações (Update) e excluir (Delete).
 */
public class CadastroActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "registro_id";

    private final RegistroRepository repositorio = RegistroRepository.getInstance();

    private EditText editTitulo;
    private EditText editSetor;
    private EditText editDescricao;
    private CheckBox checkConcluido;

    /** Id do registro em edição; 0 indica um novo registro. */
    private int registroId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTitulo = findViewById(R.id.editTitulo);
        editSetor = findViewById(R.id.editSetor);
        editDescricao = findViewById(R.id.editDescricao);
        checkConcluido = findViewById(R.id.checkConcluido);
        Button buttonSalvar = findViewById(R.id.buttonSalvar);
        Button buttonExcluir = findViewById(R.id.buttonExcluir);

        registroId = getIntent().getIntExtra(EXTRA_ID, 0);
        if (registroId != 0) {
            carregarRegistro();
            setTitle(R.string.titulo_editar);
            buttonExcluir.setVisibility(View.VISIBLE);
        }

        buttonSalvar.setOnClickListener(v -> salvar());
        buttonExcluir.setOnClickListener(v -> excluir());
    }

    /** READ: preenche os campos com os dados do registro selecionado. */
    private void carregarRegistro() {
        Registro registro = repositorio.buscarPorId(registroId);
        if (registro == null) {
            Toast.makeText(this, R.string.msg_nao_encontrado, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        editTitulo.setText(registro.getTitulo());
        editSetor.setText(registro.getSetor());
        editDescricao.setText(registro.getDescricao());
        checkConcluido.setChecked(registro.isConcluido());
    }

    /** CREATE ou UPDATE, conforme o modo da tela. */
    private void salvar() {
        String titulo = editTitulo.getText().toString().trim();
        String setor = editSetor.getText().toString().trim();
        String descricao = editDescricao.getText().toString().trim();
        boolean concluido = checkConcluido.isChecked();

        if (titulo.isEmpty() || setor.isEmpty()) {
            Toast.makeText(this, R.string.msg_campos_obrigatorios, Toast.LENGTH_SHORT).show();
            return;
        }

        if (registroId == 0) {
            repositorio.adicionar(new Registro(titulo, setor, descricao, concluido));
            Toast.makeText(this, R.string.msg_registro_criado, Toast.LENGTH_SHORT).show();
        } else {
            repositorio.atualizar(registroId, titulo, setor, descricao, concluido);
            Toast.makeText(this, R.string.msg_registro_atualizado, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    /** DELETE: remove o registro em edição. */
    private void excluir() {
        repositorio.remover(registroId);
        Toast.makeText(this, R.string.msg_registro_excluido, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
