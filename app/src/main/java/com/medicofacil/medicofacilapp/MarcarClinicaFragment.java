package com.medicofacil.medicofacilapp;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.medicofacil.medicofacilapp.classesDBO.Clinica;
import com.medicofacil.medicofacilapp.classesDBO.Consulta;
import com.medicofacil.medicofacilapp.classesDBO.Convenio;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


public class MarcarClinicaFragment extends Fragment {
    private static final String INDEX = "http://webservicepaciente.cfapps.io/";
    private static final int ORDEM_DISTANCIA = 0;

    private Spinner spinner;
    private SearchView pesquisa;
    private ListView lstClinicas;
    private ArrayList<Clinica> lista;
    private String lat, lon;
    private AlertDialog msg;
    private int posicaoItem;
    private Convenio convenio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_marcar_clinica, container, false);
        ConsultarActivity activity = (ConsultarActivity)getActivity();
        activity.setAlterando(false);
      //  convenio = activity.getConvenio();

        Localizacao localizacao = activity.getLocalizacao();
        lat = localizacao.getLatitude()+"";
        lon = localizacao.getLongitude()+"";

        lstClinicas = (ListView) view.findViewById(R.id.lstClinicas);
        lista = new ArrayList<Clinica>();

        spinner = (Spinner) view.findViewById(R.id.spnOrdem);
        String [] itens = new String[]{"Mais próximo", "Ordem alfabética"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, itens);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                if(lista.size() > 0)
                {
                    ClinicaAdapter adaptador;

                    if (position == ORDEM_DISTANCIA)
                        adaptador = new ClinicaAdapter(MarcarClinicaFragment.this.getActivity(), lista);
                    else
                        adaptador = new ClinicaAdapter(MarcarClinicaFragment.this.getActivity(), ordenaAlfabetica());

                    lstClinicas.setAdapter(adaptador);
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

        //quando se desejar pesquisar algo
        pesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //quando for clicado no botao de pesquisar
            @Override
            public boolean onQueryTextSubmit(String s) {
                //se foi digitado alguma coisa, pesquise
                if(!pesquisa.getQuery().toString().isEmpty())
                    new ClinicasTask().execute(lat, lon, pesquisa.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        lstClinicas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicaoItem, long l) {

                MarcarClinicaFragment.this.posicaoItem = posicaoItem;

                ArrayList<String> itens = new ArrayList<String>();
                itens.add("Visualizar no mapa");
                itens.add("Pesquisar médicos");

                //adapter utilizando um layout customizado (TextView)
                ArrayAdapter adapter = new ArrayAdapter(MarcarClinicaFragment.this.getActivity(),
                        R.layout.item_dialog, itens);

                AlertDialog.Builder builder = new AlertDialog.Builder(MarcarClinicaFragment.this.getActivity());
                builder.setTitle("O que deseja?");

                //define o diálogo como uma lista, passa o adapter.
                builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog , int posicao) {

                        Clinica clinica  = lista.get(MarcarClinicaFragment.this.posicaoItem);

                        if(posicao == 0)//se quer visualizar a clínica no mapa
                        {
                            Intent intent = new Intent(getActivity(), MapaActivity.class);

                            //passa a geolocalização para o mapa
                            Bundle bundle = new Bundle();
                            bundle.putString("clinica", clinica.getLatitude()+"/"+clinica.getLongitude());
                            intent.putExtras(bundle);

                            getActivity().startActivity(intent);
                        }
                        else
                        {
                            FragmentTransaction gerente = getActivity().getSupportFragmentManager().beginTransaction();
                            ConsultarActivity activity = (ConsultarActivity) getActivity();
                            //guarda a clínica clicada
                            activity.setClinica(clinica);
                            gerente.replace(R.id.fragmentConteiner, new MarcarMedicoFragment());
                            gerente.commit();
                        }

                        msg.dismiss();
                    }
                });

                msg = builder.create();
                msg.show();
            }
        });

        //executa a pesquisa padrão
        new ClinicasTask().execute(lat, lon);

        // Inflate the layout for this fragment
        return view;
    }

    //retorna uma lista com os nomes ordenados por ordem alfabética
    private ArrayList<Clinica> ordenaAlfabetica()
    {
        ArrayList<Clinica> vetAux = new ArrayList<Clinica>();

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
                    Clinica aux = vetAux.get(i);

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


    private class ClinicasTask extends AsyncTask<String, Void, ArrayList<Clinica>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MarcarClinicaFragment.this.getContext(), "Aguarde",
                    "Aguarde um momento, estamos buscando as clínicas deste convênio no nosso sistema");

            //limpa a lista
            lista.clear();
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected ArrayList<Clinica> doInBackground(String... params) {
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
                //pesquise por nome todas as clínicas daquele convênio
                if(!nome.isEmpty())
                    url += "getClinicasPorNome/" + nome + "/" + lat + "/" + lon; //"getClinicasPorNomeConvenio/"+convenio.getId()+"/"+lat+"/"+lon;
                else
                  url += "getClinicas/"+lat+"/"+lon; //"getClinicasPorConvenio/"+convenio.getId()+"/"+lat+"/"+lon;

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                //o web service retorna uma lista de prontos socorros ordenada
                //da menor para a maior distância entre o usuário e o PS
                Clinica vet[] = restTemplate.getForObject(url, Clinica[].class);

                for(int i=0; i<vet.length; i++) {
                    lista.add(vet[i]);
                }

                return lista;

            } catch (Exception e) {
                Log.e("BuscarClinica", e.getMessage(), e);
            }

            return null;
        }

        protected void onPostExecute(ArrayList<Clinica> clinicas) {
            dialog.dismiss();

            if(clinicas != null)
            {
                ClinicaAdapter adaptador;

                if(spinner.getSelectedItemPosition() == ORDEM_DISTANCIA)
                    adaptador = new ClinicaAdapter(MarcarClinicaFragment.this.getActivity(), clinicas);
                else
                    adaptador = new ClinicaAdapter(MarcarClinicaFragment.this.getActivity(), ordenaAlfabetica());

                lstClinicas.setAdapter(adaptador);
            }
        }

    }

}
