package com.medicofacil.medicofacilapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.medicofacil.medicofacilapp.classesDBO.Medico;

import java.util.ArrayList;


public class MarcarMedicoFragment extends Fragment {

    private SearchView pesquisa;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_marcar_medico, container, false);

        pesquisa = (SearchView)view.findViewById(R.id.srcPesquisa);

        ArrayList<Medico> lista = new ArrayList<Medico>();

        try
        {
            lista.add(new Medico(1, "Reumatologista", "SP", "123456", "Dr. João Alves dos Santos"));
            lista.add(new Medico(1, "Cardiologista", "SP", "123456", "Dr. João Urbano"));
            lista.add(new Medico(1, "Endocrinologista", "SP", "123456", "Dr. Carlos da Silva"));
            lista.add(new Medico(1, "Pediatra", "SP", "123456", "Dra. Fátima Wistaker"));
            lista.add(new Medico(1, "Reumatologista", "SP", "123456", "Dra. Rozangela de Oliveira"));
        }
        catch (Exception ex){}

        MedicoAdapter adaptador = new MedicoAdapter(this.getActivity(), lista);
        ListView lstMedico = (ListView)view.findViewById(R.id.lstMedicos);
        lstMedico.setAdapter(adaptador);

        lstMedico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //muda o fragment da Tab selecionada(marcação de médico) para marcação de horário
                FragmentTransaction gerente = getActivity().getSupportFragmentManager().beginTransaction();
                gerente.replace(R.id.fragmentConteiner, new MarcarHorarioFragment());
                gerente.commit();
            }
        });

        return view;
    }

}
