package com.github.tobinatore.optimizr;



import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;


import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** Liest die Adressen ein und findet die kürzeste Route mit Hilfe des Nearest Neighbour Algorithmus und der 2-Opt-Heuristik */

public class AddressFragment extends Fragment {

    double[][] distanceMatrix; // Die Matrix in welcher die Distanzen zwischen den einzelnen Orten gespeichert werden
    String[] addresses = new String[11]; // Die Adressen wie sie der User eingibt
    LatLng[] addressesLatLng = new LatLng[11]; // Die vom User eigegebenen Adressen als Längen- und Breitengrad
    ArrayList<Integer> route; //Die berechnete Route

    DataCommunication mCallback; //Der Callback zur MainActivity um Daten zwischen den Fragments auszutauschen

    int arrayLength = 0; // Die Zahl der vom User eingegebenen Adressen
    double bestDist; //zuerst ermittelte Gesamtdistanz

    public AddressFragment() {
    //Standardmässig leerer Konstruktor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            mCallback = (DataCommunication) context; //Anhängen des Interfaces zum Datentausch
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "muss DataCommunication implementieren!");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_adress, container, false);

        /**
         * Verschiedene wichtige UI-Elemente
         */
        Button check = (Button) v.findViewById(R.id.button_check);
        final TextView  start = (TextView) v.findViewById(R.id.startText);
        final TextView  address1 = (TextView) v.findViewById(R.id.address1);
        final TextView  address2 = (TextView) v.findViewById(R.id.address2);
        final TextView  address3 = (TextView) v.findViewById(R.id.address3);
        final TextView  address4 = (TextView) v.findViewById(R.id.address4);
        final TextView  address5 = (TextView) v.findViewById(R.id.address5);
        final TextView  address6 = (TextView) v.findViewById(R.id.address6);
        final TextView  address7 = (TextView) v.findViewById(R.id.address7);
        final TextView  address8 = (TextView) v.findViewById(R.id.address8);
        final TextView  address9 = (TextView) v.findViewById(R.id.address9);
        final TextView  address10 = (TextView) v.findViewById(R.id.address10);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Einlesen der Adressen
                 */
                addresses[0] = start.getText().toString();
                addresses[1] = address1.getText().toString();
                addresses[2] = address2.getText().toString();
                addresses[3] = address3.getText().toString();
                addresses[4] = address4.getText().toString();
                addresses[5] = address5.getText().toString();
                addresses[6] = address6.getText().toString();
                addresses[7] = address7.getText().toString();
                addresses[8] = address8.getText().toString();
                addresses[9] = address9.getText().toString();
                addresses[10] = address10.getText().toString();

                arrayLength = getLength(); //Bestimmen der Zahl von eingegebenen Adressen
                distanceMatrix = new double[arrayLength][arrayLength]; //Initialisierung der Matrix mit der Zahl der eingegebenen Adressen entsprechender Größe
                route = new ArrayList<>(); //Initialisierung der ArrayList
                bestDist = Double.POSITIVE_INFINITY;

                for (int i = 0; i < arrayLength;i++){
                    addressesLatLng[i] = getLocationFromAddress(getContext(),addresses[i]); //Durch Geocoding den Längen- und Breitengrad erhalten
                }

                for (int i = 0; i < arrayLength; i++){
                    for (int j = 0; j < arrayLength; j++) {
                        try {
                            if (addressesLatLng[i].equals(addressesLatLng[j])) {
                                distanceMatrix[i][j] = 0.00; //Die Distanz zwischen ein un demselben Ort ist 0
                            } else {
                                getDistances(i, j); // Distanz berechnen
                            }
                        }catch (NullPointerException e){ // NullPointer -> Adresse konnte nicht gefunden werden
                            Toast.makeText(getContext()," Bitte überprüfen Sie Ihre Eingabe auf Fehler oder versuchen Sie es in einem Moment noch einmal.",Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
                nearestNeighbourAlgorithm(); //Route ermitteln
            }

        });

        return v;
    }

    /**
     * Funktion zum Bestimmen des Längen- und Breitengrades eines Ortes
     * @param context der Context der Anwendung, benötigt um den Geocoder zu initialisieren
     * @param strAddress die Adresse des zu geocodenden Ortes
     * @return den Ort als Längen-und Breitengrad
     */
    public LatLng getLocationFromAddress(Context context, String strAddress) {

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

    /**
     * Funktion zur Bestimmung der Distanz zwischen zwei Städten
     * @param i der Vertex von dem die Verbindung ausgeht
     * @param j der Vertex an dem die Verbindung endet
     * distanceBetween nutzt nicht die Straßendaten von GoogleMaps,
     * weshalb das Ergebnis - je nach Eingaben - etwas von der realen
     * Distanz abweichen kann.
     *
     */
    public void getDistances(final int i, final int j){

        float[] results = new float[3];
        Location.distanceBetween(addressesLatLng[i].latitude,
                addressesLatLng[i].longitude, addressesLatLng[j].latitude,
                addressesLatLng[j].longitude, results);
        distanceMatrix[i][j] = results[0]/1000;

    }

    /**
     * Funktion zum Ermitteln der Anzahl der eingegebenen Adressen
     * @return festgestellte Länge oder Standardwert
     */
    public int getLength(){
        for (int i = 0; i < 11; i++){
            if(addresses[i].contentEquals("")){
                return i;
            }
        }
        return 11;
    }

    /**
     * Aufgrund der Tatsache, dass ich erst am 19.02. auf diesen Wettbewerb gestoßen bin, hatte ich leider keine Zeit den weitaus
     * besseren Algorithmus von Christofides zu implementieren, da dies noch eine separate Klasse für den Graphen, sowie die Implemen-
     * tierung von Kruskal's oder Prim's Algorithmus zum Finden des kleinsten Spannbaumes erfordert hätte.
     *
     * Stattdessen wende ich hier den Nearest-Neighbour-Algorithmus an um eine Obergrenze zu finden und optimiere danach das Ergebnis
     * mittels der 2-Opt-Heuristik. Dies ist zwar laufzeittechnisch nicht optimal, bei maximal 11 Strecken noch vertretbar.
     */

    //TODO: Durch Christofide's Algorithmus ersetzen
    public void nearestNeighbourAlgorithm(){
        //Nachricht damit der User Bescheid weiß dass das Programm nicht hängt
        Snackbar.make(getView(),"Berechnung wird gestartet.",Snackbar.LENGTH_SHORT);

        //neuer Thread um das UI nicht noch mehr zu blockieren
        new Thread(new Runnable() {
            @Override
            public void run() {
                Stack<Integer> stack = new Stack<>();
                int[] vis = new int[distanceMatrix.length];

                int nodes = distanceMatrix.length;
                int element, dist = 0, i;
                double min;
                boolean flag = false;

                route.add(0);
                vis[0] = 1;
                stack.push(0);

                while (!stack.isEmpty()){
                    element = stack.peek();
                    i = 0;
                    min = Double.POSITIVE_INFINITY;

                    while (i < nodes){
                        if(distanceMatrix[element][i] > 0 && vis[i] == 0){
                            if (min > distanceMatrix[element][i]){
                                min = distanceMatrix[element][i];
                                dist = i;
                                flag = true;
                            }
                        }
                        i++;
                    }

                    if (flag){
                        vis[dist] = 1;
                        route.add(dist);
                        stack.push(dist);
                        flag = false;
                        continue;
                    }
                    stack.pop();
                }
                route.add(0);

                for (int x = 0; x< route.size()-1; x++){
                   bestDist += distanceMatrix[route.get(x)][route.get(x+1)]; //momentane Distanz als die beste Distanz definieren

                }
                twoOpt();
            }

        }).start();




    }

    /**
     * Bei der 2-Opt-Heuristik werden zwei Kanten gestrichen und kreuzweise wieder eingefügt.
     * Hier werden die verschiedenen Möglichkeiten geprüft.
     */
        public void twoOpt() {
            double new_distance;
            ArrayList<Integer> new_route;

            for (int i = 1; i < route.size() - 2; i++) {
                for (int k = i + 1; k < route.size() - 1; k++) {
                    new_route = twoOptSwap(i, k);
                    new_distance = calculateTotalDistance(new_route);
                    if (new_distance < bestDist) {
                        route = new_route;
                        bestDist = new_distance;
                    }
                }
            }


            //Verschiedene Variablen an die MainActivity übergeben um sie im SolutionFragment nutzen zu können
            mCallback.setAddresses(addresses);
            mCallback.setRoute(route);
            mCallback.setTotalDistance(bestDist);

            Snackbar.make(getView(),"Berechnung abgeschlossen",Snackbar.LENGTH_LONG).show();
        }

    /**
     * Modifiziert die Route entsprechend der 2-Opt-Vorgaben
     * @return die neue Route
     */
    public ArrayList<Integer> twoOptSwap(int i, int k){
        ArrayList<Integer> newRoute = new ArrayList<>();



        for(int l = 0; l <= i-1; l++){
            newRoute.add(route.get(l));
        }

        for (int m = k; m >= i; m--){
            newRoute.add(route.get(m));
        }


        for(int j = k+1; j < route.size();j++){
            newRoute.add(route.get(j));
        }


        return newRoute;
    }


    /**
     * Funktion zum Finden der Gesamtdistanz der Rundreise
     * @param routeToCheck die Route von welcher die Gesamtdistanz ermittelt werden soll
     * @return die Distanz der gesamten Strecke
     */
    public double calculateTotalDistance(ArrayList<Integer> routeToCheck){
        double newDist = 0;
        for (int x = 0; x< routeToCheck.size()-1; x++){
            newDist += distanceMatrix[routeToCheck.get(x)][routeToCheck.get(x+1)];
        }
        return newDist;
    }

}
