package com.example.mytripplanner;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class LocationItem {
    int index;
    String name;
    LatLng geo;

    public LocationItem (int index, String name, LatLng geo) {
        this.index = index;
        this.name = name;
        this.geo = geo;
    }
}
