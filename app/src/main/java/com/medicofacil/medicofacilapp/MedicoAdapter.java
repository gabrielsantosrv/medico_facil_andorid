package com.medicofacil.medicofacilapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medicofacil.medicofacilapp.classesDBO.Medico;

import java.util.ArrayList;

/**
 * Created by Gabriel Oliveira on 28/05/2016.
 */
public class MedicoAdapter extends BaseAdapter {

    private Context contexto;
    private ArrayList<Medico> medicos;

    public MedicoAdapter(Context contexto, ArrayList<Medico> medicos)
    {
        this.contexto = contexto;
        this.medicos = (ArrayList<Medico>) medicos.clone();
    }

    //qtos itens tem na lista
    @Override
    public int getCount() {
        return this.medicos.size();
    }

    //o item da posicao passada como parâmetro
    @Override
    public Object getItem(int position) {
        return this.medicos.get(position);
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
        Medico medico = this.medicos.get(position);

        //prepara o inflater para inflar um layout
        LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //infla o layout
        //(layout, parent)
        View layout = inflater.inflate(R.layout.item_marcar_medico, null);

        TextView txtNome = (TextView)layout.findViewById(R.id.txtNomeMedico);
        TextView txtEspecialidade = (TextView)layout.findViewById(R.id.txtEspecialidade);

        txtNome.setText(medico.getNome());
//      txtEspecialidade.setText(medico.getEspecialidade());


        return layout;
    }
}

