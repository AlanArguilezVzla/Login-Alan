package com.ut.login.Activities.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ut.login.Activities.FirebaseList;
import com.ut.login.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseTest extends Fragment {

    View view;
    //FusedLocationProviderClient fusedLocationClient;

    double latitude = 0, longitude = 0;

    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;

    Button btnSendCoordinates;

    Button btngoToActivity;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String TAG = "firebaseTest";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_firebase_test, container, false);

        btnSendCoordinates = view.findViewById(R.id.btnSendCoordinates);

        btnSendCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCoordinates();
            }
        });

        btngoToActivity = view.findViewById(R.id.btngoToActivity);

        btngoToActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendToActivity();
            }
        });

        //Inicializndo
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null){
                    return;
                }

                for (Location location : locationResult.getLocations()){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Toast.makeText(getContext(),
                            "" + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();

                    Log.d("locationTest", "" + latitude + ", " + longitude);

                }
            }
        };

        return view;
    }

    public void SendToActivity(){
        Intent intent = new Intent(getContext(), FirebaseList.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopLocationCallback();
    }

    private void stopLocationCallback() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void checkPermission(){
        int permCode = 120;

        String[] perms = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        int accessFineLocation = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        int accessCoarseLocation = getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED && accessCoarseLocation == PackageManager.PERMISSION_GRANTED){
            checkGPSSensor();
        } else {
            requestPermissions(perms, permCode);
        }
    }

    private void  checkGPSSensor(){
        LocationManager locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            getCoordinates();
        } else {
            AlertNoGPS();
        }
    }

    private void getCoordinates() {
        if (ActivityCompat.checkSelfPermission(getContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        } else {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(28000);
            locationRequest.setFastestInterval(10000);


            fusedLocationClient.requestLocationUpdates(
              locationRequest,
              locationCallback,
              Looper.getMainLooper()
            );
        }
    }

    private void AlertNoGPS() {
        //Inicializacion de AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //Titulo
        builder.setTitle("Solicitud de permisos");

        //Mensaje
        builder.setMessage("Se requieren permisos de ubicacion para obtener coordenadas. ");

        //Boton acptar
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        //Boton Cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //Crear alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void sendCoordinates(){
        // Crear la estructura
        Map<String, Object> coordinate = new HashMap<>();
        coordinate.put("latitude", latitude);
        coordinate.put("longitude", longitude);
        coordinate.put("date", new Date());

        db.collection("Coordinates")
                .add(coordinate)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        Toast.makeText(getContext(), "ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);

                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}