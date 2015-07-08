package com.cmu.jayakumaur.galleryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

/**
 * Created by Jayakumaur on 25-06-2015.
 * Class to contain the logic that goes in to display
 * the camera
 */

public class Tab2Activity extends Fragment// implements LocationListener
 {
     Button takePhotoButton, recordVideoButton;
     Intent intent;
     @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
         View v = inflater.inflate(R.layout.camera, container, false);
         takePhotoButton = (Button) v.findViewById(R.id.photoButton);
         recordVideoButton = (Button) v.findViewById(R.id.videoButton);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
            intent = new Intent(getActivity(), CameraActivity.class);
            getActivity().startActivity(intent);
//          getActivity().startActivityForResult(intent,45);
             }
         });
         recordVideoButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 intent = new Intent(getActivity(), RecordVideoActivity.class);
                 getActivity().startActivity(intent);
//              getActivity().startActivityForResult(intent,45);
             }
         });

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