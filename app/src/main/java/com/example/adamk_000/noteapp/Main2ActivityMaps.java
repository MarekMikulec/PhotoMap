package com.example.adamk_000.noteapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Main2ActivityMaps extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener {


    //private static final int SELECT_IMAGE = 15;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates;
    double currentLat;
    double currentLng;
    LatLng position;






    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    


    int i = 0;
    private String mLastLatitude;
    private String mLastLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_activity_maps);

        FloatingActionButton gal = (FloatingActionButton) findViewById(R.id.gal);
        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2ActivityMaps.this, MainActivity.class);
                startActivity(intent);

            }
        });

        FloatingActionButton cam = (FloatingActionButton) findViewById(R.id.cam);
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(view);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        createLocationRequest();
    }




    public void click(View v) {
        //Launch camera
        startActivityForResult(TakePhoto.preparePhotoIntent(), TakePhoto.TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean everthingOK = TakePhoto.photoResults(requestCode);
        if (everthingOK){
            TakePhoto.restartIntent(this);
            Toast.makeText(this, "Photo taken! :)",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"There is some problem with the taken photo! :(",Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop listening for updates
        stopLocationUpdates();
        // Disconnect from the API
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If we are connected to the Google API, but not listening for updates yet, we should begin listening
        // for updates.
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't listen for updates when the Activity is not active
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        // Handle some permission stuff, this is auto-generated.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Start listening for updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        // Set the value to true, so we know that we are listening for updates
        mRequestingLocationUpdates = true;

        mMap.setMyLocationEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
    }

    private void stopLocationUpdates() {
        // Stop listening for location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        // Set the value to false so we know if we have to start listening again
        mRequestingLocationUpdates = false;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new PictureAndTextInfoWindowAdapter(this));
        mMap.setOnInfoWindowClickListener(this);
        createAllPins();
    }

    public void createAllPins() {
        ArrayList<String> names = new ArrayList<>();
        MainActivity.readNames(MainActivity.getPath(), names);
        if (names != null && MainActivity.getPath() != null) {
            for (String name : names) {
                String[] information = readTextInformation(name, MainActivity.getPath());
                if (information != null) {
                    String nameOfPhotoWithTxt = information[0];
                    String nameWithoutTxt = String.copyValueOf(nameOfPhotoWithTxt.toCharArray(), 0, nameOfPhotoWithTxt.length() - 4);
                    double latitude = Double.valueOf(information[1]);
                    double longitude = Double.valueOf(information[2]);
                    createPin(nameWithoutTxt, latitude, longitude);
                }
            }
        } else {
            Toast.makeText(this, "Path or names doesn't found!", Toast.LENGTH_LONG).show();
        }
    }

    private void createPin(String title, double latitude, double longitude) {
        LatLng photoPosition = new LatLng(latitude, longitude);
        Marker marker = mMap.addMarker(new MarkerOptions().position(photoPosition).title(title));
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(photoPosition));

    }

    public String[] readTextInformation(String fileName, File path) {
        String fullPath = path.getPath() + "/" + fileName + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
            String[] information = new String[3];
            int i = 0;
            while (i < 3) {
                information[i] = reader.readLine();
                i++;
            }
            return information;
        } catch (IOException e) {
            Toast.makeText(this, "Photo does not exist!", Toast.LENGTH_LONG).show();
            return null;
        }
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
        if (!mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // Retrieve the new location
        mLastLocation = location;

        currentLat = mLastLocation.getLatitude();
        currentLng = mLastLocation.getLongitude();

        position = new LatLng(currentLat, currentLng);

        // This way camera will not still move
        if (i < 1) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            i += 1;
        } else {
            return;
        }
    }





    private void launchNoteDetailActivity(String title, String message) {
        //Grab the note information associated with whatever note item we clicked on


        //Create a new intent that launches our note activity
        Intent intent = new Intent(Main2ActivityMaps.this, NoteDetailActivity.class);

        //pass along the info
        intent.putExtra(MainActivity.NOTE_TITLE_EXTRA, title);
        intent.putExtra(MainActivity.NOTE_MESSAGE_EXTRA, message);
        intent.putExtra(MainActivity.NOTE_CATEGORY_EXTRA, Note.Category.PERSONAL);

        startActivity(intent);

    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        launchNoteDetailActivity(marker.getTitle(),null);
    }
}
