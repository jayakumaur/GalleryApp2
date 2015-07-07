package com.cmu.jayakumaur.galleryapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jayakumaur on 25-06-2015.
 * Class to contain the logic that goes in to display
 * the camera and the tracking of the current position
 * Also responsible for reverse geocoding feature.
 */

public class Tab2Activity extends Fragment// implements LocationListener
 {
    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            //View v = inflater.inflate(R.layout.camera, container, false);
            View v = null;
            double currentLatitude=0;
            double currentLongitude=0;
            return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}