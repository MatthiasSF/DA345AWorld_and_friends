package com.example.matth.p2;

/**
 * Singleton class that holds an static reference of the map
 *
 * @author Matthias Falk
 */
public class Singleton {
    private static Singleton instance = new Singleton();
    private static MapsActivity mapsActivity;

    private Singleton() {
    }

    public static Singleton getReference() {
        return instance;
    }

    public void setMapsActivity(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    public MapsActivity getMapsActivity() {
        return mapsActivity;
    }
}
