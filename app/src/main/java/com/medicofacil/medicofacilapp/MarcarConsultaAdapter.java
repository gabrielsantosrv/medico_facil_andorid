package com.medicofacil.medicofacilapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medicofacil.medicofacilapp.classesDBO.Consulta;
import com.medicofacil.medicofacilapp.classesDBO.ProntoSocorro;

import java.util.ArrayList;

/**
 * Created by Gabriel Oliveira on 28/05/2016.
 */
public class MarcarConsultaAdapter extends BaseAdapter {

    private Context contexto;
    private ArrayList<Consulta> consultas;

    public MarcarConsultaAdapter(Context contexto, ArrayList<Consulta> consultas)
    {
        this.contexto = contexto;
        this.consultas = (ArrayList<Consulta>) consultas.clone();
    }

    //qtos itens tem na lista
    @Override
    public int getCount() {
        return this.consultas.size();
    }

    //o item da posicao passada como parâmetro
    @Override
    public Object getItem(int position) {
        return this.consultas.get(position);
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
        Consulta consulta = this.consultas.get(position);

        //prepara o inflater para inflar um layout
        LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //infla o layout
        //(layout, parent)
        View layout = inflater.inflate(R.layout.item_consulta, null);

        TextView txtNome = (TextView)layout.findViewById(R.id.txtNome);
        TextView txtDataHora = (TextView)layout.findViewById(R.id.txtDataHora);
        TextView txtEspecialidade = (TextView)layout.findViewById(R.id.txtEspecialidade);
        TextView txtNomeClinica = (TextView)layout.findViewById(R.id.txtNomeClinica);

        txtNome.setText(consulta.getMedico().getNome());
        txtEspecialidade.setText(consulta.getMedico().getEspecialidade());
        txtDataHora.setText(
            consulta.getDataHora().getDay()+"/"+consulta.getDataHora().getMonth()+"/"+consulta.getDataHora().getYear()+" - "+
            consulta.getDataHora().getHours()+":"+consulta.getDataHora().getMinutes());
        txtNomeClinica.setText(consulta.getClinica().getNome());


        return layout;
    }
}

