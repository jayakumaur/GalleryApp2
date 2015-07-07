package com.cmu.jayakumaur.galleryapp;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;

/**
 * Created by Jayakumaur on 25-06-2015.
 * Class to contain the logic that goes in to display
 * the andrew ID, time and the sensors
 */

public class Tab1Activity extends Fragment {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private String[] filePathStrings;
    private String[] fileNameStrings;
    private File[] listFile;
    private Integer[] fileNameExtensions;

    File file;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.gallery,container,false);

        this.loadImages();
        gridView = (GridView) v.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(getActivity(), fileNameStrings ,filePathStrings, fileNameExtensions);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Intent intent;
                //Create intent based on the file type.
                Log.d("----------->"+fileNameStrings[position]+"----",fileNameExtensions[position].toString());
                if(fileNameExtensions[position] == 3){
                    intent = new Intent(getActivity(),VideoActivity.class);
                }
                else {
                    intent = new Intent(getActivity(), ImageActivity.class);
                }

                //Addition of file details to the intent.
                intent.putExtra("filepath",filePathStrings);
                intent.putExtra("filename",fileNameStrings);
                intent.putExtra("position",position);

                //Start the respective details activity.
                startActivity(intent);
            }
        });
        return v;
    }

    /* Method - loadImages
     * Method to load the image and video files'
     * Metadata into the respective arraylists.
     * The name, filepath and extension indices are retrieved.
     * The extension index is used to distinguish
     * between an image and a video.
     */
    public void loadImages(){
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };

        // Return only video and image metadata.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC" // Sort order.
        );

        Cursor cursor = cursorLoader.loadInBackground();
        int count = cursor.getCount();
        // Create a String array for FilePathStrings
        filePathStrings = new String[count];
        // Create a String array for FileNameStrings
        fileNameStrings = new String[count];
        // Create a String array for FileExtension
        fileNameExtensions = new Integer[count];


        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int fileNameIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
            int extensionIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);

            // Get the path of the image file
            filePathStrings[i] = cursor.getString(dataColumnIndex);
            // Get the name image file
            fileNameStrings[i] = cursor.getString(fileNameIndex);
            // Get the name image extension
            fileNameExtensions[i] = cursor.getInt(extensionIndex);

        }
    }
}