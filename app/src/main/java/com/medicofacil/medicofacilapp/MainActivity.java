package com.medicofacil.medicofacilapp;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

public class MainActivity extends Activity implements  GoogleApiClient.ConnectionCallbacks,
                                                       GoogleApiClient.OnConnectionFailedListener{

    private static final String PREF_NAME = "geolocalização";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnConsultar = (ImageButton) this.findViewById(R.id.btnConsultar);
        ImageButton btnBuscar = (ImageButton) this.findViewById(R.id.btnBuscar);
        ImageButton btnAvaliar = (ImageButton) this.findViewById(R.id.btnAvaliar);

        callConnection();

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent consultar = new Intent(MainActivity.this, ConsultarActivity.class);

                MainActivity.this.startActivity(consultar);
                MainActivity.this.finish();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buscar = new Intent(MainActivity.this, BuscarActivity.class);
                MainActivity.this.startActivity(buscar);
                MainActivity.this.finish();
            }
        });

        btnAvaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Intent avaliar = new Intent(MainActivity.this.getBaseContext(), AvaliarActivity.class);
                MainActivity.this.startActivity(avaliar);
                MainActivity.this.finish();*/

                Toast.makeText(MainActivity.this, "Nós ainda não temos essa opção. Aguar de as próximas atualizações!", Toast.LENGTH_SHORT).show();
            }
        });

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
        Location localizacao = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(localizacao != null)
        {
            //guarda a última geolocalização obtida
            SharedPreferences.Editor editor;

            SharedPreferences geo = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            editor = geo.edit();
            editor.putString("latitude", localizacao.getLatitude()+"");
            editor.putString("longitude", localizacao.getLongitude()+"");

            editor.commit();
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

