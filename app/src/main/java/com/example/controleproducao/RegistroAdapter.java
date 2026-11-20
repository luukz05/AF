package com.example.controleproducao;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

/**
 * Adapter responsável por "desenhar" cada {@link Registro} na ListView da
 * {@link ListagemActivity}, utilizando o layout {@code item_registro.xml}.
 *
 * <p>Aplica o padrão ViewHolder para reaproveitar as views e evitar chamadas
 * repetidas a {@code findViewById} durante a rolagem da lista.</p>
 */
public class RegistroAdapter extends ArrayAdapter<Registro> {

    private final LayoutInflater inflater;

    public RegistroAdapter(@NonNull Context context, @NonNull List<Registro> registros) {
        super(context, 0, registros);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_registro, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Registro registro = getItem(position);
        if (registro != null) {
            preencher(holder, registro);
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        Registro registro = getItem(position);
        return registro != null ? registro.getId() : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /** Transfere os dados do registro para as views da linha. */
    private void preencher(ViewHolder holder, Registro registro) {
        Context context = getContext();
        int corPrioridade = obterCorPrioridade(context, registro.getPrioridade());

        holder.tvTitulo.setText(registro.getTitulo());
        holder.tvMeta.setText(context.getString(R.string.formato_setor_tipo,
                registro.getSetor(), registro.getTipo()));

        if (TextUtils.isEmpty(registro.getDescricao())) {
            holder.tvDescricao.setVisibility(View.GONE);
        } else {
            holder.tvDescricao.setVisibility(View.VISIBLE);
            holder.tvDescricao.setText(registro.getDescricao());
        }

        holder.tvPrioridade.setText(registro.getPrioridade());
        holder.tvPrioridade.setTextColor(corPrioridade);
        holder.faixaPrioridade.setBackgroundColor(corPrioridade);

        holder.tvData.setText(registro.getDataCriacao());
        aplicarStatus(holder.tvStatus, registro.isConcluido());
    }

    // ---------------------------------------------------------------------
    // Utilitários de apresentação reutilizados também pela DetalheActivity
    // ---------------------------------------------------------------------

    /** @return a cor associada à prioridade (alta = vermelho, média = laranja, baixa = verde). */
    public static int obterCorPrioridade(Context context, String prioridade) {
        int corRes;
        if (Registro.PRIORIDADE_ALTA.equals(prioridade)) {
            corRes = R.color.prioridade_alta;
        } else if (Registro.PRIORIDADE_MEDIA.equals(prioridade)) {
            corRes = R.color.prioridade_media;
        } else {
            corRes = R.color.prioridade_baixa;
        }
        return ContextCompat.getColor(context, corRes);
    }

    /** Configura texto e cor do indicador de situação (Pendente / Concluído). */
    public static void aplicarStatus(TextView tvStatus, boolean concluido) {
        Context context = tvStatus.getContext();
        tvStatus.setText(concluido ? R.string.status_concluido : R.string.status_pendente);
        tvStatus.setTextColor(ContextCompat.getColor(context,
                concluido ? R.color.status_concluido : R.color.status_pendente));
    }

    /** Guarda as referências das views de uma linha da lista. */
    private static class ViewHolder {
        final View faixaPrioridade;
        final TextView tvTitulo;
        final TextView tvPrioridade;
        final TextView tvMeta;
        final TextView tvDescricao;
        final TextView tvData;
        final TextView tvStatus;

        ViewHolder(View itemView) {
            faixaPrioridade = itemView.findViewById(R.id.view_faixa_prioridade);
            tvTitulo = itemView.findViewById(R.id.tv_item_titulo);
            tvPrioridade = itemView.findViewById(R.id.tv_item_prioridade);
            tvMeta = itemView.findViewById(R.id.tv_item_meta);
            tvDescricao = itemView.findViewById(R.id.tv_item_descricao);
            tvData = itemView.findViewById(R.id.tv_item_data);
            tvStatus = itemView.findViewById(R.id.tv_item_status);
        }
    }
}
