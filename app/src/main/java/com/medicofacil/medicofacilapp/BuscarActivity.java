package com.medicofacil.medicofacilapp;

import android.location.Location;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;


public class BuscarActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);


        ActionBar barra = this.getActionBar();
        barra.setTitle("Buscar");

        barra.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab1 = barra.newTab();
        tab1.setText("Hospitais");
        tab1.setTabListener(new NavegacaoTab(new BuscarProntoSocorroFragment()));
        barra.addTab(tab1);

        ActionBar.Tab tab2 = barra.newTab();
        tab2.setText("Clínicas");
        tab2.setTabListener(new NavegacaoTab(new BuscarClinicaFragment()));
        barra.addTab(tab2);

        SupportMapFragment manipulaMapa = SupportMapFragment.newInstance();

        ActionBar.Tab tab3 = barra.newTab();
        tab3.setText("Mapa");
        tab3.setTabListener(new NavegacaoTab(manipulaMapa));
        barra.addTab(tab3);

        this.lat = 0;
        this.lon = 0;
        callConnection();

        //cria um mapa asincrono
        manipulaMapa.getMapAsync(this);

        //qnd sair e voltar recupera a última tab
        if (savedInstanceState != null) {
            int indiceTab = savedInstanceState.getInt("indiceTab");
            getActionBar().setSelectedNavigationItem(indiceTab);
        } else
            getActionBar().setSelectedNavigationItem(0);
            getActionBar().setSelectedNavigationItem(0);
    }

    private synchronized void callConnection(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                .addOnConnectionFailedListener(this)
                                .addConnectionCallbacks(this)
                                .addApi(LocationServices.API)
                                .build();

        mGoogleApiClient.connect();
    }

    //é chamado quando após o mapa ser criado
    @Override
    public void onMapReady(GoogleMap map) {

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lon), 16));

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.voce))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(lat, lon)));

        map.addMarker(new MarkerOptions()
                      .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marca_clinica))
                      .anchor(0.0f, 1.0f)
                      .position(new LatLng(41.889, -87.822)));


        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marca_hospital))
                .anchor(0.0f, 1.0f)
                .position(new LatLng(-31.889, -87.822)));

        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marca_clinica))
                .anchor(0.0f, 1.0f)
                .position(new LatLng(-22.889, -102)));


        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marca_hospital))
                .anchor(0.0f, 1.0f)
                .position(new LatLng(-31.889, -7.822)));
    }

    public void onBackPressed() {
        //limpa a pilha de activities e volta para a Main
        Intent main = new Intent(this, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
    }


    //salva a última sessão antes de sair
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("indiceTab", getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Location localizacao = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(localizacao != null)
        {
            this.lat = localizacao.getLatitude();
            this.lon = localizacao.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Falha na busca de sua geolocalização", Toast.LENGTH_SHORT).show();
    }

    private class NavegacaoTab implements ActionBar.TabListener{

        private Fragment frag;

        public NavegacaoTab(Fragment frgmt)
        {
            this.frag = frgmt;
        }

        //troca de fragment
        @Override
        public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            FragmentTransaction gerente = BuscarActivity.this.getSupportFragmentManager().beginTransaction();
            gerente.replace(R.id.fragmentConteiner, frag);
            gerente.commit();
        }

        //volta para o fragment inicial
        @Override
        public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            FragmentTransaction gerente = BuscarActivity.this.getSupportFragmentManager().beginTransaction();
            gerente.remove(frag);
            gerente.commit();
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

        }
    }

}
