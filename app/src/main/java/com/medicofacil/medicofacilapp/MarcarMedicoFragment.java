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
import android.widget.Toast;

import com.medicofacil.medicofacilapp.classesDBO.Clinica;
import com.medicofacil.medicofacilapp.classesDBO.Medico;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


public class MarcarMedicoFragment extends Fragment {

    private static final String INDEX = "http://webservicepaciente.cfapps.io/";
    private ArrayList<Medico> lista;
    private ListView lstMedicos;
    private SearchView pesquisa;
    private Clinica clinica;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_medico, container, false);

        //recupera a clínica guardada no momento em que se selecionou uma clínica no fragment MarcarClinica
        clinica = ((ConsultarActivity)getActivity()).getClinica();

        lista = new ArrayList<Medico>();

        TextView txtNomeClinica = (TextView)view.findViewById(R.id.txtNome);
        txtNomeClinica.setText(clinica.getNome());

        pesquisa = (SearchView)view.findViewById(R.id.srcPesquisa);

        //quando se desejar pesquisar algo
        pesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //quando for clicado no botao de pesquisar
            @Override
            public boolean onQueryTextSubmit(String s) {
                //se foi digitado alguma coisa, pesquise
                if(!pesquisa.getQuery().toString().isEmpty())
                    new MedicosTask().execute(pesquisa.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        lstMedicos = (ListView)view.findViewById(R.id.lstMedicos);

        lstMedicos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //muda o fragment da Tab selecionada(marcação de médico) para marcação de horário
                FragmentTransaction gerente = getActivity().getSupportFragmentManager().beginTransaction();

                ConsultarActivity activity = (ConsultarActivity) getActivity();
                //guarda o médico clicada
                activity.setMedico(lista.get(position));

                gerente.replace(R.id.fragmentConteiner, new MarcarDataHorarioFragment());
                gerente.commit();
            }
        });


        new MedicosTask().execute();

        return view;
    }

    private class MedicosTask extends AsyncTask<String, Void, ArrayList<Medico>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MarcarMedicoFragment.this.getContext(), "Aguarde",
                    "Aguarde um momento, estamos buscando os médicos desta clínica no nosso sistema");

            int tamanho = lista.size();

            //limpa a lista
            for(int i=0; i<tamanho; i++)
                lista.remove(0);
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected ArrayList<Medico> doInBackground(String... params) {
            try {

                String url = INDEX;

                //se foi passado parte do nome do médico por parâmetro
                if(params.length > 0)
                  url += "getMedicosPorNomeIdClinica/"+params[0];
                else
                    url += "getMedicosPorClinica";

                url += "/"+clinica.getId();

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Medico vet[] = restTemplate.getForObject(url, Medico[].class);

                for(int i=0; i<vet.length; i++)
                    lista.add(vet[i]);

                return lista;

            } catch (Exception e) {
                Log.e("Medico", e.getMessage(), e);
            }

            return null;
        }


        protected void onPostExecute(ArrayList<Medico> medicos) {
            dialog.dismiss();

            if(medicos.size() == 0)
            {
                Toast.makeText(getContext(), "Infelizmente a clínica "+clinica.getNome()+" não tem médicos disponíveis.", Toast.LENGTH_LONG);
                getActivity().getActionBar().setSelectedNavigationItem(0);
            }
            else
            {
                MedicoAdapter adaptador;
                adaptador = new MedicoAdapter(MarcarMedicoFragment.this.getActivity(), medicos);

                lstMedicos.setAdapter(adaptador);
            }
        }

    }

}
