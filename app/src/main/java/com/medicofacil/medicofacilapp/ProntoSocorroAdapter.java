package com.medicofacil.medicofacilapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.medicofacil.medicofacilapp.classesDBO.ProntoSocorro;
import java.util.ArrayList;

/**
 * Created by Gabriel Oliveira on 28/05/2016.
 */
public class ProntoSocorroAdapter extends BaseAdapter {

    private Context contexto;
    private ArrayList<ProntoSocorro> prontosSocorros;

    public ProntoSocorroAdapter(Context contexto, ArrayList<ProntoSocorro> prontosSocorros)
    {
        this.contexto = contexto;
        this.prontosSocorros = (ArrayList<ProntoSocorro>) prontosSocorros.clone();
    }

    //qtos itens tem na lista
    @Override
    public int getCount() {
        return this.prontosSocorros.size();
    }

    //o item da posicao passada como parâmetro
    @Override
    public Object getItem(int position) {
        return this.prontosSocorros.get(position);
    }

    //o id do item da posicao passada como parâmetro
    @Override
    public long getItemId(int position) {
        return position;
    }

    //esse método é chamado para construir cada item
    //da sua lista
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProntoSocorro prontoSocorro = this.prontosSocorros.get(position);

        //prepara o inflater para inflar um layout
        LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //infla o layout
        //(layout, parent)
        View layout = inflater.inflate(R.layout.item_buscar_ps_clinica, null);

        TextView txtNome = (TextView)layout.findViewById(R.id.txtNome);
        TextView txtEndereco = (TextView)layout.findViewById(R.id.txtEndereco);

        txtNome.setText(prontoSocorro.getNome());
        txtEndereco.setText(prontoSocorro.getEndereco()+"\n"+prontoSocorro.getBairro()+"\n"+
                                     prontoSocorro.getIdCidade()+" - SP");

        return layout;
    }
}

