package com.medicofacil.medicofacilapp;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.medicofacil.medicofacilapp.classesDBO.Clinica;
import com.medicofacil.medicofacilapp.classesDBO.Consulta;
import com.medicofacil.medicofacilapp.classesDBO.Convenio;
import com.medicofacil.medicofacilapp.classesDBO.Medico;


public class ConsultarActivity extends FragmentActivity  implements  GoogleApiClient.ConnectionCallbacks,
                                                                     GoogleApiClient.OnConnectionFailedListener{

    public static final byte CADASTRAR = 0, ALTERAR = 1;
    private byte operacao;
    private boolean alterando;
    private Consulta consulta;
    private Clinica clinica;
    private Convenio convenio;
    private Medico medico;
    private int idConsulta;
    private GoogleApiClient mGoogleApiClient;
    private double lat, lon; //geolocalização do paciente

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public void setAlterando(boolean alterando) {
        this.alterando = alterando;
    }

    public byte getOperacao() {
        return operacao;
    }

    public void setOperacao(byte operacao) {
        this.operacao = operacao;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public Clinica getClinica() {
        return clinica;
    }

    public void setClinica(Clinica clinica) {
        this.clinica = clinica;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Localizacao  getLocalizacao() {
        //altera o lat e o lon (latitude e longitude)
        callConnection();

        //retorna essa geolocalização
        return new Localizacao (lat, lon);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar);

        ActionBar barra = this.getActionBar();

        barra.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        barra.setTitle("Consultar");

        Tab tab1 = barra.newTab();
        tab1.setText("Consultas");
        tab1.setTabListener(new NavegacaoTab(new ConsultaFragment()));
        barra.addTab(tab1);

        Tab tab2 = barra.newTab();
        tab2.setText("Marcar");
        tab2.setTabListener(new NavegacaoTab(new MarcarClinicaFragment()));
        barra.addTab(tab2);

        alterando = false;

        //qnd sair e voltar recupera a última tab
        if(savedInstanceState != null){
            int indiceTab = savedInstanceState.getInt("indiceTab");
            getActionBar().setSelectedNavigationItem(indiceTab);
        }
        else
            getActionBar().setSelectedNavigationItem(0);

    }

    //toda vez que a aplicação é pausada quando
    //ela volta, executa-se esse método (start)
    @Override
    protected void onStart() {
        super.onStart();

        //verifica se existe alguma conexão coma  Internet
        if(!VerificaFerramentas.isInternetHabilitada(
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)))
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConsultarActivity.this);

            synchronized (alertDialogBuilder) {
                alertDialogBuilder.setMessage("Você está sem conexão com a internet. Gostaria de habilitá-la?")
                        .setCancelable(false)
                        .setPositiveButton("Habilitar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(callGPSSettingIntent);
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
        }
    }

    //salva a última sessão antes de sair
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("indiceTab", getActionBar().getSelectedNavigationIndex());
    }

    private class NavegacaoTab implements ActionBar.TabListener{

        private Fragment frag;

        public NavegacaoTab(Fragment frgmt)
        {
            this.frag = frgmt;
        }

        //troca de fragment
        @Override
        public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {

            if(tab.getPosition() == 1 && !alterando)
                operacao = CADASTRAR;

            FragmentTransaction gerente = ConsultarActivity.this.getSupportFragmentManager().beginTransaction();
            gerente.replace(R.id.fragmentConteiner, frag);
            gerente.commit();
        }

        //volta para o fragment inicial
        @Override
        public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
            FragmentTransaction gerente = ConsultarActivity.this.getSupportFragmentManager().beginTransaction();
            gerente.remove(frag);
            gerente.commit();
        }

        @Override
        public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {

        }
    }

    public void onBackPressed() {
        //limpa a pilha de activities e volta para a Main
        Intent main = new Intent(this, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
    }

    private synchronized void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //se o GPS estiver habilitado, busque a localização do usuário
        if (VerificaFerramentas.isGspHabilitado((LocationManager)getSystemService(LOCATION_SERVICE)))
        {
            Location localizacao = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (localizacao != null) {  //guarada a localização atual do usuário
                this.lat = localizacao.getLatitude();
                this.lon = localizacao.getLongitude();
            }
        }
        else //se não estiver habilitado exiba uma mensagem para o usuário habilitar o GSP
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("GPS está desabilitado. Gostaria de habilitá-lo?")
                    .setCancelable(false)
                    .setPositiveButton("Habilitar", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            Intent callGPSSettingIntent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        }
                    });

            alertDialogBuilder.setNegativeButton("Cancelar",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            dialog.cancel();

                            //finaliza a aplicação
                            finish();
                        }
                    });

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Falha na busca de sua geolocalização", Toast.LENGTH_SHORT).show();
    }
}
