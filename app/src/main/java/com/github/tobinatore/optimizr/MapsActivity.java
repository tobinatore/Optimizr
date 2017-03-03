package com.github.tobinatore.optimizr;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Beinhaltet die Karte, auf welcher die Stationen zu sehen sind */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    LatLng[] coordinates = new LatLng[11]; // Die vom User eigegebenen Adressen als Längen- und Breitengrad
    String[] addresses = new String[11];   // Die vom User eigegebenen Adressen als String
    ArrayList<Integer> route = new ArrayList<>(); //Die ermittelte Route

    int arrayLength;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Das SupportMapFragment erhalten und benachrichtigt werden, wenn die Map genutzt werden kann
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            addresses = intent.getStringArrayExtra("addresses");
            route = intent.getIntegerArrayListExtra("route");
            arrayLength = route.size()-1;
        }

                for (int i = 0; i < arrayLength; i++){
                    coordinates[i] = geocode(getApplicationContext(),addresses[i]); //erneutes Geocoden um die Marker auf der Map platzieren zu können
                }




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       for (int i= 0; i < arrayLength; i++){
            mMap.addMarker(new MarkerOptions().position(coordinates[i])
                    .title(String.valueOf(i) + ". Station: " + addresses[i]));
            // Marker auf der Map platzieren und mit Titel sowie Platz in der Route anzeigen
        }

    }

    public LatLng geocode(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // Könnte IOException auslösen
            address = coder.getFromLocationName(strAddress, 5); // Geocoder geben Adressen im Format "Straße Hausnummer", "Stadt", "Land" zurück
            if (address == null) {
                return null;
            }
            Address location = address.get(0); // Straße und Hausnummer aus der Liste holen...
            location.getLatitude();            // ...und Längen- bwz Breitengrad bestimmen
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() ); //erhaltenes Ergebnis im LatLng-Format speichern

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

}
