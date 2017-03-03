package com.github.tobinatore.optimizr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/** Präsentiert dem Nutzer das Ergebnis und ermöglicht es ihm die Karte zu öffnen */

public class SolutionFragment extends Fragment {

    ArrayList<Integer> route = new ArrayList<>(); // Die Route
    String[] addresses; // Die Adressen wie sie der User eingegeben hat

    DataCommunication mCallback;

    TextView home_address;
    TextView first_address;
    TextView second_address;
    TextView third_address;
    TextView fourth_address;
    TextView fifth_address;
    TextView sixth_address;
    TextView seventh_address;
    TextView eigth_address;
    TextView ninth_address;
    TextView tenth_address;
    TextView back_home_address;
    TextView tDist;

    public SolutionFragment() {
        // Geforderter leerer Konstruktor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            mCallback = (DataCommunication) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "muss DataCommunication implementieren!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_solution, container, false);

        home_address = (TextView) v.findViewById(R.id.textView4);
        first_address = (TextView) v.findViewById(R.id.textView5);
        second_address = (TextView) v.findViewById(R.id.textView6);
        third_address = (TextView) v.findViewById(R.id.textView7);
        fourth_address = (TextView) v.findViewById(R.id.textView9);
        fifth_address = (TextView) v.findViewById(R.id.textView10);
        sixth_address = (TextView) v.findViewById(R.id.textView11);
        seventh_address = (TextView) v.findViewById(R.id.textView12);
        eigth_address = (TextView) v.findViewById(R.id.textView13);
        ninth_address = (TextView) v.findViewById(R.id.textView14);
        tenth_address = (TextView) v.findViewById(R.id.textView15);
        back_home_address = (TextView) v.findViewById(R.id.textView16);
        tDist = (TextView) v.findViewById(R.id.txt_totalDist);

        Button open_map = (Button) v.findViewById(R.id.btn_karte);

        open_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Die Karte öffnen und wichtige Daten anhängen
                 */

                Intent map_intent = new Intent(getActivity(),MapsActivity.class);
                map_intent.putIntegerArrayListExtra("route",route);
                map_intent.putExtra("addresses",addresses);
                startActivity(map_intent);
            }
        });

        return v;
    }

    public void updateUI() {
        route = mCallback.getRoute();
        addresses = mCallback.getAddresses();
        if (addresses != null) {
            try {
                /**
                * Anzeigen der ermittelten Route
                */
                tDist.setText(String.valueOf(mCallback.getTotalDistance())+ " km");
                home_address.setText(addresses[0]);
                first_address.setText(addresses[route.get(1)]);
                second_address.setText(addresses[route.get(2)]);
                third_address.setText(addresses[route.get(3)]);
                fourth_address.setText(addresses[route.get(4)]);
                fifth_address.setText(addresses[route.get(5)]);
                sixth_address.setText(addresses[route.get(6)]);
                seventh_address.setText(addresses[route.get(7)]);
                eigth_address.setText(addresses[route.get(8)]);
                ninth_address.setText(addresses[route.get(9)]);
                tenth_address.setText(addresses[route.get(10)]);
                back_home_address.setText(addresses[0]);

            } catch (IndexOutOfBoundsException e) {
                //Abfangen des Fehlers falls der User weniger als zehn weitere Adressen eingegeben hat.
                // ErrorHandling nicht notwendig, da die entsprechenden TextViews einfach leer bleiben
            }
    }
}

}
