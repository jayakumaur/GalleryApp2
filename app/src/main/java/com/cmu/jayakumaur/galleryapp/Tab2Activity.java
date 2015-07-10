package com.cmu.jayakumaur.galleryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jayakumaur on 25-06-2015.
 * Class to contain the logic that goes in to display
 * the camera
 */

public class Tab2Activity extends Fragment// implements LocationListener
 {
     Button takePhotoButton, recordVideoButton, saveButton, discardButton;
     Intent intent;
     final int IMG_CODE = 44;
     final int VID_CODE = 47;
     String filePath = null;
     ImageView previewImage;
     VideoView previewVideo;
     String mediaFormat = null;
     Uri uri;
     @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View v = inflater.inflate(R.layout.camera, container, false);

            //instantiating the button objects
            takePhotoButton = (Button) v.findViewById(R.id.photoButton);
            recordVideoButton = (Button) v.findViewById(R.id.videoButton);
            saveButton = (Button) v.findViewById(R.id.saveButton);
            discardButton = (Button) v.findViewById(R.id.discardButton);
            previewImage = (ImageView) v.findViewById(R.id.previewImage);
            previewVideo = (VideoView) v.findViewById(R.id.previewVideo);

         //Listener for taking photos
         takePhotoButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                intent = new Intent(v.getContext(), CameraActivity.class);
                startActivityForResult(intent, IMG_CODE);
                }
         });

         //Listener for recording videos
         recordVideoButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 intent = new Intent(getActivity(), RecordVideoActivity.class);
                 startActivityForResult(intent, VID_CODE);
             }
         });
         return v;
     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         discardButton.setVisibility(View.VISIBLE);
         saveButton.setVisibility(View.VISIBLE);
         switch (requestCode) {
             case(IMG_CODE):
                //Media File is an Image
                 previewImage.setVisibility(View.VISIBLE);
                 mediaFormat ="image/jpeg";
                 uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                 if(resultCode == Activity.RESULT_OK){
                     filePath = data.getStringExtra("imagePath");
                     //Setting up image preview
                     previewImage.setVisibility(View.VISIBLE);
                     Bitmap myBitmap = decodeFile(new File(filePath));
                     previewImage.setImageBitmap(myBitmap);
                 }
                 break;

             case(VID_CODE):
                 //Media File is an Image
                 previewVideo.setVisibility(View.VISIBLE);
                 mediaFormat ="video/mp4";
                 uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                 if(resultCode == Activity.RESULT_OK){
                     filePath = data.getStringExtra("videoPath");
                     //Setting up video preview
                     previewVideo.setVideoPath(filePath);
                     previewVideo.start();
                 }
                 break;
         }
         //Listener for discarding the taken media file
         discardButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 //Obtaining the mediafile handler
                 File mediaFile = new File(filePath);
                 if (mediaFile.exists()) {
                     previewImage.setImageDrawable(null);

                     if (mediaFile.delete()) {
                         //Discarding the mediafile and providing user notification
                         String toastMessage = getString(R.string.toastMess_Deleted);
                         Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                     }
                 } else{
                     //mediaFile not available for deletion
                     String toastMessage = getString(R.string.toastMess_UnableToDelete);
                     Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                 }

                 //Doing away with the elements
                 previewVideo.setVisibility(View.GONE);
                 previewImage.setVisibility(View.GONE);
                 discardButton.setVisibility(View.GONE);
                 saveButton.setVisibility(View.GONE);
             }
         });

         //Listener for saving the taken media file
         saveButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                //Setting the contentvalues - MediaStore settings
                ContentValues values = new ContentValues();
                 Long time = System.currentTimeMillis()/1000;
                 values.put(MediaStore.Files.FileColumns.DATE_ADDED, time);
                 values.put(MediaStore.Files.FileColumns.DATE_MODIFIED, time);
                 values.put(MediaStore.Files.FileColumns.MIME_TYPE, mediaFormat);
                 values.put(MediaStore.Files.FileColumns.DATA, filePath);
                 getActivity().getApplicationContext().getContentResolver().insert(uri, values);

                 //Logging the device details whenever a mediaFile is saved
                 Log.d(getString(R.string.alertMess), getString(R.string.alertMess2) + getDisplayText());

                 //Providing user notification that the mediaFile has been saved
                 String toastMessage1 = getString(R.string.toastMess_Saved);
                 Toast.makeText(getActivity().getApplicationContext(), toastMessage1, Toast.LENGTH_SHORT).show();

                 //Doing away with the elements
                 previewImage.setImageDrawable(null);
                 previewVideo.setVisibility(View.GONE);
                 discardButton.setVisibility(View.GONE);
                 saveButton.setVisibility(View.GONE);
             }
         });

     }

     private Bitmap decodeFile(File f) {
         try {
             // Decode image size
             BitmapFactory.Options o = new BitmapFactory.Options();
             o.inJustDecodeBounds = true;
             BitmapFactory.decodeStream(new FileInputStream(f), null, o);

             // The new size we want to scale to
             final int REQUIRED_SIZE=540;

             // Find the correct scale value. It should be the power of 2.
             int scale = 1;
             while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                     o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                 scale *= 2;
             }

             // Decode with inSampleSize
             BitmapFactory.Options o2 = new BitmapFactory.Options();
             o2.inSampleSize = scale;
             return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
         } catch (FileNotFoundException e) {}
         return null;
     }

     public String getDisplayText(){
         //Andrew ID
         String andrewID = getString(R.string.andrewID);
         //Timestamp
         String timestamp = new SimpleDateFormat(getString(R.string.datetimeFormat)).format(new Date());
         // Device model
         String PhoneModel = android.os.Build.MODEL;
         // Android version
         String AndroidVersion = android.os.Build.VERSION.RELEASE;
         //Android API Level
         int APILevel = (android.os.Build.VERSION.SDK_INT);

         //Final text to be displayed
         String displayText = andrewID+" :"+PhoneModel+"-Android "+AndroidVersion+"-API Level "+APILevel+" : "+timestamp;
         return displayText;
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