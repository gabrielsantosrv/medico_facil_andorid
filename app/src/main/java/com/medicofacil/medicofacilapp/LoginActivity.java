package com.medicofacil.medicofacilapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.medicofacil.medicofacilapp.classesDBO.LoginPaciente;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


public class LoginActivity extends Activity {

    private static final String PREF_NAME = "geolocalização";
    private boolean conectado;

    private static final String INDEX = "http://webservicepaciente.cfapps.io/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        conectado = false;
        //verifica se existe alguma conexão coma  Internet
        if(!VerificaFerramentas.isInternetHabilitada(
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)))
        {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage("Você está sem conexão com a internet. Gostaria de habilitá-la?")
                        .setCancelable(false)
                        .setPositiveButton("Habilitar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(callGPSSettingIntent);
                                conectado = true;
                            }
                        });

                alertDialogBuilder.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                //finaliza a aplicação
                                finish();
                            }
                        });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
        }
        else
        {
            conectado = true;
        }

        final EditText edtCpf = (EditText)findViewById(R.id.edtCpf);
        final EditText edtSenha = (EditText)findViewById(R.id.edtSenha);


        final TextView txtCadastrar = (TextView)this.findViewById(R.id.txtCadastrar);


        txtCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent site = new Intent(Intent.ACTION_VIEW);
                site.setData(Uri.parse("http://www.google.com"));
                startActivity(site);
            }
        });

        new ConectaBdTask().execute();

        Button btnEntrar = (Button)findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inicia e executa a classe responsável por fazer requisições ao Web Service
                new LoginTask().execute(edtCpf.getText().toString(), edtSenha.getText().toString());
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        //quando fechar a aplicação
        //libera a conexão com o
        //banco de dados

        try {
            final String url = INDEX+"desconectarBd";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }

        //limpa os arquivos temporários
        //de geolocalização
        SharedPreferences.Editor editor;

        SharedPreferences geo = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = geo.edit();

        editor.clear();
        editor.commit();
    }


    private class ConectaBdTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //se não tiver conexão com
            //internet não pesquisa nada
            if(!conectado)
             this.cancel(true);

        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected Boolean doInBackground(Void... params) {


            try {
                final String url = INDEX+"conectarBd";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                //faz a requisição ao Web Service
                Boolean conectado = restTemplate.getForObject(url, Boolean.class);

                return conectado;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return Boolean.FALSE;
        }


        protected void onPostExecute(Boolean conectado) {

            //caso não seja possível se conectar ao banco de dados
            //exibe uma mensagem indicando o problema e fecha o app
            if(!conectado.booleanValue())
            {
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Erro");
                alertDialog.setMessage("Pedimos desculpas, estamos com um problema no nosso sistema, em breve ele será solucionado!");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.setIcon(R.mipmap.error);
                alertDialog.show();
            }

        }

    }


    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(LoginActivity.this, "Aguarde",
                    "Aguarde um momento, estamos verificando suas credenciais no nosso sistema");
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected Boolean doInBackground(String... params) {
            try
            {
                final String url = INDEX+"loginPaciente";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                String cpf = params[0];
                String senha = params[1];

                LoginPaciente paciente  = new LoginPaciente(cpf, senha);

                return restTemplate.postForObject(url,paciente, Boolean.class);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return Boolean.FALSE;
        }


        protected void onPostExecute(Boolean isLogin) {
            dialog.dismiss();

            //se os dados estiverem corretos isLogin é true
            if(isLogin.booleanValue())
            {
                //não fechamos estaq Activity, e garantimos que
                //quando ela fechar o aplicativo também fecha
                //e assim liberamos a conexão com o banco

                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(main);
            }
            else
            {
                Toast.makeText(LoginActivity.this, "Usuário ou senha inválidos. Por favor, verifique seus dados e tente novamente.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
