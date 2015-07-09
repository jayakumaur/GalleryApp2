package com.cmu.jayakumaur.galleryapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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
     @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View v = inflater.inflate(R.layout.camera, container, false);
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

         //Listener for saving the taken media file
         saveButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                Log.d("--ALERTMSG---->", "MEDIA FILE SAVED!:" + getDisplayText());
                String toastMessage1 = "Saved";
                Toast.makeText(getActivity().getApplicationContext(), toastMessage1, Toast.LENGTH_SHORT).show();
                previewImage.setImageDrawable(null);
                previewVideo.setVisibility(View.GONE);
                discardButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
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
                 previewImage.setVisibility(View.VISIBLE);
                 if(resultCode == Activity.RESULT_OK){
                     filePath = data.getStringExtra("imagePath");
                     //Listener for discarding the taken media file
                     discardButton.setOnClickListener(new View.OnClickListener() {
                         public void onClick(View v) {

//                             File imageFile = new File(filePath);
                             if (new File(filePath).exists()) {
                                 previewImage.setImageDrawable(null);
                                 if (new File(filePath).delete()) {
                                     String toastMessage = "Deleted";
                                     Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                                 }
                             } else{
                                 String toastMessage = "Unable to delete";
                                 Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                         }
                         discardButton.setVisibility(View.GONE);
                         saveButton.setVisibility(View.GONE);
                     }
                 });
                     previewImage.setVisibility(View.VISIBLE);
                     Bitmap myBitmap = decodeFile(new File(filePath));
                     previewImage.setImageBitmap(myBitmap);
                 }
                 break;

             case(VID_CODE):
                 previewVideo.setVisibility(View.VISIBLE);
                 if(resultCode == Activity.RESULT_OK){
                     filePath = data.getStringExtra("videoPath");
                     previewVideo.setVideoPath(filePath);
                     previewVideo.start();
                     //Listener for discarding the taken media file
                     discardButton.setOnClickListener(new View.OnClickListener() {
                         public void onClick(View v) {
                             File videoFile = new File(filePath);
                             if (videoFile.exists()) {
                                 previewImage.setImageDrawable(null);
                                 if (videoFile.delete()) {
                                     String toastMessage = "Deleted";
                                     Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                                 }
                             } else{
                                 String toastMessage = "Unable to delete";
                                 Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                             }
                             previewVideo.setVisibility(View.GONE);
                             discardButton.setVisibility(View.GONE);
                             saveButton.setVisibility(View.GONE);
                         }
                     });
                     Bitmap thumbnailBitmap;
                     previewImage.setVisibility(View.VISIBLE);
                     BitmapFactory.Options options=new BitmapFactory.Options();
                     options.inSampleSize = 1;
                     thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(filePath,
                             MediaStore.Video.Thumbnails.MICRO_KIND);
//                     Bitmap myBitmap = decodeFile(new File(filePath));
                     previewImage.setImageBitmap(thumbnailBitmap);
                 }
                 break;
         }

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
         String andrewID = "jravisan";
         //Timestamp
         String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
         // Device model
         String PhoneModel = android.os.Build.MODEL;
         // Android version
         String AndroidVersion = android.os.Build.VERSION.RELEASE;
         //Android API Level
         int APILevel = (android.os.Build.VERSION.SDK_INT);

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