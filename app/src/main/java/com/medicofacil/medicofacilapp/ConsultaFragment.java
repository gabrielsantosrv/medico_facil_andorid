package com.medicofacil.medicofacilapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConsultaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConsultaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultaFragment extends Fragment {
    private Spinner spinner;
    private SearchView pesquisa;

    public ConsultaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_consulta, container, false);

        spinner = (Spinner) view.findViewById(R.id.spnOrdem);
        String [] itens = new String[]{"Ordem alfabética", "Mais próximo"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, itens);
        spinner.setAdapter(adapter);

        pesquisa = (SearchView)view.findViewById(R.id.srcPesquisa);

        pesquisa.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.INVISIBLE);
            }
        });

        pesquisa.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                spinner.setVisibility(View.VISIBLE);
                return false;
            }
        });

        return view;
    }
}
