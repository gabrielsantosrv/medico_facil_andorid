package com.medicofacil.medicofacilapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.medicofacil.medicofacilapp.classesDBO.Convenio;
import com.medicofacil.medicofacilapp.classesDBO.Medico;
import com.medicofacil.medicofacilapp.classesDBO.ProntoSocorro;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


public class BuscarConvenioFragment extends Fragment {

    private static final String INDEX = "http://webservicepaciente.cfapps.io/";
    private ArrayList<Convenio> lista;
    private ListView lstConvenios;
    private SearchView pesquisa;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_medico, container, false);

        lista = new ArrayList<Convenio>();

        pesquisa = (SearchView)view.findViewById(R.id.srcPesquisa);

        //quando se desejar pesquisar algo
        pesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //quando for clicado no botao de pesquisar
            @Override
            public boolean onQueryTextSubmit(String s) {
                //se foi digitado alguma coisa, pesquise
                if(!pesquisa.getQuery().toString().isEmpty())
                    new ConveniosTask().execute(pesquisa.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        lstConvenios = (ListView)view.findViewById(R.id.lstConvenios);

        lstConvenios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //muda o fragment da Tab selecionada(marcação de médico) para marcação de horário
                FragmentTransaction gerente = getActivity().getSupportFragmentManager().beginTransaction();

                ConsultarActivity activity = (ConsultarActivity) getActivity();
                //guarda o convênio clicado
                activity.setConvenio(lista.get(position));

                gerente.replace(R.id.fragmentConteiner, new MarcarClinicaFragment());
                gerente.commit();
            }
        });


        new ConveniosTask().execute();

        return view;
    }

    private class ConveniosTask extends AsyncTask<String, Void, ArrayList<Convenio>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(BuscarConvenioFragment.this.getContext(), "Aguarde",
                    "Aguarde um momento, estamos buscando os convênios disponíveis no nosso sistema");

            //limpa a lista
            lista.clear();
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected ArrayList<Convenio> doInBackground(String... params) {
            try {
                String url = INDEX;

                if(params.length > 0)
                    url += "getConvenio/"+params[0];
                else
                  url += "getConvenios";

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Convenio vet[] = restTemplate.getForObject(url, Convenio[].class);

                for(int i=0; i<vet.length; i++) {
                    lista.add(vet[i]);
                }

                return lista;

            } catch (Exception e) {
                Log.e("Convenio", e.getMessage(), e);
            }

            return null;
        }


        protected void onPostExecute(ArrayList<Convenio> convenios) {
            dialog.dismiss();

            if(convenios != null)
            {
                ConvenioAdapter adaptador;
                adaptador = new ConvenioAdapter(BuscarConvenioFragment.this.getActivity(), convenios);

                lstConvenios.setAdapter(adaptador);
            }

        }

    }

}
