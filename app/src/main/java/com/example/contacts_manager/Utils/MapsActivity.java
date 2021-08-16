package com.example.contacts_manager.Utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contacts_manager.Activities.AddContact;
import com.example.contacts_manager.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {



    private static final String TAG = "MapActivity";




    private GoogleMap mMap;
    private Geocoder geocoder;
    String currL = "";
    Button done;
    String streetAddress = "";
    String latLong = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        done = findViewById(R.id.btn_mapDone);

        currL = getIntent().getStringExtra("latLong");



        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addLocation = latLong;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("address", addLocation);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);

        String[] llParse = currL.split(",");
        double lat = Double.parseDouble(llParse[0]);
        double lon = Double.parseDouble(llParse[1]);
        LatLng startLoc = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(startLoc).title("Starting Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLoc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15.0f));

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        Log.d(TAG, "onMapLongClick: " + latLng.toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            this.latLong = latLng.latitude + "," + latLng.longitude;

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                streetAddress = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions().position(latLng).title(streetAddress).draggable(true));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        Log.d(TAG, "onMarkerDragStart: ");
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        Log.d(TAG, "onMarkerDrag: ");
    }

    @Override
    public void onMarkerDragEnd(@NonNull  Marker marker) {
        Log.d(TAG, "onMarkerDragEnd: ");
        LatLng latLng = marker.getPosition();
        this.latLong = latLng.latitude + "," + latLng.longitude;

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                streetAddress = address.getAddressLine(0);
                marker.setTitle(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mGps.setVisibility(View.VISIBLE);
            }
        });

        getDeviceLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {

            if (mLocationPermissionsGranted) {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {

                        if (location != null) {
                            mlastlocation = location;
                            double a = location.getLatitude();
                            double b = location.getLongitude();
                            latLng = new LatLng(a, b);

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(a, b), 15.0f));
                            //rotatemarker(mPositionMarker, 360, mMap);
                            mGps.setVisibility(View.GONE);
                            pickupmarker.setVisibility(View.VISIBLE);
                            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                                @Override
                                public void onCameraIdle() {
                                    center = mMap.getCameraPosition().target;
                                    try {
                                        pickup.setText(getStringAddress(new MarkerOptions().position(center).getPosition().latitude,
                                                new MarkerOptions().position(center).getPosition().longitude));
                                        pickuplatlng=new LatLng(center.latitude,center.longitude);
                                        donelayout.setVisibility(View.VISIBLE);
                                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mGps.getLayoutParams();
                                        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            //dialog12.cancel();
                        } else {
//                            buildAlertMessageNogps();
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    private void init() {

        mGps = findViewById(R.id.mylocation);
        wheretolayout = findViewById(R.id.where_layout);
        donelayout = findViewById(R.id.donelayout);
        //pickcard = view.findViewById(R.id.picup_layout);
        pickandendlocationCard = findViewById(R.id.pickupandend);
//        fragment_bottom_sheet = findViewById(R.id.fragment_bottom_sheet);
        drivername = findViewById(R.id.drivername);
        driverphoneno = findViewById(R.id.driverphoneno);
        rideprice = findViewById(R.id.rideprice);
        btn_confirm = findViewById(R.id.btn_confirm);
//        sheetBehavior=BottomSheetBehavior.from(fragment_bottom_sheet);
        mGps.setVisibility(View.VISIBLE);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        img_menu = findViewById(R.id.img_menu);
//        getLocationPermission();////////////////getting location





        whereto = findViewById(R.id.whereto);
        pickup = findViewById(R.id.pickup);
        wheretomarker = findViewById(R.id.wheretomarker);
        pikCard=findViewById(R.id.pickupandend);
        pickupmarker = findViewById(R.id.pickupmarker);
        //whereto.setKeyListener(null);
        whereto.setClickable(true);
        whereto.setFocusable(false);
        whereto.setLongClickable(false);
        pickup.setClickable(true);
        pickup.setFocusable(false);
        pickup.setLongClickable(false);
        //whereto.setEnabled(false);

        initMap();



    }
    ///////initilizing map
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void click() {

        if (mLocationPermissionsGranted) {
            mGps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn_Location();
//                    dialog12.cancel();
                }
            });
        }

//        YoYo.with(Techniques.BounceInUp)
//                .playOn(pickandendlocationCard);
        whereto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wheretomarker.setVisibility(View.VISIBLE);
                pickupmarker.setVisibility(View.GONE);
                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        center = mMap.getCameraPosition().target;
                        try {
                            whereto.setText(getStringAddress(new MarkerOptions().position(center).getPosition().latitude,
                                    new MarkerOptions().position(center).getPosition().longitude));
                            donelayout.setVisibility(View.VISIBLE);
                            wheretolatlng=new LatLng(center.latitude,center.longitude);
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mGps.getLayoutParams();
                            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wheretomarker.setVisibility(View.GONE);
                pickupmarker.setVisibility(View.VISIBLE);
                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        center = mMap.getCameraPosition().target;
                        try {
                            pickup.setText(getStringAddress(new MarkerOptions().position(center).getPosition().latitude,
                                    new MarkerOptions().position(center).getPosition().longitude));
                            pickuplatlng=new LatLng(center.latitude,center.longitude);
//                            donelayout.setVisibility(View.VISIBLE);
//                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mGps.getLayoutParams();
//                            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        donelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



//                getDirection(pickuplatlng,wheretolatlng);
//                pikCard.setVisibility(View.GONE);


            }
        });
    }

    public String getStringAddress(Double lat, Double lng) throws IOException {


        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, lng, 1);

        address = addresses.get(0).getAddressLine(0);


        return address;
    }

    private void btn_Location() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        try {
            if (mLocationPermissionsGranted) {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        if (location != null) {

                            double a = location.getLatitude();
                            double b = location.getLongitude();
                            latLng = new LatLng(a, b);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(a, b), DEFAULT_ZOOM));
                            mGps.setVisibility(View.GONE);
                        } else {
//                            buildAlertMessageNogps();
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }*/
