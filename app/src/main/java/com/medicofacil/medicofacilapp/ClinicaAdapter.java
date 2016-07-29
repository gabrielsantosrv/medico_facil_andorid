package com.medicofacil.medicofacilapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medicofacil.medicofacilapp.classesDBO.Clinica;

import java.util.ArrayList;

/**
 * Created by Gabriel Oliveira on 02/07/2016.
 */
public class ClinicaAdapter extends BaseAdapter{

    private Context contexto;
    private ArrayList<Clinica> clinicas;

    public ClinicaAdapter(Context contexto, ArrayList<Clinica> prontosSocorros)
    {
        this.contexto = contexto;
        this.clinicas = (ArrayList<Clinica>) prontosSocorros.clone();
    }

    //qtos itens tem na lista
    @Override
    public int getCount() {
        return this.clinicas.size();
    }

    //o item da posicao passada como parâmetro
    @Override
    public Object getItem(int position) {
        return this.clinicas.get(position);
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
        Clinica clinica = this.clinicas.get(position);

        //prepara o inflater para inflar um layout
        LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //infla o layout
        //(layout, parent)
        View layout = inflater.inflate(R.layout.item_buscar_ps_clinica, null);

        TextView txtNome = (TextView)layout.findViewById(R.id.txtNomeMedico);
        TextView txtEndereco = (TextView)layout.findViewById(R.id.txtEndereco);

        txtNome.setText(clinica.getNome());

        //formato original telefone: 0000000000
        //formato convertido: (00) 0000-0000
        String tel = clinica.getTelefone();
        String ddd, numIni, numFim;

        ddd = tel.substring(0,2);
        numIni = tel.substring(2,6);
        numFim = tel.substring(6);

        txtEndereco.setText("Endereço: "+clinica.getEndereco()+"\n"+
                            "Bairro: "+clinica.getBairro()+"\n"+
                            clinica.getCidade()+" - "+clinica.getUf()+"\n"+
                            "Tel: ("+ddd+") "+numIni+"-"+numFim);

        return layout;
    }
}
