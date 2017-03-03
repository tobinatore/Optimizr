package com.github.tobinatore.optimizr;


import java.util.ArrayList;

/**
 * Ein Interface um Daten zwischen Fragments zu tauschen
 */

public interface DataCommunication{
    String[] getAddresses();
    ArrayList<Integer> getRoute();
    double getTotalDistance();
    void setAddresses(String[] addresses);
    void setRoute(ArrayList<Integer> route);
    void setTotalDistance(double totalDistance);
}