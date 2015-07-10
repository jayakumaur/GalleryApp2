package com.cmu.jayakumaur.galleryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

/**
 * Created by Jayakumaur on 06-07-2015.
 */
public class CameraActivity extends Activity {
    private Button cameraButton;
    private Intent intent, previewIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_takeimage);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2ImageFragment.newInstance())
                    .commit();
            Log.d("----------->","End of Camera Activity...");
        }
    }
}

