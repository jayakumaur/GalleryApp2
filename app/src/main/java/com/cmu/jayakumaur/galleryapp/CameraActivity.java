package com.cmu.jayakumaur.galleryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.io.File;

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
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
            Log.d("----------->","End of Camera Activity...");
        }
    }
}

