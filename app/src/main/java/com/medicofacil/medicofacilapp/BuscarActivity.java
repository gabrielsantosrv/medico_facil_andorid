package com.medicofacil.medicofacilapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.zza;
import com.medicofacil.medicofacilapp.classesDBO.Clinica;
import com.medicofacil.medicofacilapp.classesDBO.ProntoSocorro;

import android.app.ActionBar;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


public class BuscarActivity extends FragmentActivity implements  GoogleApiClient.ConnectionCallbacks,
                                                                 GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private double lat, lon;//geolocalização do paciente
    private ProntoSocorro prontoSocorro;

    public ProntoSocorro getProntoSocorro() {
        return prontoSocorro;
    }

    public void setProntoSocorro(ProntoSocorro prontoSocorro) {
        this.prontoSocorro = prontoSocorro;
    }

    public Localizacao getLocalizacao() {
        //altera o lat e o lon (latitude e longitude)
        callConnection();

        //retorna essa geolocalização
        return new Localizacao(lat, lon);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        FragmentTransaction gerente = BuscarActivity.this.getSupportFragmentManager().beginTransaction();
        gerente.replace(R.id.fragmentConteiner, new BuscarProntoSocorroFragment());
        gerente.commit();
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BuscarActivity.this);

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
        if (VerificaFerramentas.isGspHabilitado((LocationManager) getSystemService(LOCATION_SERVICE))) {
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
