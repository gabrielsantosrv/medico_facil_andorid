package com.medicofacil.medicofacilapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medicofacil.medicofacilapp.classesDBO.Clinica;
import com.medicofacil.medicofacilapp.classesDBO.Convenio;

import java.util.ArrayList;

/**
 * Created by Gabriel Oliveira on 02/07/2016.
 */
public class ConvenioAdapter extends BaseAdapter{

    private Context contexto;
    private ArrayList<Convenio> convenios;

    public ConvenioAdapter(Context contexto, ArrayList<Convenio> convenios)
    {
        this.contexto = contexto;
        this.convenios = (ArrayList<Convenio>) convenios.clone();
    }

    //qtos itens tem na lista
    @Override
    public int getCount() {
        return this.convenios.size();
    }

    //o item da posicao passada como parâmetro
    @Override
    public Object getItem(int position) {
        return this.convenios.get(position);
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
        Convenio convenio = this.convenios.get(position);

        //prepara o inflater para inflar um layout
        LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //infla o layout
        //(layout, parent)
        View layout = inflater.inflate(R.layout.item_consulta, null);

        TextView txtNome = (TextView)layout.findViewById(R.id.txtNome);

        txtNome.setText(convenio.getNome());

        return layout;
    }
}
