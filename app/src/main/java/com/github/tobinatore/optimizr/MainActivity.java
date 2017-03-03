package com.github.tobinatore.optimizr;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/** MainActivity
 * Managed TabLayout und darin enthaltene Fragments
 */

public class MainActivity extends AppCompatActivity implements DataCommunication {


    /**
     * Der {@link android.support.v4.view.PagerAdapter} welcher Fragments
     * für jede Sektion bereitstellt. Wir nutzen eine
     * {@link FragmentPagerAdapter} Ableitung, welche jedes geladene Fragment
     * im Speicher behält. Bei nur 2 Fragments wird da nicht zu speicherintensiv.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * Der {@link ViewPager} welcher die Inhalte bereithält.
     */
    private ViewPager mViewPager;


    public String[] addresses = new String[11]; // Die vom AddressFragment zurückgegebenen Addressen als String
    public ArrayList<Integer> route = new ArrayList<>(); // Die vom AddressFragment bestimmte Route

    double totalDistance; //Die gesamte zurückgelegte Distanz



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Den ViewPager mit dem SectionsPagerAdapter initialisieren
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                SolutionFragment fragment = (SolutionFragment) getSupportFragmentManager()
                        .getFragments().get(1);
                if (position==1){
                    fragment.updateUI();
                }
            }

            @Override
            public void onPageSelected(int position) {
                SolutionFragment fragment = (SolutionFragment) getSupportFragmentManager()
                        .getFragments().get(1);
                if (position==1){
                    fragment.updateUI();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    /**
     *
     * Hier folgen einige aus dem Interface "DataCommunication" implementierte Methoden.
     * Diese werden genutzt um den Fragments zu ermöglichen, untereinander zu kommunizieren.
     *
     */
    @Override
    public String[] getAddresses(){
        return addresses;
    }

    @Override
    public double getTotalDistance(){
        return totalDistance;
    }

    @Override
    public ArrayList<Integer> getRoute(){
        return route;
    }

    @Override
    public void setAddresses(String[] addresses) {
        this.addresses = addresses;

    }


    @Override
    public void setRoute(ArrayList<Integer> route) {
        this.route = route;
    }
    @Override
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    /**
     * Ende der Interface-Methoden
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    /**
     * Ein PlatzhalterFragment
     */
    public static class PlaceholderFragment extends Fragment {


        public PlaceholderFragment() {
        }

        /**
         * Gibt das entsprechende Fragment zurück
         */
        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment = new PlaceholderFragment();

            if (sectionNumber == 1) {
                fragment = new AddressFragment();
            } else if (sectionNumber == 2) {
                fragment = new SolutionFragment();
            }

            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }
    }

    /**
     * Ein {@link FragmentPagerAdapter} welcher das einem Tab entsprechende Fragment zurückgibt
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }


        @Override
        public Fragment getItem(int position) {
           // getItem initialisiert das Fragment für die entsprechende Seite
            return PlaceholderFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            //Zahl der angezeigten Tabs festlegen
            return 2; //Hier 2
        }

        @Override
        public CharSequence getPageTitle(int position) {
          //Den Titel der Tabs festlegen
            switch (position) {
                case 0:
                    return "ADRESSEN";
                case 1:
                    return "ERGEBNIS";
            }
            return null;
        }
    }

}
