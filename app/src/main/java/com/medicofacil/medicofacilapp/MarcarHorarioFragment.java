package com.medicofacil.medicofacilapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class MarcarHorarioFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marcar_horario, container, false);

        ImageButton btnData = (ImageButton) view.findViewById(R.id.btnData);
        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DatePicker calendario = new DatePicker(getActivity(), "Dr.Fulano");
                    calendario.start();
                    calendario.getDatePicker().setAccentColor(Color.rgb(31,106,218)); //altera a cor do datePicker


                }
                catch(Exception e){e.printStackTrace();}
            }
        });



        return view;
    }

    private class DatePicker implements DatePickerDialog.OnDateSetListener,  DialogInterface.OnCancelListener  {

        private int year, month, day;
        private Activity activity;
        private String titulo= "";
        private DatePickerDialog datePicker;


        public DatePicker(Activity activity, String titulo) throws Exception{
            this.setActivity(activity);
            this.setTitulo(titulo);
        }

        public void start()
        {
            inicializaData();

            Calendar dataPadrao = Calendar.getInstance();
            dataPadrao.set(year, month, day); //data atual

            //muda a data para o dia atual
            datePicker = DatePickerDialog.newInstance(this,
                    dataPadrao.get(Calendar.YEAR),
                    dataPadrao.get(Calendar.MONTH),
                    dataPadrao.get(Calendar.DAY_OF_MONTH));

            datePicker.setSelectableDays(this.diasSelecionaveis(dataPadrao));
            datePicker.setTitle(titulo);

            datePicker.setOnCancelListener(this);

            datePicker.show(activity.getFragmentManager(), "datePicker");
        }

        public void setDiasDisponiveis(Calendar[] dias)
        {
            datePicker.setSelectableDays(dias);
            datePicker.show(activity.getFragmentManager(), "datePicker");
        }

        //lógia para deixar selecionar somente dias
        //de segunda à sexta que estão entre a data min e máx
        //se quiser mudar é só passar um vetor de Calender com
        //os dias selecionáveis no método setSelectableDays do
        //datePickture
        private Calendar[] diasSelecionaveis(Calendar dataPadrao){

            Calendar dataMin = Calendar.getInstance();//data inicial
            Calendar dataMax = Calendar.getInstance();//data final

            //último dia do ano, lembrando q os meses começam no 0
            // ou seja Janeiro = 0 e Dezembro = 11
            dataMax.set(dataPadrao.get(Calendar.YEAR)+1, dataPadrao.get(Calendar.MONTH), dataPadrao.get(Calendar.DAY_OF_MONTH));

            datePicker.setMinDate(dataMin);
            datePicker.setMaxDate(dataMax);

            List<Calendar> listaDias = new LinkedList<>();
            Calendar[] vetorDias;

            Calendar calendAux = Calendar.getInstance();

            final  int DIA = 24*60*60*1000; //1 dia em milisegundos
            while(calendAux.getTimeInMillis() <= dataMax.getTimeInMillis())
            {
                //dias da semana Domingo = 1 e Sábado = 7
                if(calendAux.get(Calendar.DAY_OF_WEEK) != 1 &&
                        calendAux.get(Calendar.DAY_OF_WEEK) != 7)
                {

                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis( calendAux.getTimeInMillis() );
                    listaDias.add(c);
                }

                calendAux.setTimeInMillis(calendAux.getTimeInMillis() + DIA);
            }

            vetorDias = new Calendar[listaDias.size()];

            for(int i = 0; i < vetorDias.length; i++)
            {
                vetorDias[i] = listaDias.get(i);
            }

            return vetorDias;
        }


        private void inicializaData()
        {
            if(year == 0)//caso a data esteja vazia
            {
                Calendar calendario = Calendar.getInstance();

                year = calendario.get(Calendar.YEAR);
                month = calendario.get(Calendar.MONTH);
                day = calendario.get(Calendar.DAY_OF_MONTH);
            }
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            //zera a data
            year = month = day = 0;
        }

        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            this.year = year;
            this.month = monthOfYear;
            this.day = dayOfMonth;

            TextView txtData = (TextView)getActivity().findViewById(R.id.txtEspecialidade);
            txtData.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
        }



        public void setActivity(Activity activity) throws Exception{
            if(activity == null)
                throw new Exception("view nula. Classe: DatePickerData");

            this.activity = activity;
        }


        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public DatePickerDialog getDatePicker()
        {
            return this.datePicker;
        }
    }


}
