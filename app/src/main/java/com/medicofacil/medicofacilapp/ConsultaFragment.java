package com.medicofacil.medicofacilapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.medicofacil.medicofacilapp.classesDBO.Clinica;
import com.medicofacil.medicofacilapp.classesDBO.Consulta;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


public class ConsultaFragment extends Fragment {

    private ListView lstConsultas;
    private SearchView pesquisa;
    private static final String INDEX = "http://webservicepaciente.cfapps.io/";
    private ArrayList<Consulta> lista;

    public ConsultaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_consulta, container, false);
        pesquisa = (SearchView)view.findViewById(R.id.srcPesquisa);
        lista = new ArrayList<Consulta>();

        //quando se desejar pesquisar algo
        pesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //quando for clicado no botao de pesquisar
            @Override
            public boolean onQueryTextSubmit(String s) {
                //se foi digitado alguma coisa, pesquise
                if(!pesquisa.getQuery().toString().isEmpty())
                    new ConsultasTask().execute(pesquisa.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        lstConsultas = (ListView) view.findViewById(R.id.lstConsultas);
        new ConsultasTask().execute();

        return view;
    }

    private class ConsultasTask extends AsyncTask<String, Void, ArrayList<Consulta>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ConsultaFragment.this.getContext(), "Aguarde",
                    "Aguarde um momento, estamos buscando suas consultas no nosso sistema");

            int tamanho = lista.size();

            //limpa a lista
            for(int i=0; i<tamanho; i++)
                lista.remove(0);
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected ArrayList<Consulta> doInBackground(String... params) {
            try {
                String nome = "";

                if(params.length > 0)
                    nome = params[0];


                String url = INDEX+"/getConsultas";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                //o web service retorna uma lista de prontos socorros ordenada
                //da menor para a maior distância entre o usuário e o PS
                Consulta vet[] = restTemplate.getForObject(url, Consulta[].class);

                ArrayList<Consulta> listaConsulta = new ArrayList<Consulta>();

                for(int i=0; i<vet.length; i++) {
                    lista.add(vet[i]);
                    listaConsulta.add(vet[i]);
                }

                return listaConsulta;

            } catch (Exception e) {
                Log.e("Consulta", e.getMessage(), e);
            }

            return null;
        }


        protected void onPostExecute(ArrayList<Consulta> consultas) {
            dialog.dismiss();

            if(consultas != null)
            {
                MarcarConsultaAdapter adaptador;
                adaptador = new MarcarConsultaAdapter(ConsultaFragment.this.getActivity(), consultas);

                lstConsultas.setAdapter(adaptador);
            }

         /*   lstClinicas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //muda o fragment da Tab selecionada(marcação de médico) para marcação de horário
                    FragmentTransaction gerente = getActivity().getSupportFragmentManager().beginTransaction();
                    gerente.replace(R.id.fragmentConteiner, new MarcarHorarioFragment());
                    gerente.commit();
                }
            });*/
        }

    }

}
