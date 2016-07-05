package com.medicofacil.medicofacilapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.medicofacil.medicofacilapp.classesDBO.Clinica;
import com.medicofacil.medicofacilapp.classesDBO.ProntoSocorro;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


public class BuscarProntoSocorroFragment extends Fragment {
    private static final String INDEX = "http://webservicepaciente.cfapps.io/";
    private static final int ORDEM_DISTANCIA = 0;

    private Spinner spinner;
    private SearchView pesquisa;
    private ListView lstProntosSocorros;
    private ArrayList<ProntoSocorro> lista;
    private String lat, lon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_buscar_pronto_socorro, container, false);

        lstProntosSocorros = (ListView) view.findViewById(R.id.lstProntosSocorros);
        lista = new ArrayList<ProntoSocorro>();

        spinner = (Spinner) view.findViewById(R.id.spnOrdem);
        String [] itens = new String[]{"Mais próximo", "Ordem alfabética"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, itens);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                if(lista.size() > 0)
                {
                    ProntoSocorroAdapter adaptador;

                    if (position == ORDEM_DISTANCIA)
                        adaptador = new ProntoSocorroAdapter(BuscarProntoSocorroFragment.this.getActivity(), lista);
                    else
                        adaptador = new ProntoSocorroAdapter(BuscarProntoSocorroFragment.this.getActivity(), ordenaAlfabetica());

                    lstProntosSocorros.setAdapter(adaptador);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        pesquisa = (SearchView)view.findViewById(R.id.srcPesquisa);

        //quando abrir o campo de pesquisa
        pesquisa.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.INVISIBLE);
            }
        });

        //quando fechar o campo de pesquisa
        pesquisa.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                spinner.setVisibility(View.VISIBLE);
                return false;
            }
        });

        SharedPreferences geo = this.getContext().getSharedPreferences("geolocalização", Context.MODE_PRIVATE);
        lat = geo.getString("latitude", "0");
        lon = geo.getString("longitude", "0");

        //quando se desejar pesquisar algo
        pesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //quando for clicado no botao de pesquisar
            @Override
            public boolean onQueryTextSubmit(String s) {
                //se foi digitado alguma coisa, pesquise
                if(!pesquisa.getQuery().toString().isEmpty())
                    new ProntoSocorrosTask().execute(lat, lon, pesquisa.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        new ProntoSocorrosTask().execute(lat, lon);

        // Inflate the layout for this fragment
        return view;
    }

    //retorna uma lista com os nomes ordenados por ordem alfabética
    private ArrayList<ProntoSocorro> ordenaAlfabetica()
    {
       ArrayList<ProntoSocorro> vetAux = new ArrayList<ProntoSocorro>();

       int tamanho = lista.size();

       //copia a lista ordenada por distância
       for(int a=0; a<tamanho; a++)
        vetAux.add(lista.get(a));

       //faz uma ordenação do tipo Bubble Sort
       //e a lista termina ordenada por nome
       for (int i = 0; i < tamanho - 1; ++i)
         for (int j = i + 1; j < tamanho; ++j)
           if (vetAux.get(i).getNome().compareTo(vetAux.get(j).getNome()) > 0)
           {
               ProntoSocorro aux = vetAux.get(i);

               //ao adicionar um registro na posição i,
               //são deslocados todos os dados, a partir da posição i,
               //uma posição
               vetAux.add(i, vetAux.get(j));

               //desta forma se um desejava excluir um item que
               //se encontrava na posição i, ao adicionar um
               //registro, eu devo excluir o elemento da posição i+1,
               //uma vez que ele foi deslocada uma posição para frente
               vetAux.remove(i+1);

               vetAux.add(j, aux);
               vetAux.remove(j+1);
           }

        return vetAux;
    }


    private class ProntoSocorrosTask extends AsyncTask<String, Void, ArrayList<ProntoSocorro>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(BuscarProntoSocorroFragment.this.getContext(), "Aguarde",
                    "Aguarde um momento, estamos buscando os Prontos Socorros no nosso sistema");

            int tamanho = lista.size();

            //limpa a lista
            for(int i=0; i<tamanho; i++)
              lista.remove(0);
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected ArrayList<ProntoSocorro> doInBackground(String... params) {
            try {
                double lat =0, lon=0;
                String nome = "";

                if(params.length > 0)
                {
                    lat = Double.parseDouble(params[0]);
                    lon = Double.parseDouble(params[1]);

                    if(params.length == 3)
                        nome = params[2];
                }

                String url = INDEX+"getProntoSocorros";

                //foi digitado alguma coisa em pesquisa
                //pesquise por convênio
                if(nome == null || !nome.isEmpty())
                    url += "PorConvenio/"+nome;

                url += "/"+lat+"/"+lon;


                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                //o web service retorna uma lista de prontos socorros ordenada
                //da menor para a maior distância entre o usuário e o PS
                ProntoSocorro vet1[] = restTemplate.getForObject(url, ProntoSocorro[].class);
                ArrayList<ProntoSocorro> listaPs = new ArrayList<ProntoSocorro>();

                for(int i=0; i<vet1.length; i++) {
                    lista.add(vet1[i]);
                    listaPs.add(vet1[i]);
                }

                //e pesquise por parte do nome
                if(!nome.isEmpty()) {
                    url = INDEX+"getProntoSocorrosPorNome/" + nome + "/" + lat + "/" + lon;

                    ProntoSocorro vet2[] = restTemplate.getForObject(url, ProntoSocorro[].class);

                    for(int i=0; i<vet2.length; i++) {
                        lista.add(vet2[i]);
                        listaPs.add(vet2[i]);
                    }
                }

                return listaPs;

            } catch (Exception e) {
                Log.e("BuscarProntoSocorro", e.getMessage(), e);
            }

            return null;
        }


        protected void onPostExecute(ArrayList<ProntoSocorro> prontoSocorros) {
            dialog.dismiss();

            if(prontoSocorros != null)
            {
                ProntoSocorroAdapter adaptador;

                if(spinner.getSelectedItemPosition() == ORDEM_DISTANCIA)
                  adaptador = new ProntoSocorroAdapter(BuscarProntoSocorroFragment.this.getActivity(), prontoSocorros);
                else
                    adaptador = new ProntoSocorroAdapter(BuscarProntoSocorroFragment.this.getActivity(), ordenaAlfabetica());

                lstProntosSocorros.setAdapter(adaptador);
            }

         /*   lstProntosSocorros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
