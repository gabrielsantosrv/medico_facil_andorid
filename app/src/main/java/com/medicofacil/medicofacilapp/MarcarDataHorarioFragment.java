package com.medicofacil.medicofacilapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.medicofacil.medicofacilapp.classesDBO.Clinica;
import com.medicofacil.medicofacilapp.classesDBO.Consulta;
import com.medicofacil.medicofacilapp.classesDBO.Horario;
import com.medicofacil.medicofacilapp.classesDBO.Medico;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MarcarDataHorarioFragment extends Fragment {

    private static final String INDEX = "http://webservicepaciente.cfapps.io/";
    private ArrayList<Horario> lista;
    private Medico medico;
    private Clinica clinica;
    private TextView txtData, txtHorario;
    private AlertDialog msg;
    private int posicaoHorario;
    private int idConsulta;
    private ArrayList<Horario> horariosLivres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marcar_horario, container, false);

        posicaoHorario = -1;

        lista  = new ArrayList<Horario>();
        horariosLivres = new ArrayList<Horario>();

        //recupera a clínica e o médico escolhidos
        medico = ((ConsultarActivity)getActivity()).getMedico();
        clinica = ((ConsultarActivity)getActivity()).getClinica();
        idConsulta = ((ConsultarActivity)getActivity()).getIdConsulta();

        TextView txtNome = (TextView)view.findViewById(R.id.txtNome);
        txtData = (TextView)view.findViewById(R.id.txtEspecialidade);
        txtHorario = (TextView)view.findViewById(R.id.txtDataHora);

        txtNome.setText(medico.getNome());

        ImageButton btnData = (ImageButton) view.findViewById(R.id.btnData);
        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DatePicker calendario = new DatePicker(getActivity(), medico.getNome());
                    calendario.start();
                    calendario.getDatePicker().setAccentColor(Color.rgb(31,106,218)); //altera a cor do datePicker

                    String data = calendario.getDatePicker().getSelectedDay().getDay()+"/"+
                                  calendario.getDatePicker().getSelectedDay().getMonth()+"/"+
                                  calendario.getDatePicker().getSelectedDay().getYear();

                    txtData.setText(data);
                }
                catch(Exception e){e.printStackTrace();}
            }
        });

        ImageButton btnHorario = (ImageButton) view.findViewById(R.id.btnHorario);
        btnHorario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    //formato da data: dd/MM/yyyy
                    String dataEscolhida = txtData.getText().toString();

                    if(dataEscolhida.isEmpty() || dataEscolhida.length() != 10)
                        Toast.makeText(MarcarDataHorarioFragment.this.getContext(), "Por favor, escolha uma data para marcar sua consulta!", Toast.LENGTH_SHORT);
                    else
                    {
                      ArrayList<String> itens = new ArrayList<String>();

                      int dia, mes, ano, diaAux, mesAux, anoAux;;

                      //uso esta converção dos campos data para inteiro,
                      //porque os métodos que fariam isso (getDay, getMonth e getYear) estão defasados

                      dia = Integer.parseInt(dataEscolhida.substring(0,2));
                      mes = Integer.parseInt(dataEscolhida.substring(3,5));
                      ano = Integer.parseInt(dataEscolhida.substring(6));

                      //quando clicar no botão de escolher horário
                      //abrirá uma caixa de diálogo contendo
                      //todos os horários disponíveis para uma
                      //consulta com o médico previamente escolhido
                      for(int i=0; i<lista.size(); i++) {

                         //formato da tada do getDate().toString(): yyyy-mm-dd
                         String data = lista.get(i).getData().toString();

                         diaAux = Integer.parseInt(data.substring(8));
                         mesAux = Integer.parseInt(data.substring(5,7));
                         anoAux = Integer.parseInt(data.substring(0,4));


                         if (diaAux == dia && mesAux == mes && anoAux == ano) {

                           //exibe somente os horários do dia selecionado, e guarda-os
                           //no ArrayList horariosLivres para poder recuperar quando
                           //um horário for escolhido
                           Time horaInicio = lista.get(i).getHoraInicio();
                           Time horaFim = lista.get(i).getHoraFim();

                           //formato de hora do TIME: hh:mm:ss, transformo para hh:mm
                           itens.add(horaInicio.toString().substring(0,5) + " - " +horaFim.toString().substring(0,5));

                           //popula a lista com os horários somente do dia selecionado
                           horariosLivres.add(lista.get(i));
                         }
                      }

                     //adapter utilizando um layout customizado (TextView)
                     ArrayAdapter adapter = new ArrayAdapter(MarcarDataHorarioFragment.this.getActivity(), R.layout.item_dialog, itens);

                     AlertDialog.Builder builder = new AlertDialog.Builder(MarcarDataHorarioFragment.this.getActivity());
                     builder.setTitle("Escolha o horário para a sua consulta");

                     //define o diálogo como uma lista, passando o adapter como parâmetro.
                     builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog , int position) {

                            //recupera o horário escolhido
                            Time horaInicio = horariosLivres.get(position).getHoraInicio();
                            Time horaFim = horariosLivres.get(position).getHoraFim();

                            txtHorario.setText(horaInicio.toString().substring(0,5) + " - " +horaFim.toString().substring(0,5));

                            posicaoHorario = position;
                            msg.dismiss();
                        }
                     });

                     msg = builder.create();
                     msg.show();
                    }
            }
        });

        Button btnMarcar = (Button)view.findViewById(R.id.btnMarcar);
        btnMarcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CadastrarConsultaTask().execute();
            }
        });

        new DatasTask().execute();

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
                                                      dataPadrao.get(Calendar.DAY_OF_MONTH)+7);

            datePicker.setSelectableDays(this.setDiasDisponiveis());
            datePicker.setTitle(titulo);

            datePicker.setOnCancelListener(this);

            datePicker.show(activity.getFragmentManager(), "datePicker");
        }

        public Calendar[] setDiasDisponiveis()
        {
            Calendar[] vet = new Calendar[lista.size()];

            for(int i=0; i<lista.size(); i++)
            {
                Calendar calendar = Calendar.getInstance();
                Date data = lista.get(i).getData();
                calendar.setTime(data);

                vet[i] = calendar;
            }

            return vet;
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
            txtData.setText("");
            txtHorario.setText("");
        }

        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            this.year = year;
            this.month = monthOfYear;
            this.day = dayOfMonth;

            String dia = "", mes = "";

            if(dayOfMonth<10)
                dia = "0";

            //começa a contagem em 0
            if(monthOfYear<9)
                mes = "0";

            mes += (monthOfYear+1);
            dia += dayOfMonth;

            txtData.setText(dia+"/"+mes+"/"+year);
            txtHorario.setText("");

            //sempre que mudar a data colocamos o valor inicial
            //para a posição de um horário
            posicaoHorario = -1;

            horariosLivres.clear();
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


    private class DatasTask extends AsyncTask<String, Void, ArrayList<Horario>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MarcarDataHorarioFragment.this.getContext(), "Aguarde",
                    "Aguarde um momento, estamos buscando os horários disponíveis desse médico no nosso sistema");

            int tamanho = lista.size();

            //limpa a lista
            for(int i=0; i<tamanho; i++)
                lista.remove(0);
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected ArrayList<Horario> doInBackground(String... params) {
            try {

                //um médico pode atender várias clínicas
                //String url = INDEX+"/getHorariosLivres/"+medico.getId()+"/"+clinica.getId();
                String url = INDEX+"getHorariosLivres/"+medico.getId();

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Horario vet[] = restTemplate.getForObject(url, Horario[].class);

                for(int i=0; i<vet.length; i++)
                    lista.add(vet[i]);

                return lista;

            } catch (Exception e) {
                Log.e("Horario", e.getMessage(), e);
            }

            return null;
        }

        protected void onPostExecute(ArrayList<Horario> horarios) {
            dialog.dismiss();

            if(horarios.size() == 0)
            {
                Toast.makeText(getContext(), "Infelizmente o médico "+medico.getNome()+" não tem horários disponíveis.", Toast.LENGTH_LONG);
                getActivity().getActionBar().setSelectedNavigationItem(0);
            }
        }

    }

    private class CadastrarConsultaTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private String operacao;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MarcarDataHorarioFragment.this.getContext(), "Aguarde",
                    "Aguarde um momento, estamos marcando sua consulta no nosso sistema");
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected Boolean doInBackground(String... params) {
             try {
                if(posicaoHorario == -1)
                  return Boolean.FALSE;

                Consulta consulta = new Consulta(idConsulta,horariosLivres.get(posicaoHorario), null);

                String url = INDEX;

                ConsultarActivity activity = (ConsultarActivity)getActivity();

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                switch (activity.getOperacao())
                {
                    case ConsultarActivity.CADASTRAR:
                        url += "agendarConsulta";
                        operacao = "cadastrar";

                        restTemplate.put(url,consulta);
                        break;
                    case ConsultarActivity.ALTERAR:
                        url += "alterarConsulta";
                        operacao = "alterar";

                        return restTemplate.postForObject(url, consulta, Boolean.class);
                }

                return Boolean.TRUE;

            } catch (Exception e) {
                Log.e("Horario", e.getMessage(), e);
            }

            return Boolean.FALSE;
        }


        protected void onPostExecute(Boolean resp) {
            dialog.dismiss();

            if(!resp.booleanValue())
                if(posicaoHorario == -1)
                    Toast.makeText(MarcarDataHorarioFragment.this.getContext(), "Por favor, escolha um horário para marcarmos sua consulta!", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MarcarDataHorarioFragment.this.getContext(), "Ocorreu algum problema ao tentar "+operacao+" uma nova consulta. Por favor," +
                                                                                    "tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            else
            //se marcou a consulta volta para a tab de exibição de consulta
             getActivity().getActionBar().setSelectedNavigationItem(0);
        }

    }

}
