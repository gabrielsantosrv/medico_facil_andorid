package com.medicofacil.medicofacilapp;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Gabriel Oliveira on 28/07/2016.
 */
public abstract class VerificaFerramentas {

    public static boolean isGspHabilitado(LocationManager locationManager)
    {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isInternetHabilitada(ConnectivityManager cm)
    {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
