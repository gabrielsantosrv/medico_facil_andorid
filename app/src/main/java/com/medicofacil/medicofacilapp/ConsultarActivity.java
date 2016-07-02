package com.medicofacil.medicofacilapp;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class ConsultarActivity extends FragmentActivity {

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



        //qnd sair e voltar recupera a última tab
        if(savedInstanceState != null){
            int indiceTab = savedInstanceState.getInt("indiceTab");
            getActionBar().setSelectedNavigationItem(indiceTab);
        }
        else
            getActionBar().setSelectedNavigationItem(0);

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

}
