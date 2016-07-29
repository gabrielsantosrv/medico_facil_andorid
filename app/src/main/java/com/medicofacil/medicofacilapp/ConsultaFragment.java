package com.medicofacil.medicofacilapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.medicofacil.medicofacilapp.classesDBO.Consulta;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.util.ArrayList;


public class ConsultaFragment extends Fragment {

    private AlertDialog msg;
    private ListView lstConsultas;
    private SearchView pesquisa;
    private static final String INDEX = "http://webservicepaciente.cfapps.io/";
    private ArrayList<Consulta> lista;
    private int posicao;

    public ConsultaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_consulta, container, false);
        pesquisa = (SearchView)view.findViewById(R.id.srcPesquisa);
        lista = new ArrayList<Consulta>();

        posicao = -1;

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
        lstConsultas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                ArrayList<String> itens = new ArrayList<String>();
                itens.add("+ informações");
                itens.add("Alterar");
                itens.add("Cancelar");

                ArrayAdapter adapter = new ArrayAdapter(ConsultaFragment.this.getActivity(), R.layout.item_dialog, itens);

                AlertDialog.Builder builder = new AlertDialog.Builder(ConsultaFragment.this.getActivity());
                builder.setTitle("Opções");

                posicao = pos;
                //define o diálogo como uma lista, passando o adapter como parâmetro.
                builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog , int position) {

                        ConsultarActivity activity = (ConsultarActivity)getActivity();

                        switch (position)
                        {
                            //+informações
                            case 0:
                                //muda o fragment da Tab selecionada(marcação de médico) para marcação de horário
                                FragmentTransaction gerente = getActivity().getSupportFragmentManager().beginTransaction();
                                //guarda a consulta selecionada na Activity
                                //para poder ser recuperada depois
                                activity.setConsulta(lista.get(posicao));

                                gerente.replace(R.id.fragmentConteiner, new InformacaoConsultaFragment());
                                gerente.commit();
                                break;

                            //alterar
                            case 1:
                                //altera a frag de operação para ALTERAR
                                activity.setOperacao(ConsultarActivity.ALTERAR);
                                activity.setAlterando(true);
                                activity.setIdConsulta(lista.get(posicao).getId());
                                //muda para a tab de marcação de consultas
                                activity.getActionBar().setSelectedNavigationItem(1);
                                break;

                            //cancelar
                            case 2:
                                 new CancelarConsultasTask(posicao).execute();
                                break;
                        }


                        msg.dismiss();
                    }
                });

                msg = builder.create();
                msg.show();
            }
        });


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


                String url = INDEX+"getConsultas";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


                Consulta vet[] = restTemplate.getForObject(url, Consulta[].class);

                for(int i=0; i<vet.length; i++) {
                    lista.add(vet[i]);
                }

                return lista;

            } catch (Exception e) {
                Log.e("Consulta", e.getMessage(), e);
            }

            return null;
        }


        protected void onPostExecute(ArrayList<Consulta> consultas) {
            dialog.dismiss();

            if(consultas != null)
            {
                ConsultaAdapter adaptador;
                adaptador = new ConsultaAdapter(ConsultaFragment.this.getActivity(), consultas);

                lstConsultas.setAdapter(adaptador);
            }

        }

    }

    private class CancelarConsultasTask extends AsyncTask<Void, Void,Boolean> {

        private ProgressDialog dialog;
        private int posicao;

        public CancelarConsultasTask(int posicao)
        {
            this.posicao = posicao;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ConsultaFragment.this.getContext(), "Aguarde",
                    "Aguarde um momento, estamos cancelando sua consulta no nosso sistema");
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = INDEX+"cancelarConsulta/"+lista.get(posicao).getId();
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                return restTemplate.getForObject(url, Boolean.class);
            }
            catch (Exception e) {
                Log.e("Consulta", e.getMessage(), e);
            }

            return Boolean.FALSE;
        }


        protected void onPostExecute(Boolean resp) {
            dialog.dismiss();

            if(!resp.booleanValue())
                Toast.makeText(ConsultaFragment.this.getContext(), "Ocorreu um erro ao tentar cancelar esta consulta. Por favor, " +
                                                                   "tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            else
            //atualiza a lista de consultas
             new ConsultasTask().execute();
        }

    }

}
