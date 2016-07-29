package com.medicofacil.medicofacilapp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.medicofacil.medicofacilapp.classesDBO.Clinica;
import com.medicofacil.medicofacilapp.classesDBO.ProntoSocorro;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


public class MapaActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //coordenadas do paciente
    private double lat, lon;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment manipulaMapa;
    private String ps, clinica;
    private ArrayList<ProntoSocorro> listaPs;
    private ArrayList<Clinica> listaClinica;

    private static final String INDEX = "http://webservicepaciente.cfapps.io/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        Bundle bundle = getIntent().getExtras();
        //se uma clínica ou pronto socorro foi
        if(bundle != null) {
            ps = bundle.getString("ps");
            clinica = bundle.getString("clinica");
        }

        listaClinica = new ArrayList<Clinica>();
        listaPs = new ArrayList<ProntoSocorro>();

        manipulaMapa = SupportMapFragment.newInstance();

        //exibe o fragment do mapa
        FragmentTransaction gerente = MapaActivity.this.getSupportFragmentManager().beginTransaction();
        gerente.replace(R.id.fragmentConteiner, manipulaMapa);
        gerente.commit();

        callConnection();
        criaMapa();
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapaActivity.this);
            alertDialogBuilder.setMessage("Você está sem conexão com a internet. Gostaria de habilitá-la?")
                    .setCancelable(false)
                    .setPositiveButton("Habilitar", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            Intent callGPSSettingIntent = new Intent(
                                    Settings.ACTION_WIRELESS_SETTINGS);
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

    //coordenadas para onde a câmera se moverá
    private double latitude, longitude;

    //é chamado quando após o mapa ser criado
    @Override
    public void onMapReady(GoogleMap map) {

        //clinica e ps serão tratados de tal modo
        //que ambos nunca serão nulos simultaneamente,
        //somente, se o usuário ir para o mapa sem clicar
        //em nenhuma clínica ou pronto socorro
        if((clinica == null || clinica.isEmpty()) && (ps == null || ps.isEmpty()))
        {
            latitude = lat;
            longitude = lon;
        }
        else
        //quando a localização foi passada para o mapa ela era
        //do seguinte formato: latitude(Float)/longitude(Float)
        if(clinica == null) { //se escolheu um PS
            int iBarra =  ps.indexOf("/");
            latitude = Float.parseFloat(ps.substring(0,iBarra));
            longitude = Float.parseFloat(ps.substring(iBarra+1));
        }
        else//se escolheu uma clínica
         {
             int iBarra =  clinica.indexOf("/");
             latitude = Float.parseFloat(clinica.substring(0,iBarra));
             longitude = Float.parseFloat(clinica.substring(iBarra+1));
          }


        //move a câmera para as coordenadas indicadas

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude, longitude), 16));

        LatLng coord = new LatLng(lat, lon);
        //marcador do usuário
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.voce))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(coord)
                .flat(true));


        int tamanhoPs = listaPs.size();
        int tamanhoClinica = listaClinica.size();

        //cria os marcadores dos prontos socorros
        for(int i=0; i<tamanhoPs; i++) {
            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marca_hospital))
                    .anchor(0.0f, 1.0f) // Ancora o marcador à esquerda
                    .position(new LatLng(listaPs.get(i).getLatitude(), listaPs.get(i).getLongitude()))
                    .title(listaPs.get(i).getNome())
                    .snippet(listaPs.get(i).getEndereco()));
        }

        //cria os marcadores das clínicas
        for(int i=0; i<tamanhoClinica; i++) {
            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marca_clinica))
                    .anchor(0.0f, 1.0f) // Ancora o marcador à esquerda
                    .position(new LatLng(listaClinica.get(i).getLatitude(), listaClinica.get(i).getLongitude()))
                    .title(listaClinica.get(i).getNome())
                    .snippet(listaClinica.get(i).getEndereco()));
        }

    }

    public void onBackPressed() {
        //limpa a pilha de activities e volta para a Main
        Intent main = new Intent(this, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
    }

    private synchronized void callConnection() {
        new ClinicaPsTask().execute();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    public void criaMapa()
    {
        //cria um mapa asincrono
        manipulaMapa.getMapAsync(MapaActivity.this);
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

    private class ClinicaPsTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MapaActivity.this, "Aguarde",
                    "Aguarde um momento, estamos buscando os prontos socorros e clínicas no nosso sistema");

            int tamanhoPs = listaPs.size();
            int tamanhoClinica = listaClinica.size();

            //limpa as listas
            listaClinica.clear();
            listaPs.clear();
        }

        //quando doInBackground termina, é chamado o onPostExecute com o retorno do doInBackground
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                double lat =0, lon=0;

                if(params.length > 0)
                {
                    lat = Double.parseDouble(params[0]);
                    lon = Double.parseDouble(params[1]);
                }

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                String url = INDEX+"getClinicas/"+lat+"/"+lon;

                //o web service retorna uma lista de prontos socorros ordenada
                //da menor para a maior distância entre o usuário e o PS e a Clínica
                Clinica vetClinica[] = restTemplate.getForObject(url, Clinica[].class);

                for (int i = 0; i < vetClinica.length; i++)
                   listaClinica.add(vetClinica[i]);

                url = INDEX+"getProntoSocorros/"+lat+"/"+lon;

                ProntoSocorro vetPs[] = restTemplate.getForObject(url, ProntoSocorro[].class);

                for(int i=0; i<vetPs.length; i++)
                   listaPs.add(vetPs[i]);

                return Boolean.TRUE;

            } catch (Exception e) {
                Log.e("Buscar", e.getMessage(), e);
            }

            return Boolean.FALSE;
        }


        protected void onPostExecute(Boolean resultado) {
            dialog.dismiss();

            if(!resultado.booleanValue())
            {
                Intent intent = new Intent(MapaActivity.this, MainActivity.class);
                MapaActivity.this.startActivity(intent);
                MapaActivity.this.finish();
            }
        }

    }


}
