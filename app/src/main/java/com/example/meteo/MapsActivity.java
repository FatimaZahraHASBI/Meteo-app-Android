package com.example.meteo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    // l'identifiant de l'appel de l'autorisation
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Fournisseur d'emplacement fusionné pour récupérer le dernier emplacement connu de l'appareil
        // API de localisation des services Google Play
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void fetchLocation() {
        // Vérifier est ce que l'application est autorisée à accéder à la localisation de l'appareil
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demande d'autorisation, un pop up s'afficher pour accepter ou refuser la demande d'autorisation // le résultat de cette demande est renvoyé à la méthode onRequestPermissionsResult qui se
            //chargera de la suite
            ActivityCompat.requestPermissions(this, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
            return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Bundle b= getIntent().getExtras();
        String valuue = b.getString("name");
        double longitude = b.getDouble("longitude");
        double latitude = b.getDouble("latitude");

        mMap = googleMap;

        LatLng city = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(city).title(valuue+", longitude: " + longitude +", latitude: "+latitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(city));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city,10));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(city);
        circleOptions.radius(700);
        circleOptions.fillColor(Color.TRANSPARENT);
        circleOptions.strokeWidth(6);
        mMap.addCircle(circleOptions);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            final GoogleMap gmap=mMap;
            @Override
            public void onMapClick(LatLng latLng) {
                gmap.addMarker(new MarkerOptions().position(latLng));
                Toast.makeText(MapsActivity.this, latLng.latitude+""
                        +latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // Dans le cas lorsque vous cliquez sur le bouton "autoriser" du pop up ,
            // il y aura un deuxième appel de la méthode
            // fetchLocation() pour obtenir la dernière localisation
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

}