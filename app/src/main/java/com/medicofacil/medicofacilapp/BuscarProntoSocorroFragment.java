package com.medicofacil.medicofacilapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
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
    private AlertDialog msg;
    private int posicao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_buscar_pronto_socorro, container, false);

        lstProntosSocorros = (ListView) view.findViewById(R.id.lstProntosSocorros);
        lista = new ArrayList<ProntoSocorro>();

        posicao = -1;
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

        BuscarActivity activity = (BuscarActivity)getActivity();
        Localizacao localizacao = activity.getLocalizacao();
        lat = localizacao.getLatitude()+"";
        lon = localizacao.getLongitude()+"";

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

        lstProntosSocorros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

                posicao =  pos;
                ArrayList<String> itens = new ArrayList<String>();
                itens.add("Visualizar no mapa");
                itens.add("Pesquisar médicos em plantão");

                //adapter utilizando um layout customizado (TextView)
                ArrayAdapter adapter = new ArrayAdapter(BuscarProntoSocorroFragment.this.getActivity(),
                        R.layout.item_dialog, itens);

                AlertDialog.Builder builder = new AlertDialog.Builder(BuscarProntoSocorroFragment.this.getActivity());
                builder.setTitle("O que deseja?");

                //define o diálogo como uma lista, passa o adapter.
                builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog , int position) {

                        ProntoSocorro ps  = lista.get(BuscarProntoSocorroFragment.this.posicao);

                        //visualizar no mapa
                        if(position == 0)
                        {
                            Intent intent = new Intent(getActivity(), MapaActivity.class);

                            //passa a geolocalização para o mapa
                            Bundle bundle = new Bundle();
                            bundle.putString("ps", ps.getLatitude()+"/"+ps.getLongitude());
                            intent.putExtras(bundle);

                            getActivity().startActivity(intent);
                        }
                        else//visualizar médicos de plantão
                        {
                            FragmentTransaction gerente = getActivity().getSupportFragmentManager().beginTransaction();
                            BuscarActivity activity = (BuscarActivity)getActivity();
                            //guarda o pronto socorro clicado
                            activity.setProntoSocorro(ps);
                            gerente.replace(R.id.fragmentConteiner, new BuscarMedicoPlantaoFragment());
                            gerente.commit();
                        }

                        msg.dismiss();
                    }
                });

                msg = builder.create();
                msg.show();
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

            //limpa a lista
            lista.clear();
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected ArrayList<ProntoSocorro> doInBackground(String... params) {
            try {
                float lat =0, lon=0;
                String nome = "";

                if(params.length > 0)
                {
                    lat = Float.parseFloat(params[0]);
                    lon = Float.parseFloat(params[1]);

                    if(params.length == 3)
                        nome = params[2];
                }

                String url = INDEX;

                //foi digitado alguma coisa em pesquisa
                //pesquise por convênio
                if(!nome.isEmpty())
                    url += "getProntoSocorrosPorNome/" + nome + "/" + lat + "/" + lon; //"getProntoSocorrosPorNomeConvenio/"+convenio.getId()+"/"+lat+"/"+lon;
                else
                  url += "getProntoSocorros/"+lat+"/"+lon;  //"getProntoSocorrosPorConvenio/"+convenio.getId()+"/"+lat+"/"+lon;

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                //o web service retorna uma lista de prontos socorros ordenada
                //da menor para a maior distância entre o usuário e o Pronto Socorro
                ProntoSocorro vet[] = restTemplate.getForObject(url, ProntoSocorro[].class);

                for(int i=0; i<vet.length; i++)
                    lista.add(vet[i]);

                return lista;

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

        }

    }
}
