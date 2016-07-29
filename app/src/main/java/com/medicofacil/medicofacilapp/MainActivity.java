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

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnConsultar = (ImageButton) this.findViewById(R.id.btnConsultar);
        ImageButton btnBuscar = (ImageButton) this.findViewById(R.id.btnBuscar);
        ImageButton btnAvaliar = (ImageButton) this.findViewById(R.id.btnAvaliar);

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
}

