package com.example.adamk_000.noteapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.adamk_000.noteapp.TakePhoto.*;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    public static final String NOTE_ID_EXTRA = "com.example.adamk_000.noteapp.Note Identifier";
    public static final String NOTE_TITLE_EXTRA = "com.example.adamk_000.noteapp.Note Title";
    public static final String NOTE_MESSAGE_EXTRA = "com.example.adamk_000.noteapp.Note Message";
    public static final String NOTE_CATEGORY_EXTRA = "com.example.adamk_000.noteapp.Note Category";

    private static File path;

    public static String mLastLatitude;
    public static String mLastLongitude;
    private String name;
    private String nameBefore;
    private int counter;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    private Intent mapIntent;
    private ArrayList<String> names = new ArrayList<>();
    private static File thumbnails;


    public static File getPath() {
        return path;
    }

    public static void setPath(File path) {
        MainActivity.path = path;
    }

    public static File getThumbnails() {
        return thumbnails;
    }

    public static void setThumbnails(File thumbnails) {
        MainActivity.thumbnails = thumbnails;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapIntent = new Intent(MainActivity.this, Main2ActivityMaps.class);
        createFolder();

        FloatingActionButton map = (FloatingActionButton) findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapIntent.putExtra("names", names);
                startActivity(mapIntent);

            }
        });

        FloatingActionButton cam = (FloatingActionButton) findViewById(R.id.cam);
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(view);
            }
        });

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void createFolder() {
        setPath(new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/turista/"));//save file on sd card to folder /DCIM/awesome
        getPath().mkdirs();
        mapIntent.putExtra("path", getPath());
        setThumbnails(new File(getPath() + "/.thumbnails/"));
        getThumbnails().mkdirs();

    }


    public void click(View v) {
        startActivityForResult(TakePhoto.preparePhotoIntent(), TakePhoto.TAKE_PICTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean everythingOK = photoResults(requestCode);
        if (everythingOK) {
            restartIntent(this);
            Toast.makeText(this, "Photo taken! :)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "There is some problem with the taken photo! :(", Toast.LENGTH_LONG).show();
        }
    }

    public static void readNames(File path, ArrayList<String> arrayList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.getPath() + "/names.txt"))) {
            String add = reader.readLine();
            while (add != null) {
                arrayList.add(add);
                add = reader.readLine();
            }
        } catch (IOException e) {
        }
    }

    public String getName() {
        Calendar c = Calendar.getInstance();
        Date time = c.getTime();

        name = String.valueOf(time.getTime());
        if (name == nameBefore) {
            counter++;
            name = time.toString() + "_" + counter;
        } else {
            counter = 0;
        }
        nameBefore = name;
        return name;
    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLastLatitude = String.valueOf(mLastLocation.getLatitude());
            mLastLongitude = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
