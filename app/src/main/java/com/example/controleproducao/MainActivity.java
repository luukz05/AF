package com.example.controleproducao;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Tela inicial: dá acesso ao cadastro e à listagem de registros.
 */
public class MainActivity extends AppCompatActivity {

    private TextView textTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textTotal = findViewById(R.id.textTotal);
        Button buttonNovo = findViewById(R.id.buttonNovo);
        Button buttonListar = findViewById(R.id.buttonListar);

        // Intents explícitas para as demais telas
        buttonNovo.setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class)));
        buttonListar.setOnClickListener(v ->
                startActivity(new Intent(this, ListagemActivity.class)));
    }

    /** Chamado sempre que a tela volta ao primeiro plano: atualiza o total de registros. */
    @Override
    protected void onResume() {
        super.onResume();
        int total = RegistroRepository.getInstance().listar().size();
        textTotal.setText(getString(R.string.total_registros, total));
    }
}
