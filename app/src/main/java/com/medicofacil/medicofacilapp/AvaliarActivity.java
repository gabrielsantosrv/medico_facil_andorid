package com.medicofacil.medicofacilapp;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class AvaliarActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliar);

        ActionBar barra = this.getActionBar();
        barra.setTitle("Avaliar");
    }
}
