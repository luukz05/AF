package com.example.controleproducao;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Tela de listagem: exibe os registros em uma ListView.
 * Toque em um item abre o cadastro em modo de edição.
 */
public class ListagemActivity extends AppCompatActivity {

    private ArrayAdapter<Registro> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ListView listView = findViewById(R.id.listRegistros);
        listView.setEmptyView(findViewById(R.id.textVazio));

        // ArrayAdapter usa o toString() de Registro para montar cada linha
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                RegistroRepository.getInstance().listar());
        listView.setAdapter(adapter);

        // Abre o cadastro passando o id do registro selecionado
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Registro registro = adapter.getItem(position);
            Intent intent = new Intent(this, CadastroActivity.class);
            intent.putExtra(CadastroActivity.EXTRA_ID, registro.getId());
            startActivity(intent);
        });

        FloatingActionButton fab = findViewById(R.id.fabNovo);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class)));
    }

    /** Recarrega a lista ao voltar do cadastro (após incluir, editar ou excluir). */
    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
