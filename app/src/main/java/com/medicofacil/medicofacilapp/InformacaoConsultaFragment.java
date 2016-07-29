package com.medicofacil.medicofacilapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medicofacil.medicofacilapp.classesDBO.Consulta;

public class InformacaoConsultaFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_informacao_consulta, container, false);

        TextView txtNomeMedico = (TextView) view.findViewById(R.id.txtNomeMedico);
        TextView txtNomeClinica = (TextView) view.findViewById(R.id.txtNome);
        TextView txtEspecialidade = (TextView) view.findViewById(R.id.txtEspecialidade);
        TextView txtDataHora = (TextView) view.findViewById(R.id.txtDataHora);
        TextView txtEndereco = (TextView) view.findViewById(R.id.txtEndereco);
        TextView txtBairro = (TextView) view.findViewById(R.id.txtBairro);
        TextView txtCidade = (TextView) view.findViewById(R.id.txtCidade);
        TextView txtTelefone = (TextView) view.findViewById(R.id.txtTelefone);

        //recupera a consulta guardada no momento em que se selecionou uma consulta no fragment Consulta
        Consulta consulta = ((ConsultarActivity)getActivity()).getConsulta();

        txtNomeMedico.setText(consulta.getHorario().getMedico().getNome());
        //txtEspecialidade.setText(consulta.getHorario().getMedico().getEspecialidade());
        txtNomeClinica.setText(consulta.getHorario().getClinica().getNome());
        txtEndereco.setText("Endereço: "+consulta.getHorario().getClinica().getEndereco());
        txtBairro.setText("Bairro: "+consulta.getHorario().getClinica().getBairro());
        txtCidade.setText("Cidade:"+consulta.getHorario().getClinica().getCidade()+" - "+consulta.getHorario().getClinica().getUf());

        //formato original telefone: 0000000000
        //formato convertido: (00) 0000-0000
        String tel = consulta.getHorario().getClinica().getTelefone();
        String ddd, numIni, numFim;

        ddd = tel.substring(0,2);
        numIni = tel.substring(2,6);
        numFim = tel.substring(6);

        txtTelefone.setText("Tel: ("+ddd+") "+numIni+"-"+numFim);

        //formato da data do getDate().toString(): yyyy-mm-dd
        //formato desejado dd/mm/yyyy
        String data = consulta.getHorario().getData().toString();
        String dia, mes, ano;

        dia = data.substring(8);
        mes = data.substring(5,7);
        ano = data.substring(0,4);

        txtDataHora.setText(dia+"/"+mes+"/"+ano+"     "+
                            consulta.getHorario().getHoraInicio().toString().substring(0,5)+" - "+
                            consulta.getHorario().getHoraFim().toString().substring(0,5));

        // Inflate the layout for this fragment
        return view;
    }
}
